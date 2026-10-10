package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.AdminOrderEntity
import com.example.data.model.BonusSettingEntity
import com.example.data.model.PaymentSubmissionEntity
import com.example.data.model.WalletTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MexcPayDao {

    @Query("SELECT * FROM admin_orders ORDER BY createdAtEpochMs DESC")
    fun observeAllOrders(): Flow<List<AdminOrderEntity>>

    @Query("SELECT * FROM admin_orders WHERE orderId = :orderId LIMIT 1")
    fun observeOrderById(orderId: String): Flow<AdminOrderEntity?>

    @Query("SELECT * FROM admin_orders WHERE orderId = :orderId LIMIT 1")
    suspend fun getOrderById(orderId: String): AdminOrderEntity?

    @Query("SELECT COUNT(*) FROM admin_orders")
    suspend fun getOrdersCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrders(orders: List<AdminOrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateOrder(order: AdminOrderEntity)

    @Query("SELECT * FROM payment_submissions ORDER BY submittedAtEpochMs DESC")
    fun observeAllSubmissions(): Flow<List<PaymentSubmissionEntity>>

    @Query("SELECT * FROM payment_submissions WHERE orderId = :orderId ORDER BY submittedAtEpochMs DESC")
    fun observeSubmissionsForOrder(orderId: String): Flow<List<PaymentSubmissionEntity>>

    @Query("SELECT * FROM payment_submissions WHERE submittedUtrNumber = :utr LIMIT 1")
    suspend fun findSubmissionByUtr(utr: String): PaymentSubmissionEntity?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSubmission(submission: PaymentSubmissionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmissions(submissions: List<PaymentSubmissionEntity>)

    @Query("SELECT * FROM wallet_transactions ORDER BY timestampEpochMs DESC")
    fun observeAllTransactions(): Flow<List<WalletTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: WalletTransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<WalletTransactionEntity>)

    @Query("SELECT * FROM bonus_settings ORDER BY minAmountInr ASC")
    fun observeBonusSettings(): Flow<List<BonusSettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBonusSettings(settings: List<BonusSettingEntity>)

    @Query("DELETE FROM admin_orders")
    suspend fun clearOrders()

    @Query("DELETE FROM payment_submissions")
    suspend fun clearSubmissions()

    @Query("DELETE FROM wallet_transactions")
    suspend fun clearTransactions()

    @Query("DELETE FROM bonus_settings")
    suspend fun clearBonusSettings()
}
