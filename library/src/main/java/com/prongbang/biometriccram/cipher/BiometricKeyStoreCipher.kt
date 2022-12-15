package com.prongbang.biometriccram.cipher

import android.os.Build
import androidx.annotation.RequiresApi
import com.prongbang.biometriccram.key.CryptographyKey
import com.prongbang.biometriccram.keypair.KeyStoreManager
import javax.crypto.Cipher
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class BiometricKeyStoreCipher @Inject constructor(
    private val cryptographyKey: CryptographyKey,
    private val keyStoreManager: KeyStoreManager
) : KeyStoreCipher {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getSymmetricEncryptCipher(secretKey: SecretKey): Cipher {
        val cipher = Cipher.getInstance(Cryptography.CIPHER_SYMMETRIC_CONFIG)

        cipher.init(Cipher.ENCRYPT_MODE, secretKey)

        return cipher
    }

    override fun getSymmetricDecryptCipher(iv: ByteArray, secretKey: ByteArray): Cipher {
        val cipher = Cipher.getInstance(Cryptography.CIPHER_SYMMETRIC_CONFIG)
        val ivSpec = IvParameterSpec(iv)
        val secretSpec = SecretKeySpec(
            secretKey,
            0,
            secretKey.size,
            Cryptography.ALGORITHM_CONFIG
        )

        cipher.init(Cipher.DECRYPT_MODE, secretSpec, ivSpec)

        return cipher
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getAsymmetricEncryptCipher(): Cipher {
        val cipher = Cipher.getInstance(Cryptography.CIPHER_ASYMMETRIC_CONFIG)
        val publicKey = keyStoreManager.getPublicKey(cryptographyKey.key())

        cipher?.init(Cipher.ENCRYPT_MODE, publicKey)

        return cipher
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getAsymmetricDecryptCipher(): Cipher {
        val cipher = Cipher.getInstance(Cryptography.CIPHER_ASYMMETRIC_CONFIG)
        val privateKey = keyStoreManager.getPrivateKey(cryptographyKey.key())

        cipher?.init(Cipher.DECRYPT_MODE, privateKey)

        return cipher
    }

}