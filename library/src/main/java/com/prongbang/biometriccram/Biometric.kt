package com.prongbang.biometriccram

data class Biometric(
    val decrypted: String = "",
    val status: com.prongbang.biometriccram.Biometric.Status
) {
    data class PromptInfo(
        val title: String = "",
        val subtitle: String = "",
        val description: String = "",
        val negativeButton: String = "",
    )
    enum class Status {
        SUCCEEDED,
        ERROR,
        CANCEL
    }
}