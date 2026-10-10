package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.AdminOrderEntity
import com.example.data.model.BonusSettingEntity
import com.example.data.model.PaymentSubmissionEntity
import com.example.data.model.WalletTransactionEntity

@Database(
    entities = [
        AdminOrderEntity::class,
        PaymentSubmissionEntity::class,
        WalletTransactionEntity::class,
        BonusSettingEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MexcPayDatabase : RoomDatabase() {
    abstract fun mexcPayDao(): MexcPayDao

    companion object {
        @Volatile
        private var INSTANCE: MexcPayDatabase? = null

        fun getInstance(context: Context): MexcPayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MexcPayDatabase::class.java,
                    "mexc_pay_sandbox.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
