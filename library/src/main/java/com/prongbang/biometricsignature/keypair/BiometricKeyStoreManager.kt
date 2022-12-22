package com.prongbang.biometricsignature.keypair

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import com.prongbang.biometricsignature.exception.GenerateKeyPairException
import com.prongbang.biometricsignature.exception.PrivateKeyNotFoundException
import com.prongbang.biometricsignature.exception.PublicKeyNotFoundException
import java.security.*
import java.security.spec.ECGenParameterSpec
import javax.inject.Inject

/**
 * Reference: https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec#example:-nist-p-256-ec-key-pair-for-signingverification-using-ecdsa
 */
class BiometricKeyStoreManager @Inject constructor() : KeyStoreManager {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getPublicKey(key: String): PublicKey {
        return try {
            val keyStore = getKeyStore()
            val publicKey = keyStore.getCertificate(key).publicKey
            publicKey
        } catch (e: Exception) {
            throw PublicKeyNotFoundException(message = e.cause?.message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getPrivateKey(key: String): PrivateKey {
        return try {
            val keyStore = getKeyStore()
            val privateKey = keyStore.getKey(key, null) as PrivateKey
            privateKey
        } catch (e: Exception) {
            throw PrivateKeyNotFoundException(message = e.cause?.message)
        }
    }

    /**
     * How to use:
     *  val keyPair = getKeyPair(key)
     *  val publicKey = keyPair.public
     *  val privateKey = keyPair.private
     */
    override fun getKeyPair(key: String): KeyPair {
        return try {
            val purposes = KeyProperties.PURPOSE_SIGN
            val builder = KeyGenParameterSpec.Builder(key, purposes).apply {
                setAlgorithmParameterSpec(ECGenParameterSpec("secp256r1"))
                setDigests(
                    KeyProperties.DIGEST_SHA256,
                    KeyProperties.DIGEST_SHA384,
                    KeyProperties.DIGEST_SHA512
                )
                setUserAuthenticationRequired(true)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    setInvalidatedByBiometricEnrollment(false)
                }
            }

            val keyPairGenerator = KeyPairGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_EC,
                ANDROID_KEY_STORE
            )
            keyPairGenerator.initialize(builder.build())
            keyPairGenerator.generateKeyPair()
        } catch (e: Exception) {
            throw GenerateKeyPairException(message = e.cause?.message)
        }
    }

    override fun getKeyStore(): KeyStore {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE)
        keyStore.load(null)

        return keyStore
    }

    companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
    }
}