package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.MexcPayDatabase
import com.example.data.model.PaymentVerificationState
import com.example.data.repository.MexcPayRepository
import com.example.data.repository.UtrSubmissionResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var database: MexcPayDatabase
    private lateinit var repository: MexcPayRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, MexcPayDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = MexcPayRepository(database.mexcPayDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("MEXC PAY", appName)
    }

    @Test
    fun `utr submission validates 12-digit format and never credits verified wallet balance`() = runBlocking {
        repository.ensureSampleDataSeeded()

        val initialTransactions = repository.allTransactions.first()
        val initialVerifiedBalance = initialTransactions
            .filter { it.affectsVerifiedBalance }
            .sumOf { it.amountInr }

        // 1. Invalid short UTR is rejected
        val invalidResult = repository.submitUtrForOrder(
            orderId = "MXP-ORD-9041",
            rawUtr = "12345",
            rawPayerUpi = "user@okaxis",
            remarks = "Short UTR"
        )
        assertTrue(invalidResult is UtrSubmissionResult.ValidationError)

        // 2. Valid 12-digit UTR is recorded as PENDING_BACKEND_VERIFICATION
        val validResult = repository.submitUtrForOrder(
            orderId = "MXP-ORD-9041",
            rawUtr = "428190384712",
            rawPayerUpi = "trader.sample@okaxis",
            remarks = "Test sandbox UTR claim"
        )
        assertTrue(validResult is UtrSubmissionResult.RecordedPendingVerification)
        val recordedSubmission = (validResult as UtrSubmissionResult.RecordedPendingVerification).submission
        assertEquals(
            PaymentVerificationState.PENDING_BACKEND_VERIFICATION.name,
            recordedSubmission.verificationState
        )
        assertFalse(recordedSubmission.isSampleData)

        // 3. Verified wallet balance MUST remain strictly unchanged after UTR submission
        val updatedTransactions = repository.allTransactions.first()
        val updatedVerifiedBalance = updatedTransactions
            .filter { it.affectsVerifiedBalance }
            .sumOf { it.amountInr }
        assertEquals(initialVerifiedBalance, updatedVerifiedBalance, 0.001)

        // 4. Duplicate UTR is rejected
        val duplicateResult = repository.submitUtrForOrder(
            orderId = "MXP-ORD-9042",
            rawUtr = "428190384712",
            rawPayerUpi = "trader.sample@okaxis",
            remarks = "Duplicate attempt"
        )
        assertTrue(duplicateResult is UtrSubmissionResult.ValidationError)
    }
}
