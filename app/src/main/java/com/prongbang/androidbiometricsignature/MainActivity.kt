package com.prongbang.androidbiometricsignature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import com.prongbang.androidbiometricsignature.databinding.ActivityMainBinding
import com.prongbang.androidbiometricsignature.server.MyServer
import com.prongbang.biometricsignature.Biometric
import com.prongbang.biometricsignature.SignatureBiometricPromptManager
import com.prongbang.biometricsignature.key.KeyStoreAliasKey
import com.prongbang.biometricsignature.signature.BiometricSignature
import java.util.*

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val myServer by lazy { MyServer() }

    private val userId = 1

    private val cramKeyStoreAliasKey = object : KeyStoreAliasKey {
        override fun key(): String = "com.prongbang.androidbiometriccram.key"
    }
    private val promptInfo = Biometric.PromptInfo(
        title = "BIOMETRIC",
        subtitle = "Please scan biometric to Login Application",
        description = "description here",
        negativeButton = "CANCEL"
    )
    private var nonce: String = ""

    private val biometricSignature = object : BiometricSignature() {
        override fun payload(): String {
            // TODO Step: 2.1 Request
            val challenge = myServer.challengeRequest(userId)
            nonce = UUID.randomUUID().toString()

            return challenge + nonce
        }
    }

    private val registrationBiometricPromptManager by lazy {
        SignatureBiometricPromptManager.newInstance(
            this@MainActivity,
            keyStoreAliasKey = cramKeyStoreAliasKey
        )
    }

    private val signatureBiometricPromptManager by lazy {
        SignatureBiometricPromptManager.newInstance(
            this@MainActivity,
            keyStoreAliasKey = cramKeyStoreAliasKey,
            biometricSignature = biometricSignature,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initView()
    }

    private fun initView() {
        binding.apply {

            // TODO Step: 1 Registration
            registrationButton.setOnClickListener {
                registrationBiometricPromptManager.authenticate(
                    promptInfo,
                    object : SignatureBiometricPromptManager.Result {
                        override fun callback(biometric: Biometric) {
                            when (biometric.status) {
                                Biometric.Status.SUCCEEDED -> {
                                    val publicKey = biometric.keyPair?.publicKey
                                    Log.i("SUCCEEDED", "PublicKey: $publicKey")

                                    // TODO Step: 1.1 Registration
                                    val response = myServer.registration(userId, publicKey)
                                    Log.i("SUCCEEDED", "Registration: $response")
                                }
                                Biometric.Status.ERROR -> {
                                    Log.i("ERROR", "ERROR")
                                }
                                Biometric.Status.CANCEL -> {
                                    Log.i("CANCEL", "CANCEL")
                                }
                            }
                        }
                    })
            }

            // TODO Step: 2 Sign & Verify
            verifyButton.setOnClickListener {

                // TODO Step: 2.1 Sign
                signatureBiometricPromptManager.authenticate(
                    promptInfo,
                    object : SignatureBiometricPromptManager.Result {
                        override fun callback(biometric: Biometric) {
                            when (biometric.status) {
                                Biometric.Status.SUCCEEDED -> {
                                    val signature = biometric.signature
                                    Log.i("SUCCEEDED", "signature: $signature")

                                    // TODO Step: 2.2 Verify
                                    signature?.let {
                                        val response = myServer.challengeVerify(
                                            userId,
                                            it.signature,
                                            it.payload,
                                            nonce
                                        )
                                        Log.i("SUCCEEDED", "verify: $response")

                                        AlertDialog.Builder(this@MainActivity)
                                            .setTitle("Signature")
                                            .setMessage("verify: $response")
                                            .create()
                                            .show()
                                    }
                                }
                                Biometric.Status.ERROR -> {
                                    Log.i("ERROR", "ERROR")
                                }
                                Biometric.Status.CANCEL -> {
                                    Log.i("CANCEL", "CANCEL")
                                }
                            }
                        }
                    })
            }
        }
    }
}