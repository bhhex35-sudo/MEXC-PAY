package com.example.ui.security

import android.content.Context
import android.content.ContextWrapper
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_WEAK
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

enum class BiometricHardwareStatus(val statusText: String, val isReady: Boolean) {
    READY("Biometric Hardware Active (Fingerprint / Face Unlock)", true),
    NONE_ENROLLED("No Fingerprint/Face Enrolled on Device", false),
    NO_HARDWARE("No Biometric Sensor Detected on Device", false),
    UNAVAILABLE("Biometric Sensor Currently Unavailable", false)
}

object BiometricAuthHelper {

    private const val ALLOWED_AUTHENTICATORS = BIOMETRIC_STRONG or BIOMETRIC_WEAK or DEVICE_CREDENTIAL

    fun checkBiometricSupport(context: Context): BiometricHardwareStatus {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(ALLOWED_AUTHENTICATORS)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricHardwareStatus.READY
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricHardwareStatus.NONE_ENROLLED
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricHardwareStatus.NO_HARDWARE
            else -> BiometricHardwareStatus.UNAVAILABLE
        }
    }

    fun authenticate(
        context: Context,
        title: String = "MEXC PAY Biometric Security",
        subtitle: String = "Verify with Fingerprint or Face Unlock to access your OTC desk",
        onSuccess: () -> Unit,
        onError: (String, Boolean) -> Unit
    ) {
        val activity = context.findFragmentActivity()
        if (activity == null) {
            onError("Unable to attach biometric prompt to host activity.", false)
            return
        }

        val status = checkBiometricSupport(context)
        if (!status.isReady) {
            onError(
                "${status.statusText}. Enroll a fingerprint/face in Android Settings or use PIN/Password fallback.",
                true
            )
            return
        }

        val executor = ContextCompat.getMainExecutor(context)
        val callback = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                val isHardwareOrEnrollmentIssue =
                    errorCode == BiometricPrompt.ERROR_NO_BIOMETRICS ||
                        errorCode == BiometricPrompt.ERROR_HW_NOT_PRESENT ||
                        errorCode == BiometricPrompt.ERROR_HW_UNAVAILABLE ||
                        errorCode == BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL
                onError(errString.toString(), isHardwareOrEnrollmentIssue)
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Biometric not recognized. Please try again.", false)
            }
        }

        val biometricPrompt = BiometricPrompt(activity, executor, callback)
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setAllowedAuthenticators(ALLOWED_AUTHENTICATORS)
            .setConfirmationRequired(false)
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun Context.findFragmentActivity(): FragmentActivity? {
        var currentContext = this
        while (currentContext is ContextWrapper) {
            if (currentContext is FragmentActivity) {
                return currentContext
            }
            currentContext = currentContext.baseContext
        }
        return null
    }
}
