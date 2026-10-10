package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderType {
    BUY_INR,
    SELL_INR
}

enum class OrderStatus(val label: String) {
    OPEN_AVAILABLE("OPEN AVAILABLE"),
    AWAITING_UTR("AWAITING UTR"),
    UTR_SUBMITTED_PENDING_ADMIN("PENDING ADMIN AUDIT"),
    VERIFIED_SETTLED("VERIFIED BY BACKEND"),
    EXPIRED_OR_CLOSED("CLOSED")
}

enum class PaymentVerificationState(val displayLabel: String) {
    PENDING_BACKEND_VERIFICATION("PENDING BACKEND VERIFICATION"),
    UNDER_ADMIN_AUDIT("UNDER ADMIN AUDIT"),
    BACKEND_VERIFIED("BACKEND VERIFIED (SAMPLE)"),
    REJECTED_INVALID_UTR("INVALID UTR REJECTED")
}

enum class TransactionType(val label: String) {
    ORDER_SETTLEMENT_CREDIT("INR OTC Buy Settlement"),
    SELL_INR_PAYOUT("INR OTC Sell Payout"),
    BONUS_REWARD_CREDIT("OTC Tier Bonus Credit"),
    UTR_CLAIM_PENDING("UTR Submitted (Unverified)")
}

enum class DataOriginBadge(val badgeText: String) {
    SAMPLE_SANDBOX_DATA("SAMPLE DATA"),
    LOCAL_UNVERIFIED_SUBMISSION("UNVERIFIED UTR CLAIM")
}

/**
 * Represents an order created by an administrator on the OTC desk.
 * Prepared for Admin Workflow synchronization.
 */
@Entity(tableName = "admin_orders")
data class AdminOrderEntity(
    @PrimaryKey val orderId: String,
    val orderType: String, // OrderType.name
    val inrAmount: Double,
    val bonusPercentage: Double,
    val bonusInrAmount: Double,
    val totalReceivableInr: Double,
    val availableQuotaInr: Double,
    val totalQuotaInr: Double,
    val minTicketInr: Double,
    val maxTicketInr: Double,
    val status: String, // OrderStatus.name
    val createdByAdminId: String,
    val adminDeskName: String,
    val adminBatchCode: String,
    val assignedPlaceholderUpiId: String,
    val beneficiaryDisplayName: String,
    val bankReferenceNote: String,
    val settlementWindowMinutes: Int,
    val isSampleData: Boolean = true,
    val createdAtEpochMs: Long
) {
    val parsedOrderType: OrderType
        get() = runCatching { OrderType.valueOf(orderType) }.getOrDefault(OrderType.BUY_INR)

    val parsedStatus: OrderStatus
        get() = runCatching { OrderStatus.valueOf(status) }.getOrDefault(OrderStatus.OPEN_AVAILABLE)

    val availabilityPercentage: Float
        get() = if (totalQuotaInr > 0) (availableQuotaInr / totalQuotaInr).toFloat().coerceIn(0f, 1f) else 0f
}

/**
 * Represents a user's UTR (Unique Transaction Reference) payment submission.
 * SECURITY REQUIREMENT: Submitting a UTR NEVER credits the wallet balance automatically.
 * It remains in PENDING_BACKEND_VERIFICATION until an authorized backend/admin confirms bank records.
 */
@Entity(tableName = "payment_submissions")
data class PaymentSubmissionEntity(
    @PrimaryKey val submissionId: String,
    val orderId: String,
    val orderType: String,
    val submittedUtrNumber: String,
    val claimedInrAmount: Double,
    val expectedBonusInr: Double,
    val payerUpiHandle: String,
    val remarks: String,
    val verificationState: String, // PaymentVerificationState.name
    val submittedAtEpochMs: Long,
    val adminAuditNote: String,
    val isSampleData: Boolean
) {
    val parsedVerificationState: PaymentVerificationState
        get() = runCatching { PaymentVerificationState.valueOf(verificationState) }
            .getOrDefault(PaymentVerificationState.PENDING_BACKEND_VERIFICATION)
}

/**
 * Represents a wallet ledger transaction record.
 * Clearly distinguishes pre-seeded sample verified records from unverified UTR claims.
 */
@Entity(tableName = "wallet_transactions")
data class WalletTransactionEntity(
    @PrimaryKey val transactionId: String,
    val orderId: String,
    val title: String,
    val subtitle: String,
    val amountInr: Double,
    val bonusAmountInr: Double,
    val transactionType: String, // TransactionType.name
    val verificationStatus: String, // "SAMPLE_VERIFIED_RECORD" or "PENDING_BACKEND_VERIFICATION"
    val utrReference: String,
    val timestampEpochMs: Long,
    val isSampleData: Boolean,
    val affectsVerifiedBalance: Boolean
)

/**
 * Represents administrator-configured bonus settings per tier and order type.
 */
@Entity(tableName = "bonus_settings")
data class BonusSettingEntity(
    @PrimaryKey val tierId: String,
    val tierName: String,
    val orderType: String, // OrderType.name
    val minAmountInr: Double,
    val maxAmountInr: Double,
    val bonusPercentage: Double,
    val isActive: Boolean,
    val configuredByAdmin: String,
    val lastUpdatedEpochMs: Long,
    val isSampleConfig: Boolean = true
)

/**
 * Represents the user's local session/profile state.
 */
data class UserProfileAccount(
    val userId: String,
    val fullName: String,
    val email: String,
    val phoneNumber: String,
    val kycTierLabel: String,
    val linkedSettlementUpi: String,
    val accountEnvironment: String = "SANDBOX_SAMPLE_MODE",
    val isLoggedIn: Boolean = false
)
