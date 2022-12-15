package com.prongbang.biometriccram.keypair

import java.security.PrivateKey
import java.security.PublicKey

interface KeyStoreManager {
    fun getPublicKey(key: String): PublicKey
    fun getPrivateKey(key: String): PrivateKey
}