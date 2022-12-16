package com.prongbang.biometriccram

interface SignatureBiometricManager {
    fun isSupported(): Boolean
    fun isAvailable(): Boolean
    fun isUnavailable(): Boolean
    fun authenticate(info: Biometric.PromptInfo, onResult: SignatureBiometricPromptManager.Result)
}