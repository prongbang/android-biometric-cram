package com.prongbang.biometricsignature

interface SignatureBiometricManager {
    fun isSupported(): Boolean
    fun isAvailable(): Boolean
    fun isUnavailable(): Boolean
    fun authenticate(info: Biometric.PromptInfo, onResult: SignatureBiometricPromptManager.Result)
}