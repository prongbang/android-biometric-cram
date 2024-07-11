package com.prongbang.androidbiometricsignature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.prongbang.androidbiometricsignature.databinding.ActivityMainBinding
import com.prongbang.biometricsignature.Biometric
import com.prongbang.biometricsignature.SignatureBiometricPromptManager
import com.prongbang.biometricsignature.key.KeyStoreAliasKey
import com.prongbang.biometricsignature.signature.BiometricSignature
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val customKeyStoreAliasKey = object : KeyStoreAliasKey {
        override fun key(): String = "com.prongbang.signx.seckey"
    }

    private val promptInfo = Biometric.PromptInfo(
        title = "BIOMETRIC",
        subtitle = "Please scan biometric to Login Application",
        description = "description here",
        negativeButton = "CANCEL",
        invalidatedByBiometricEnrollment = true
    )

    private val signBiometricSignature = object : BiometricSignature() {
        override fun payload(): String = "hello"
    }

    private val registrationBiometricPromptManager by lazy {
        SignatureBiometricPromptManager.newInstance(
            this@MainActivity,
            keyStoreAliasKey = customKeyStoreAliasKey
        )
    }

    private val signBiometricPromptManager by lazy {
        SignatureBiometricPromptManager.newInstance(
            this@MainActivity,
            biometricSignature = signBiometricSignature,
        )
    }

    private val verifyBiometricPromptManager by lazy {
        SignatureBiometricPromptManager.newInstance(
            this@MainActivity,
            keyStoreAliasKey = customKeyStoreAliasKey,
            biometricSignature = signBiometricSignature,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.apply {

            registrationButton.setOnClickListener {
                registrationBiometricPromptManager.createKeyPair(
                    promptInfo,
                    object : SignatureBiometricPromptManager.Result {
                        override fun callback(biometric: Biometric) {
                            when (biometric.status) {
                                Biometric.Status.SUCCEEDED -> {
                                    val publicKey = biometric.keyPair?.publicKey
                                    Log.i("SUCCEEDED", "PublicKey: $publicKey")
                                }

                                Biometric.Status.ERROR -> {
                                    Log.i("ERROR", "ERROR ${biometric.error}")
                                }

                                Biometric.Status.CANCEL -> {
                                    Log.i("CANCEL", "CANCEL ${biometric.error}")
                                }

                                Biometric.Status.LOCKOUT -> {
                                    Log.i("LOCKOUT", "LOCKOUT ${biometric.error}")
                                }

                                Biometric.Status.LOCKOUT_PERMANENT -> {
                                    Log.i("LOCKOUT", "LOCKOUT_PERMANENT ${biometric.error}")
                                }
                            }
                        }
                    })
            }

            signButton.setOnClickListener {
                signBiometricPromptManager.sign(
                    promptInfo,
                    object : SignatureBiometricPromptManager.Result {
                        override fun callback(biometric: Biometric) {
                            when (biometric.status) {
                                Biometric.Status.SUCCEEDED -> {
                                    val signature = biometric.signature
                                    Log.i("SUCCEEDED", "signature: $signature")
                                }

                                Biometric.Status.ERROR -> {
                                    Log.i("ERROR", "ERROR ${biometric.error}")
                                }

                                Biometric.Status.CANCEL -> {
                                    Log.i("CANCEL", "CANCEL ${biometric.error}")
                                }

                                Biometric.Status.LOCKOUT -> {
                                    Log.i("LOCKOUT", "LOCKOUT ${biometric.error}")
                                }

                                Biometric.Status.LOCKOUT_PERMANENT -> {
                                    Log.i("LOCKOUT", "LOCKOUT_PERMANENT ${biometric.error}")
                                }
                            }
                        }
                    })
            }
        }
    }
}
