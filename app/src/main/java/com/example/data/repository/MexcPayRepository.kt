package com.example.data.repository

import com.example.data.local.MexcPayDao
import com.example.data.model.AdminOrderEntity
import com.example.data.model.BonusSettingEntity
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.data.model.PaymentSubmissionEntity
import com.example.data.model.PaymentVerificationState
import com.example.data.model.TransactionType
import com.example.data.model.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

sealed class UtrSubmissionResult {
    data class RecordedPendingVerification(val submission: PaymentSubmissionEntity) : UtrSubmissionResult()
    data class ValidationError(val message: String) : UtrSubmissionResult()
}

class MexcPayRepository(private val dao: MexcPayDao) {

    val allOrders: Flow<List<AdminOrderEntity>> = dao.observeAllOrders()
    val allSubmissions: Flow<List<PaymentSubmissionEntity>> = dao.observeAllSubmissions()
    val allTransactions: Flow<List<WalletTransactionEntity>> = dao.observeAllTransactions()
    val bonusSettings: Flow<List<BonusSettingEntity>> = dao.observeBonusSettings()

    fun observeOrderById(orderId: String): Flow<AdminOrderEntity?> = dao.observeOrderById(orderId)

    fun observeSubmissionsForOrder(orderId: String): Flow<List<PaymentSubmissionEntity>> =
        dao.observeSubmissionsForOrder(orderId)

    suspend fun ensureSampleDataSeeded() {
        val count = dao.getOrdersCount()
        if (count == 0) {
            seedSampleSandboxData()
        }
    }

    suspend fun resetSampleSandboxData() {
        dao.clearOrders()
        dao.clearSubmissions()
        dao.clearTransactions()
        dao.clearBonusSettings()
        seedSampleSandboxData()
    }

    /**
     * Validates and records a UTR submission.
     *
     * CRITICAL SECURITY REQUIREMENT:
     * - Does NOT perform fake payment verification.
     * - Does NOT credit the user's verified wallet balance.
     * - Records the submission strictly as [PaymentVerificationState.PENDING_BACKEND_VERIFICATION]
     *   and logs an unverified audit entry in the transaction history with `affectsVerifiedBalance = false`.
     */
    suspend fun submitUtrForOrder(
        orderId: String,
        rawUtr: String,
        rawPayerUpi: String,
        remarks: String
    ): UtrSubmissionResult {
        val cleanedUtr = rawUtr.trim()
        if (!cleanedUtr.matches(Regex("^\\d{12}$"))) {
            return UtrSubmissionResult.ValidationError(
                "UTR number must be an exact 12-digit numeric NPCI bank reference (e.g., 428190384712)."
            )
        }

        val cleanedUpi = rawPayerUpi.trim()
        if (cleanedUpi.isNotEmpty() && !cleanedUpi.matches(Regex("^[a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64}$"))) {
            return UtrSubmissionResult.ValidationError(
                "Please enter a valid sender UPI ID format (e.g., name@bank) or leave optional handle blank."
            )
        }

        val existingDuplicate = dao.findSubmissionByUtr(cleanedUtr)
        if (existingDuplicate != null) {
            return UtrSubmissionResult.ValidationError(
                "UTR $cleanedUtr has already been submitted (Order ${existingDuplicate.orderId}). Duplicate UTR claims are blocked."
            )
        }

        val order = dao.getOrderById(orderId)
            ?: return UtrSubmissionResult.ValidationError("Order $orderId was not found in local desk cache.")

        val now = System.currentTimeMillis()
        val submissionId = "SUB-${UUID.randomUUID().toString().take(8).uppercase()}"

        val submission = PaymentSubmissionEntity(
            submissionId = submissionId,
            orderId = order.orderId,
            orderType = order.orderType,
            submittedUtrNumber = cleanedUtr,
            claimedInrAmount = order.inrAmount,
            expectedBonusInr = order.bonusInrAmount,
            payerUpiHandle = cleanedUpi.ifEmpty { "Not specified" },
            remarks = remarks.trim().ifEmpty { "Submitted via Android client awaiting backend webhook audit" },
            verificationState = PaymentVerificationState.PENDING_BACKEND_VERIFICATION.name,
            submittedAtEpochMs = now,
            adminAuditNote = "Awaiting authorized server-side bank statement reconciliation. Wallet balance is NOT credited until verified payment records are confirmed.",
            isSampleData = false
        )

        dao.insertSubmission(submission)

        // Update order status to reflect that a UTR claim is pending admin audit
        val updatedOrder = order.copy(
            status = OrderStatus.UTR_SUBMITTED_PENDING_ADMIN.name
        )
        dao.updateOrder(updatedOrder)

        // Log an unverified ledger record that explicitly does NOT credit the verified wallet balance
        val pendingTx = WalletTransactionEntity(
            transactionId = "TX-PENDING-${cleanedUtr.takeLast(6)}",
            orderId = order.orderId,
            title = "UTR Claim Submitted (${order.orderId})",
            subtitle = "PENDING BACKEND VERIFICATION • Balance NOT credited",
            amountInr = order.inrAmount,
            bonusAmountInr = order.bonusInrAmount,
            transactionType = TransactionType.UTR_CLAIM_PENDING.name,
            verificationStatus = "PENDING_BACKEND_VERIFICATION",
            utrReference = cleanedUtr,
            timestampEpochMs = now,
            isSampleData = false,
            affectsVerifiedBalance = false
        )
        dao.insertTransaction(pendingTx)

        return UtrSubmissionResult.RecordedPendingVerification(submission)
    }

    private suspend fun seedSampleSandboxData() {
        val now = System.currentTimeMillis()
        val hourMs = 3_600_000L

        val sampleBonusSettings = listOf(
            BonusSettingEntity(
                tierId = "TIER-BUY-1",
                tierName = "Standard INR Liquidity Tier",
                orderType = OrderType.BUY_INR.name,
                minAmountInr = 5_000.0,
                maxAmountInr = 24_999.0,
                bonusPercentage = 2.5,
                isActive = true,
                configuredByAdmin = "ADMIN-DESK-01 (Sample)",
                lastUpdatedEpochMs = now - 12 * hourMs,
                isSampleConfig = true
            ),
            BonusSettingEntity(
                tierId = "TIER-BUY-2",
                tierName = "Priority OTC Volume Tier",
                orderType = OrderType.BUY_INR.name,
                minAmountInr = 25_000.0,
                maxAmountInr = 99_999.0,
                bonusPercentage = 3.8,
                isActive = true,
                configuredByAdmin = "ADMIN-DESK-01 (Sample)",
                lastUpdatedEpochMs = now - 12 * hourMs,
                isSampleConfig = true
            ),
            BonusSettingEntity(
                tierId = "TIER-BUY-3",
                tierName = "Institutional Desk Tier",
                orderType = OrderType.BUY_INR.name,
                minAmountInr = 100_000.0,
                maxAmountInr = 500_000.0,
                bonusPercentage = 4.6,
                isActive = true,
                configuredByAdmin = "ADMIN-DESK-02 (Sample)",
                lastUpdatedEpochMs = now - 6 * hourMs,
                isSampleConfig = true
            ),
            BonusSettingEntity(
                tierId = "TIER-SELL-1",
                tierName = "Express INR Settlement Tier",
                orderType = OrderType.SELL_INR.name,
                minAmountInr = 10_000.0,
                maxAmountInr = 200_000.0,
                bonusPercentage = 1.8,
                isActive = true,
                configuredByAdmin = "ADMIN-TREASURY-04 (Sample)",
                lastUpdatedEpochMs = now - 8 * hourMs,
                isSampleConfig = true
            )
        )

        val sampleOrders = listOf(
            AdminOrderEntity(
                orderId = "MXP-ORD-9041",
                orderType = OrderType.BUY_INR.name,
                inrAmount = 25_000.0,
                bonusPercentage = 3.8,
                bonusInrAmount = 950.0,
                totalReceivableInr = 25_950.0,
                availableQuotaInr = 175_000.0,
                totalQuotaInr = 250_000.0,
                minTicketInr = 25_000.0,
                maxTicketInr = 25_000.0,
                status = OrderStatus.OPEN_AVAILABLE.name,
                createdByAdminId = "ADMIN-DESK-01",
                adminDeskName = "Mumbai Prime OTC Desk (Sample)",
                adminBatchCode = "BATCH-IN-2026-A1",
                assignedPlaceholderUpiId = "mexcpay.sandbox.desk01@okaxis",
                beneficiaryDisplayName = "MEXC PAY SANDBOX ESCROW 01 (SAMPLE)",
                bankReferenceNote = "SAMPLE ONLY - DO NOT SEND REAL FUNDS",
                settlementWindowMinutes = 15,
                isSampleData = true,
                createdAtEpochMs = now - 45 * 60_000L
            ),
            AdminOrderEntity(
                orderId = "MXP-ORD-9042",
                orderType = OrderType.BUY_INR.name,
                inrAmount = 50_000.0,
                bonusPercentage = 4.2,
                bonusInrAmount = 2_100.0,
                totalReceivableInr = 52_100.0,
                availableQuotaInr = 350_000.0,
                totalQuotaInr = 500_000.0,
                minTicketInr = 50_000.0,
                maxTicketInr = 50_000.0,
                status = OrderStatus.OPEN_AVAILABLE.name,
                createdByAdminId = "ADMIN-DESK-02",
                adminDeskName = "Bengaluru Liquidity Node (Sample)",
                adminBatchCode = "BATCH-IN-2026-A2",
                assignedPlaceholderUpiId = "mexcpay.sandbox.desk02@icici",
                beneficiaryDisplayName = "MEXC PAY SANDBOX ESCROW 02 (SAMPLE)",
                bankReferenceNote = "SAMPLE ONLY - PLACEHOLDER UPI ID",
                settlementWindowMinutes = 20,
                isSampleData = true,
                createdAtEpochMs = now - 90 * 60_000L
            ),
            AdminOrderEntity(
                orderId = "MXP-ORD-9043",
                orderType = OrderType.BUY_INR.name,
                inrAmount = 10_000.0,
                bonusPercentage = 2.5,
                bonusInrAmount = 250.0,
                totalReceivableInr = 10_250.0,
                availableQuotaInr = 80_000.0,
                totalQuotaInr = 100_000.0,
                minTicketInr = 10_000.0,
                maxTicketInr = 10_000.0,
                status = OrderStatus.OPEN_AVAILABLE.name,
                createdByAdminId = "ADMIN-DESK-01",
                adminDeskName = "Retail Fast-Track Desk (Sample)",
                adminBatchCode = "BATCH-IN-2026-R4",
                assignedPlaceholderUpiId = "mexcpay.sandbox.retail@hdfcbank",
                beneficiaryDisplayName = "MEXC PAY SANDBOX RETAIL (SAMPLE)",
                bankReferenceNote = "SAMPLE ONLY - SANDBOX TEST ORDER",
                settlementWindowMinutes = 15,
                isSampleData = true,
                createdAtEpochMs = now - 2 * hourMs
            ),
            AdminOrderEntity(
                orderId = "MXP-ORD-9044",
                orderType = OrderType.SELL_INR.name,
                inrAmount = 35_000.0,
                bonusPercentage = 1.8,
                bonusInrAmount = 630.0,
                totalReceivableInr = 35_630.0,
                availableQuotaInr = 140_000.0,
                totalQuotaInr = 210_000.0,
                minTicketInr = 35_000.0,
                maxTicketInr = 35_000.0,
                status = OrderStatus.OPEN_AVAILABLE.name,
                createdByAdminId = "ADMIN-TREASURY-04",
                adminDeskName = "Settlement Payout Desk (Sample)",
                adminBatchCode = "BATCH-OUT-2026-S1",
                assignedPlaceholderUpiId = "mexcpay.sandbox.payout@yesbank",
                beneficiaryDisplayName = "MEXC PAY PAYOUT POOL (SAMPLE)",
                bankReferenceNote = "SAMPLE SELL INR LIQUIDITY POOL",
                settlementWindowMinutes = 30,
                isSampleData = true,
                createdAtEpochMs = now - 3 * hourMs
            ),
            AdminOrderEntity(
                orderId = "MXP-ORD-9045",
                orderType = OrderType.SELL_INR.name,
                inrAmount = 75_000.0,
                bonusPercentage = 2.2,
                bonusInrAmount = 1_650.0,
                totalReceivableInr = 76_650.0,
                availableQuotaInr = 225_000.0,
                totalQuotaInr = 300_000.0,
                minTicketInr = 75_000.0,
                maxTicketInr = 75_000.0,
                status = OrderStatus.OPEN_AVAILABLE.name,
                createdByAdminId = "ADMIN-TREASURY-04",
                adminDeskName = "Institutional Settlement Pool (Sample)",
                adminBatchCode = "BATCH-OUT-2026-S2",
                assignedPlaceholderUpiId = "mexcpay.sandbox.inst@kotak",
                beneficiaryDisplayName = "MEXC PAY INST POOL (SAMPLE)",
                bankReferenceNote = "SAMPLE SELL INR ORDER",
                settlementWindowMinutes = 30,
                isSampleData = true,
                createdAtEpochMs = now - 4 * hourMs
            ),
            AdminOrderEntity(
                orderId = "MXP-ORD-8998",
                orderType = OrderType.BUY_INR.name,
                inrAmount = 15_000.0,
                bonusPercentage = 2.8,
                bonusInrAmount = 420.0,
                totalReceivableInr = 15_420.0,
                availableQuotaInr = 45_000.0,
                totalQuotaInr = 150_000.0,
                minTicketInr = 15_000.0,
                maxTicketInr = 15_000.0,
                status = OrderStatus.UTR_SUBMITTED_PENDING_ADMIN.name,
                createdByAdminId = "ADMIN-DESK-01",
                adminDeskName = "Mumbai Prime OTC Desk (Sample)",
                adminBatchCode = "BATCH-IN-2026-A0",
                assignedPlaceholderUpiId = "mexcpay.sandbox.desk01@okaxis",
                beneficiaryDisplayName = "MEXC PAY SANDBOX ESCROW 01 (SAMPLE)",
                bankReferenceNote = "SAMPLE ONLY - PENDING ADMIN AUDIT",
                settlementWindowMinutes = 15,
                isSampleData = true,
                createdAtEpochMs = now - 6 * hourMs
            ),
            AdminOrderEntity(
                orderId = "MXP-ORD-8950",
                orderType = OrderType.BUY_INR.name,
                inrAmount = 40_000.0,
                bonusPercentage = 3.8,
                bonusInrAmount = 1_520.0,
                totalReceivableInr = 41_520.0,
                availableQuotaInr = 0.0,
                totalQuotaInr = 160_000.0,
                minTicketInr = 40_000.0,
                maxTicketInr = 40_000.0,
                status = OrderStatus.VERIFIED_SETTLED.name,
                createdByAdminId = "ADMIN-DESK-02",
                adminDeskName = "Bengaluru Liquidity Node (Sample)",
                adminBatchCode = "BATCH-IN-2026-V9",
                assignedPlaceholderUpiId = "mexcpay.sandbox.desk02@icici",
                beneficiaryDisplayName = "MEXC PAY SANDBOX ESCROW 02 (SAMPLE)",
                bankReferenceNote = "SAMPLE HISTORICAL VERIFIED ORDER",
                settlementWindowMinutes = 20,
                isSampleData = true,
                createdAtEpochMs = now - 26 * hourMs
            )
        )

        val sampleSubmissions = listOf(
            PaymentSubmissionEntity(
                submissionId = "SUB-SMP-8998",
                orderId = "MXP-ORD-8998",
                orderType = OrderType.BUY_INR.name,
                submittedUtrNumber = "612948372019",
                claimedInrAmount = 15_000.0,
                expectedBonusInr = 420.0,
                payerUpiHandle = "arjun.mehta.sample@okicici",
                remarks = "Sample UTR submission demonstrating unverified queue state",
                verificationState = PaymentVerificationState.PENDING_BACKEND_VERIFICATION.name,
                submittedAtEpochMs = now - 5 * hourMs,
                adminAuditNote = "Sample submission waiting for backend bank webhook match. Not credited to verified balance.",
                isSampleData = true
            ),
            PaymentSubmissionEntity(
                submissionId = "SUB-SMP-8950",
                orderId = "MXP-ORD-8950",
                orderType = OrderType.BUY_INR.name,
                submittedUtrNumber = "509384716254",
                claimedInrAmount = 40_000.0,
                expectedBonusInr = 1_520.0,
                payerUpiHandle = "arjun.mehta.sample@okicici",
                remarks = "Historical sample settlement verified by admin backend",
                verificationState = PaymentVerificationState.BACKEND_VERIFIED.name,
                submittedAtEpochMs = now - 25 * hourMs,
                adminAuditNote = "Sample historical record reconciled by Admin-Desk-02.",
                isSampleData = true
            )
        )

        val sampleTransactions = listOf(
            WalletTransactionEntity(
                transactionId = "TX-SMP-1003",
                orderId = "MXP-ORD-8998",
                title = "UTR Claim Submitted (Sample)",
                subtitle = "PENDING BACKEND VERIFICATION • Balance NOT Credited",
                amountInr = 15_000.0,
                bonusAmountInr = 420.0,
                transactionType = TransactionType.UTR_CLAIM_PENDING.name,
                verificationStatus = "PENDING_BACKEND_VERIFICATION",
                utrReference = "612948372019",
                timestampEpochMs = now - 5 * hourMs,
                isSampleData = true,
                affectsVerifiedBalance = false
            ),
            WalletTransactionEntity(
                transactionId = "TX-SMP-1002",
                orderId = "MXP-ORD-8950",
                title = "OTC Bonus Reward (Sample Verified)",
                subtitle = "3.8% Tier Bonus • Reconciled by Backend",
                amountInr = 1_520.0,
                bonusAmountInr = 1_520.0,
                transactionType = TransactionType.BONUS_REWARD_CREDIT.name,
                verificationStatus = "SAMPLE_VERIFIED_RECORD",
                utrReference = "509384716254",
                timestampEpochMs = now - 24 * hourMs,
                isSampleData = true,
                affectsVerifiedBalance = true
            ),
            WalletTransactionEntity(
                transactionId = "TX-SMP-1001",
                orderId = "MXP-ORD-8950",
                title = "INR OTC Buy Settlement (Sample Verified)",
                subtitle = "Principal Credited • Admin Batch BATCH-IN-2026-V9",
                amountInr = 40_000.0,
                bonusAmountInr = 0.0,
                transactionType = TransactionType.ORDER_SETTLEMENT_CREDIT.name,
                utrReference = "509384716254",
                verificationStatus = "SAMPLE_VERIFIED_RECORD",
                timestampEpochMs = now - 24 * hourMs - 60_000L,
                isSampleData = true,
                affectsVerifiedBalance = true
            ),
            WalletTransactionEntity(
                transactionId = "TX-SMP-1000",
                orderId = "MXP-ORD-8812",
                title = "INR OTC Sell Payout (Sample Verified)",
                subtitle = "Settled to Linked Bank Account",
                amountInr = -12_000.0,
                bonusAmountInr = 216.0,
                transactionType = TransactionType.SELL_INR_PAYOUT.name,
                utrReference = "498127364510",
                verificationStatus = "SAMPLE_VERIFIED_RECORD",
                timestampEpochMs = now - 48 * hourMs,
                isSampleData = true,
                affectsVerifiedBalance = true
            )
        )

        dao.insertBonusSettings(sampleBonusSettings)
        dao.insertOrders(sampleOrders)
        dao.insertSubmissions(sampleSubmissions)
        dao.insertTransactions(sampleTransactions)
    }
}
