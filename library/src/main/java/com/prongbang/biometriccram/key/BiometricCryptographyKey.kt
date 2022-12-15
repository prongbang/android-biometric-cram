package com.prongbang.biometriccram.key

import javax.inject.Inject

class BiometricCryptographyKey @Inject constructor() : CryptographyKey {
    override fun key(): String = "SECURE_BIOMETRIC"
}