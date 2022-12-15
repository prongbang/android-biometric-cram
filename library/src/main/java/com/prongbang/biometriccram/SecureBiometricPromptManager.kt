package com.prongbang.biometriccram

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.prongbang.biometriccram.cipher.BiometricCryptography
import com.prongbang.biometriccram.cipher.BiometricKeyStoreCipher
import com.prongbang.biometriccram.cipher.Cryptography
import com.prongbang.biometriccram.cipher.KeyStoreCipher
import com.prongbang.biometriccram.executor.ExecutorCreator
import com.prongbang.biometriccram.executor.MainExecutorCreator
import com.prongbang.biometriccram.key.BiometricCryptographyKey
import com.prongbang.biometriccram.keypair.BiometricKeyStoreManager
import com.prongbang.biometriccram.token.BiometricToken
import javax.inject.Inject

class SecureBiometricPromptManager @Inject constructor(
    activity: FragmentActivity,
    cryptography: Cryptography,
    biometricToken: BiometricToken,
    executorCreator: ExecutorCreator,
    private val keyStoreCipher: KeyStoreCipher,
    private val biometricPromptInfoBuilder: BiometricPromptInfoBuilder,
) : SecureBiometricManager {

    interface Result {
        fun callback(biometric: Biometric)
    }

    private var onResult: Result? = null
    private val biometricPrompt = BiometricPrompt(
        activity,
        executorCreator.create(activity.applicationContext),
        BiometricAuthenticationHandler(cryptography, biometricToken),
    )
    private val biometricManager = BiometricManager.from(activity.applicationContext)

    @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.N)
    override fun isSupported(): Boolean = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N

    override fun isAvailable(): Boolean {
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS && isSupported()
    }

    override fun isUnavailable(): Boolean {
        val canAuthentication =
            biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)
        return canAuthentication == BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED
                || canAuthentication == BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE
    }

    override fun authenticate(info: Biometric.PromptInfo, onResult: Result) {
        this.onResult = onResult
        try {
            val bioPromptCrypto = BiometricPrompt.CryptoObject(
                keyStoreCipher.getAsymmetricDecryptCipher()
            )
            val promptInfo = biometricPromptInfoBuilder.build(info)

            biometricPrompt.authenticate(promptInfo, bioPromptCrypto)
        } catch (e: Exception) {
            onResult.callback(Biometric(status = Biometric.Status.ERROR))
        }
    }

    inner class BiometricAuthenticationHandler(
        private val cryptography: Cryptography,
        private val biometricToken: BiometricToken,
    ) : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            val result = when (errorCode) {
                BiometricPrompt.ERROR_NEGATIVE_BUTTON -> Biometric.Status.CANCEL
                BiometricPrompt.ERROR_USER_CANCELED -> Biometric.Status.CANCEL
                else -> Biometric.Status.ERROR
            }
            onResult?.callback(Biometric(status = result))
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            try {
                val decryptedText = cryptography.decrypt(
                    cipher = result.cryptoObject?.cipher!!,
                    cipherText = biometricToken.cipherText()
                )
                onResult?.callback(
                    Biometric(
                        decryptedText,
                        Biometric.Status.SUCCEEDED
                    )
                )
            } catch (e: Exception) {
                onResult?.callback(Biometric(status = Biometric.Status.ERROR))
            }
        }
    }

    companion object {
        fun newInstance(
            fragmentActivity: FragmentActivity,
            biometricToken: BiometricToken,
        ): SecureBiometricManager {
            val cryptographyKey = BiometricCryptographyKey()
            val keyStoreManager = BiometricKeyStoreManager()
            val keyStoreCipher = BiometricKeyStoreCipher(cryptographyKey, keyStoreManager)
            val cryptography = BiometricCryptography.newInstance()
            val biometricPromptInfoBuilder = BiometricPromptInfoBuilderImpl()
            val mainExecutorCreator = MainExecutorCreator()
            return SecureBiometricPromptManager(
                activity = fragmentActivity,
                cryptography = cryptography,
                keyStoreCipher = keyStoreCipher,
                biometricToken = biometricToken,
                executorCreator = mainExecutorCreator,
                biometricPromptInfoBuilder = biometricPromptInfoBuilder,
            )
        }
    }
}