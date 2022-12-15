package com.prongbang.biometriccram.keypair

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.prongbang.biometriccram.exception.GenerateKeyPairException
import com.prongbang.biometriccram.exception.PrivateKeyNotFoundException
import com.prongbang.biometriccram.exception.PublicKeyNotFoundException
import java.security.*
import javax.inject.Inject

class BiometricKeyStoreManager @Inject constructor() : KeyStoreManager {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getPublicKey(key: String): PublicKey {
        return try {
            val keyPair = getKeyPair(key)
            keyPair.public
        } catch (e: Exception) {
            throw PublicKeyNotFoundException(message = e.cause?.message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getPrivateKey(key: String): PrivateKey {
        return try {
            val keyStore = getKeyStore()
            val privateKey = keyStore?.getKey(key, null) as? PrivateKey
            privateKey ?: let {
                getKeyPair(key)
                val keyStore2 = getKeyStore()
                val privateKey2 = keyStore2?.getKey(key, null) as PrivateKey
                privateKey2
            }
        } catch (e: Exception) {
            throw PrivateKeyNotFoundException(message = e.cause?.message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getKeyPair(key: String): KeyPair {
        return try {
            val purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            val builder = KeyGenParameterSpec.Builder(key, purposes).apply {
                setBlockModes(KeyProperties.BLOCK_MODE_ECB)
                setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                setUserAuthenticationRequired(true)
                setInvalidatedByBiometricEnrollment(false)
            }

            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_RSA,
                ANDROID_KEY_STORE
            )
            keyPairGenerator.initialize(builder.build())
            keyPairGenerator.generateKeyPair()
        } catch (e: Exception) {
            throw GenerateKeyPairException(message = e.cause?.message)
        }
    }

    private fun getKeyStore(): KeyStore? {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore?.load(null)

        return keyStore
    }

    companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}