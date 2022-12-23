# android-biometric-signature

Generate key pair and signing using Local Authentication for Android.

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
implementation 'com.github.prongbang:android-biometric-signature:1.0.0'
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

- Create field `registrationBiometricPromptManager`

```kotlin
private val registrationBiometricPromptManager by lazy {
    SignatureBiometricPromptManager.newInstance(
        this@MainActivity,
        keyStoreAliasKey = customKeyStoreAliasKey
    )
}
```

- Create field `signatureBiometricPromptManager`

```kotlin
private val signatureBiometricPromptManager by lazy {
    SignatureBiometricPromptManager.newInstance(
        this@MainActivity,
        keyStoreAliasKey = customKeyStoreAliasKey,
        biometricSignature = payloadBiometricSignature,
    )
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
registrationBiometricPromptManager.authenticate(
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
signatureBiometricPromptManager.authenticate(
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