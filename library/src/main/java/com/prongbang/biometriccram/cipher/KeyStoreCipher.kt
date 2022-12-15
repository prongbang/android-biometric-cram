package com.prongbang.biometriccram.cipher

import javax.crypto.Cipher
import javax.crypto.SecretKey

interface KeyStoreCipher {
    fun getSymmetricEncryptCipher(secretKey: SecretKey): Cipher
    fun getSymmetricDecryptCipher(iv: ByteArray, secretKey: ByteArray): Cipher
    fun getAsymmetricDecryptCipher(): Cipher
    fun getAsymmetricEncryptCipher(): Cipher
}