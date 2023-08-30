# android-biometric-signature

Generate key pair and signing (NIST P-256 EC key pair using ECDSA) using Local Authentication for Android.

https://developer.android.com/reference/android/security/keystore/KeyGenParameterSpec#example:-nist-p-256-ec-key-pair-for-signingverification-using-ecdsa

Signature

https://developer.android.com/reference/java/security/Signature

[![](https://jitpack.io/v/prongbang/android-biometric-signature.svg)](https://jitpack.io/#prongbang/android-biometric-signature)

## Setup

- `build.gradle`

```groovy
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

- `settings.gradle`

```groovy
dependencyResolutionManagement {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

- `app/build.gradle`

```groovy
implementation 'com.github.prongbang:android-biometric-signature:1.0.3'
```

## How to use

- Create field `payloadBiometricSignature`

```kotlin
private val payloadBiometricSignature = object : BiometricSignature() {
    override fun payload(): String = UUID.randomUUID().toString()
}
```

- Create field `customKeyStoreAliasKey`

```kotlin
private val customKeyStoreAliasKey = object : KeyStoreAliasKey {
    override fun key(): String = "com.prongbang.signx.seckey"
}
```

- Create field `promptInfo`

```kotlin
private val promptInfo = Biometric.PromptInfo(
    title = "BIOMETRIC",
    subtitle = "Please scan biometric to Login Application",
    description = "description here",
    negativeButton = "CANCEL"
)
```

- Generate KeyPair with Biometric

```kotlin
private val registrationBiometricPromptManager by lazy {
    SignatureBiometricPromptManager.newInstance(
        this@MainActivity,
        keyStoreAliasKey = customKeyStoreAliasKey
    )
}
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
                    Log.i("ERROR", "ERROR")
                }
                Biometric.Status.CANCEL -> {
                    Log.i("CANCEL", "CANCEL")
                }
            }
        }
    })
```

- Sign with Biometric

```kotlin
private val signatureBiometricPromptManager by lazy {
    SignatureBiometricPromptManager.newInstance(
        this@MainActivity,
        keyStoreAliasKey = customKeyStoreAliasKey,
        biometricSignature = payloadBiometricSignature,
    )
}
signatureBiometricPromptManager.sign(
    promptInfo,
    object : SignatureBiometricPromptManager.Result {
        override fun callback(biometric: Biometric) {
            when (biometric.status) {
                Biometric.Status.SUCCEEDED -> {
                    val signature = biometric.signature
                    Log.i("SUCCEEDED", "signature: $signature")
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
```

- Verify with Biometric

```kotlin
private val verifyBiometricPromptManager by lazy {
    SignatureBiometricPromptManager.newInstance(
        this@MainActivity,
        keyStoreAliasKey = customKeyStoreAliasKey,
        biometricSignature = payloadBiometricSignature,
    )
}
verifyBiometricPromptManager.verify(
    promptInfo,
    object : SignatureBiometricPromptManager.Result {
        override fun callback(biometric: Biometric) {
            when (biometric.status) {
                Biometric.Status.SUCCEEDED -> {
                    val verify = biometric.verify
                    Log.i("SUCCEEDED", "verify: $verify")
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
```
