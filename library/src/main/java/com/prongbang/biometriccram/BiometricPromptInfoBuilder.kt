package com.prongbang.biometriccram

import androidx.biometric.BiometricPrompt
import javax.inject.Inject

interface BiometricPromptInfoBuilder {
    fun build(info: Biometric.PromptInfo):  BiometricPrompt.PromptInfo
}

class BiometricPromptInfoBuilderImpl @Inject constructor() : BiometricPromptInfoBuilder {

    override fun build(info: Biometric.PromptInfo): BiometricPrompt.PromptInfo {
        return BiometricPrompt.PromptInfo.Builder()
            .setTitle(info.title)
            .setSubtitle(info.subtitle)
            .setNegativeButtonText(info.negativeButton)
            .setDescription(info.description)
            .setConfirmationRequired(false)
            .setDeviceCredentialAllowed(false)
            .build()
    }

}