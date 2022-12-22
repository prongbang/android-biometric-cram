package com.prongbang.biometricsignature.keypair

import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey

interface KeyStoreManager {
    fun getPublicKey(key: String): PublicKey
    fun getPrivateKey(key: String): PrivateKey
    fun getKeyPair(key: String): KeyPair
    fun getKeyStore(): KeyStore
}