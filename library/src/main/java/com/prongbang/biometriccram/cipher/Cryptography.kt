package com.prongbang.biometriccram.cipher

import javax.crypto.Cipher

interface Cryptography {
    fun decrypt(cipher: Cipher, cipherText: String): String
    fun decrypt(cipherText: String): String
    fun encrypt(plainText: String): String

    companion object {
        const val ALGORITHM_CONFIG = "AES"
        const val CIPHER_SYMMETRIC_CONFIG = "AES/GCM/NoPadding"
        const val CIPHER_ASYMMETRIC_CONFIG = "RSA/ECB/PKCS1Padding"
        const val SEPARATE_TEXT = ":"
    }
}