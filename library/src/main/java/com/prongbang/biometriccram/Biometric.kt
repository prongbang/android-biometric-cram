package com.prongbang.biometriccram

data class Biometric(
    val signature: Signature? = null,
    val keyPair: KeyPair? = null,
    val status: Status
) {

    data class Signature(
        val signature: String = "",
        val challenge: String = "",
    )

    data class KeyPair(
        val publicKey: String = "",
        val privateKey: String = "",
    )

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