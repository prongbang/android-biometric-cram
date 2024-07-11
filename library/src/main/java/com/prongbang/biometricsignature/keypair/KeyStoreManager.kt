package com.prongbang.biometricsignature.keypair

import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey

interface KeyStoreManager {
    fun getPublicKey(key: String): PublicKey
    fun getPrivateKey(key: String, invalidatedByBiometricEnrollment: Boolean): PrivateKey
    fun getKeyPair(key: String, invalidatedByBiometricEnrollment: Boolean): KeyPair
    fun generateKeyPair(key: String, invalidatedByBiometricEnrollment: Boolean): KeyPair
    fun deleteKeyPair(key: String): Boolean
    fun getKeyStore(): KeyStore
}