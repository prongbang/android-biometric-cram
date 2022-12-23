package com.prongbang.biometricsignature

interface SignatureBiometricManager {
    fun isSupported(): Boolean
    fun isAvailable(): Boolean
    fun isUnavailable(): Boolean
    fun createKeyPair(info: Biometric.PromptInfo, onResult: SignatureBiometricPromptManager.Result)
    fun sign(info: Biometric.PromptInfo, onResult: SignatureBiometricPromptManager.Result)
    fun verify(info: Biometric.PromptInfo, onResult: SignatureBiometricPromptManager.Result)
}