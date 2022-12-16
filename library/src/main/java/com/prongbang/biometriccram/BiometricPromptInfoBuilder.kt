package com.prongbang.biometriccram

import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import javax.inject.Inject

interface BiometricPromptInfoBuilder {
    fun build(info: Biometric.PromptInfo): BiometricPrompt.PromptInfo
}

class BiometricPromptInfoBuilderImpl @Inject constructor() : BiometricPromptInfoBuilder {

    override fun build(info: Biometric.PromptInfo): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(info.title)
            .setSubtitle(info.subtitle)
            .setDescription(info.description)
            .setNegativeButtonText(info.negativeButton)
            .setConfirmationRequired(false)
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            .build()
    }

}