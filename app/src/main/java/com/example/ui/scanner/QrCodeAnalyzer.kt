package com.example.ui.scanner

import android.net.Uri
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

data class ParsedPaymentQrPayload(
    val rawPayload: String,
    val payeeUpiId: String?,
    val payeeName: String?,
    val amountInr: Double?,
    val transactionNote: String?,
    val extractedUtrCandidate: String?,
    val matchedOrderId: String?
)

object PaymentQrParser {

    fun parse(rawText: String): ParsedPaymentQrPayload {
        val trimmed = rawText.trim()
        val twelveDigitMatch = Regex("\\b(\\d{12})\\b").find(trimmed)?.groupValues?.get(1)
        val orderIdMatch = Regex("(MXP-ORD-\\d{4})", RegexOption.IGNORE_CASE)
            .find(trimmed)?.groupValues?.get(1)?.uppercase()

        if (trimmed.startsWith("upi://pay", ignoreCase = true)) {
            val uri = runCatching { Uri.parse(trimmed) }.getOrNull()
            val pa = uri?.getQueryParameter("pa")
            val pn = uri?.getQueryParameter("pn")
            val am = uri?.getQueryParameter("am")?.toDoubleOrNull()
            val tn = uri?.getQueryParameter("tn")
            val tr = uri?.getQueryParameter("tr")

            val utrFromTr = tr?.takeIf { it.matches(Regex("^\\d{12}$")) } ?: twelveDigitMatch
            val orderFromTnOrTr = listOfNotNull(tn, tr)
                .firstNotNullOfOrNull { field ->
                    Regex("(MXP-ORD-\\d{4})", RegexOption.IGNORE_CASE).find(field)?.groupValues?.get(1)?.uppercase()
                } ?: orderIdMatch

            return ParsedPaymentQrPayload(
                rawPayload = trimmed,
                payeeUpiId = pa,
                payeeName = pn,
                amountInr = am,
                transactionNote = tn ?: tr,
                extractedUtrCandidate = utrFromTr,
                matchedOrderId = orderFromTnOrTr
            )
        }

        // Check if plain UPI handle
        val upiHandleMatch = Regex("([a-zA-Z0-9.\\-_]{2,256}@[a-zA-Z]{2,64})").find(trimmed)?.groupValues?.get(1)

        return ParsedPaymentQrPayload(
            rawPayload = trimmed,
            payeeUpiId = upiHandleMatch,
            payeeName = null,
            amountInr = null,
            transactionNote = null,
            extractedUtrCandidate = twelveDigitMatch,
            matchedOrderId = orderIdMatch
        )
    }
}

class ZxingQrCodeAnalyzer(
    private val onQrCodeDetected: (ParsedPaymentQrPayload) -> Unit
) : ImageAnalysis.Analyzer {

    private val reader = MultiFormatReader().apply {
        setHints(
            mapOf(
                DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                DecodeHintType.TRY_HARDER to true,
                DecodeHintType.ALSO_INVERTED to true
            )
        )
    }

    @Volatile
    private var lastDetectedTimestamp = 0L

    override fun analyze(image: ImageProxy) {
        try {
            val now = System.currentTimeMillis()
            if (now - lastDetectedTimestamp < 900L) {
                return
            }

            val plane = image.planes.firstOrNull() ?: return
            val bytes = plane.buffer.toByteArray()
            val width = image.width
            val height = image.height

            val source = PlanarYUVLuminanceSource(
                bytes,
                width,
                height,
                0,
                0,
                width,
                height,
                false
            )
            val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
            val result = runCatching { reader.decodeWithState(binaryBitmap) }.getOrNull()
            if (result != null && result.text.isNotBlank()) {
                lastDetectedTimestamp = now
                val parsed = PaymentQrParser.parse(result.text)
                onQrCodeDetected(parsed)
            }
        } catch (_: Exception) {
            // Frame did not contain a valid QR code; continue scanning next frame
        } finally {
            reader.reset()
            image.close()
        }
    }

    private fun ByteBuffer.toByteArray(): ByteArray {
        rewind()
        val data = ByteArray(remaining())
        get(data)
        return data
    }
}
