package com.prongbang.biometriccram

interface SecureBiometricManager {
    fun isSupported(): Boolean
    fun isAvailable(): Boolean
    fun isUnavailable(): Boolean
    fun authenticate(info: com.prongbang.biometriccram.Biometric.PromptInfo, onResult: SecureBiometricPromptManager.Result)
}