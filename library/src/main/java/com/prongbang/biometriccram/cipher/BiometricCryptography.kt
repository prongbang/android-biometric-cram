package com.prongbang.biometriccram.cipher

import android.os.Build
import androidx.annotation.RequiresApi
import com.prongbang.biometriccram.exception.DecryptException
import com.prongbang.biometriccram.exception.EncryptException
import com.prongbang.biometriccram.key.BiometricCryptographyKey
import com.prongbang.biometriccram.keypair.BiometricKeyStoreManager
import com.prongbang.biometriccram.utility.AndroidBase64Utility
import com.prongbang.biometriccram.utility.Base64Utility
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class BiometricCryptography @Inject constructor(
    private val keyStoreCipher: KeyStoreCipher,
    private val base64Utility: Base64Utility,
) : Cryptography {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun encrypt(plainText: String): String {
        try {
            val cipher = keyStoreCipher.getAsymmetricEncryptCipher()

            val keyGenerator = KeyGenerator.getInstance(Cryptography.ALGORITHM_CONFIG)
            keyGenerator.init(256)

            val secretKey = keyGenerator.generateKey()
            val encryptedSecretKey = cipher.doFinal(secretKey.encoded) ?: byteArrayOf()

            val secureCipher = keyStoreCipher.getSymmetricEncryptCipher(secretKey)
            val cipherText = secureCipher.doFinal(plainText.toByteArray())

            val encryptedSecretKeyBase64 = base64Utility.encode(encryptedSecretKey)
            val encryptedIvBase64 = base64Utility.encode(secureCipher.iv)
            val cipherTextBase64 = base64Utility.encode(cipherText)

            return "$encryptedSecretKeyBase64${Cryptography.SEPARATE_TEXT}$encryptedIvBase64${Cryptography.SEPARATE_TEXT}$cipherTextBase64"
        } catch (e: Exception) {
            throw EncryptException(e.cause?.message)
        }
    }

    override fun decrypt(cipherText: String): String {
        return try {
            decrypt(keyStoreCipher.getAsymmetricDecryptCipher(), cipherText)
        } catch (e: Exception) {
            throw DecryptException(e.cause?.message)
        }
    }

    override fun decrypt(cipher: Cipher, cipherText: String): String {
        return try {
            val cipherSplits = cipherText.split(Cryptography.SEPARATE_TEXT)

            val encryptedSecretKey = base64Utility.decode(cipherSplits[0])
            val iv = base64Utility.decode(cipherSplits[1])
            val rawCipherText = base64Utility.decode(cipherSplits[2])

            val secretKey = cipher.doFinal(encryptedSecretKey)
            val secureCipher = keyStoreCipher.getSymmetricDecryptCipher(iv, secretKey)
            val ivSpec = IvParameterSpec(iv)
            val secretSpec = SecretKeySpec(
                secretKey,
                0,
                secretKey.size,
                Cryptography.ALGORITHM_CONFIG
            )

            secureCipher.init(Cipher.DECRYPT_MODE, secretSpec, ivSpec)
            val plainText = secureCipher.doFinal(rawCipherText)

            String(plainText)
        } catch (e: Exception) {
            throw DecryptException(e.cause?.message)
        }
    }

    companion object {
        fun newInstance(): Cryptography {
            val cryptographyKey = BiometricCryptographyKey()
            val keyStoreManager = BiometricKeyStoreManager()
            val keyStoreCipher = BiometricKeyStoreCipher(cryptographyKey, keyStoreManager)
            val base64Utility = AndroidBase64Utility()
            return BiometricCryptography(keyStoreCipher, base64Utility)
        }
    }
}