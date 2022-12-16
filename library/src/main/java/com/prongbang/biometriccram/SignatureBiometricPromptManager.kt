package com.prongbang.biometriccram

import android.os.Build
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.util.Base64
import android.util.Log
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import com.prongbang.biometriccram.signature.BiometricKeyStoreSignature
import com.prongbang.biometriccram.signature.KeyStoreSignature
import com.prongbang.biometriccram.executor.ExecutorCreator
import com.prongbang.biometriccram.executor.MainExecutorCreator
import com.prongbang.biometriccram.extensions.toBase64
import com.prongbang.biometriccram.key.BiometricKeyStoreAliasKey
import com.prongbang.biometriccram.key.KeyStoreAliasKey
import com.prongbang.biometriccram.keypair.BiometricKeyStoreManager
import com.prongbang.biometriccram.keypair.KeyStoreManager
import com.prongbang.biometriccram.signature.BiometricSignature
import java.security.KeyStoreException
import java.security.SignatureException
import javax.inject.Inject

class SignatureBiometricPromptManager @Inject constructor(
    activity: FragmentActivity,
    biometricSignature: BiometricSignature?,
    keyStoreManager: KeyStoreManager,
    keyStoreAliasKey: KeyStoreAliasKey,
    executorCreator: ExecutorCreator,
    private val keyStoreSignature: KeyStoreSignature,
    private val biometricPromptInfoBuilder: BiometricPromptInfoBuilder,
) : SignatureBiometricManager {

    interface Result {
        fun callback(biometric: Biometric)
    }

    private var onResult: Result? = null
    private val biometricPrompt = BiometricPrompt(
        activity,
        executorCreator.create(activity.applicationContext),
        BiometricAuthenticationHandler(biometricSignature, keyStoreManager, keyStoreAliasKey),
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
                keyStoreSignature.getSignature()
            )
            val promptInfo = biometricPromptInfoBuilder.build(info)

            biometricPrompt.authenticate(promptInfo, bioPromptCrypto)
        } catch (e: Exception) {
            Log.e("authenticate", "${e.message}")
            onResult.callback(Biometric(status = Biometric.Status.ERROR))
        }
    }

    inner class BiometricAuthenticationHandler(
        private val biometricSignature: BiometricSignature?,
        private val keyStoreManager: KeyStoreManager,
        private val keyStoreAliasKey: KeyStoreAliasKey,
    ) : BiometricPrompt.AuthenticationCallback() {

        override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
            val result = when (errorCode) {
                BiometricPrompt.ERROR_NEGATIVE_BUTTON -> Biometric.Status.CANCEL
                BiometricPrompt.ERROR_USER_CANCELED -> Biometric.Status.CANCEL
                else -> Biometric.Status.ERROR
            }
            Log.e("onAuthenticationError", "$errString")
            onResult?.callback(Biometric(status = result))
        }

        override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
            try {
                try {
                    val cryptoObject = result.cryptoObject
                    if (biometricSignature == null) {
                        val publicKey = keyStoreManager.getPublicKey(keyStoreAliasKey.key())
                        val keyPair = Biometric.KeyPair(publicKey = publicKey.toBase64())

                        onResult?.callback(
                            Biometric(keyPair = keyPair, status = Biometric.Status.SUCCEEDED)
                        )
                    } else {
                        val signature = cryptoObject?.signature
                        val challengeText = biometricSignature.challenge()
                        val nonce = biometricSignature.nonce()
                        var textToSign = challengeText
                        if (nonce.isNotEmpty()) {
                            textToSign += nonce
                        }
                        signature?.update(textToSign.toByteArray(Charsets.UTF_8))
                        val signatureBytes = signature?.sign()
                        val signed = Base64.encodeToString(
                            signatureBytes,
                            Base64.URL_SAFE or Base64.NO_WRAP
                        )

                        onResult?.callback(
                            Biometric(
                                signature = Biometric.Signature(
                                    signature = signed,
                                    challenge = challengeText,
                                    nonce = nonce,
                                ),
                                status = Biometric.Status.SUCCEEDED
                            )
                        )
                    }
                } catch (e: KeyPermanentlyInvalidatedException) {
                    onResult?.callback(Biometric(status = Biometric.Status.ERROR))
                } catch (e: KeyStoreException) {
                    onResult?.callback(Biometric(status = Biometric.Status.ERROR))
                } catch (e: SignatureException) {
                    onResult?.callback(Biometric(status = Biometric.Status.ERROR))
                }
            } catch (e: Exception) {
                Log.e("onAuthenticationSucceeded", "${e.message}")
                onResult?.callback(Biometric(status = Biometric.Status.ERROR))
            }
        }
    }

    companion object {
        fun newInstance(
            fragmentActivity: FragmentActivity,
            biometricSignature: BiometricSignature? = null,
            keyStoreAliasKey: KeyStoreAliasKey? = null,
        ): SignatureBiometricManager {
            val keyStoreManager = BiometricKeyStoreManager()
            val keyStoreAlias = keyStoreAliasKey ?: BiometricKeyStoreAliasKey()
            val keyStoreCipher = BiometricKeyStoreSignature(keyStoreAlias, keyStoreManager)
            val biometricPromptInfoBuilder = BiometricPromptInfoBuilderImpl()
            val mainExecutorCreator = MainExecutorCreator()
            return SignatureBiometricPromptManager(
                activity = fragmentActivity,
                keyStoreSignature = keyStoreCipher,
                biometricSignature = biometricSignature,
                keyStoreManager = keyStoreManager,
                keyStoreAliasKey = keyStoreAlias,
                executorCreator = mainExecutorCreator,
                biometricPromptInfoBuilder = biometricPromptInfoBuilder,
            )
        }
    }
}