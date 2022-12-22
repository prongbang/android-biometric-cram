package com.prongbang.biometricsignature.signature

import com.prongbang.biometricsignature.key.KeyStoreAliasKey
import com.prongbang.biometricsignature.keypair.KeyStoreManager
import java.security.Signature
import javax.inject.Inject

class BiometricKeyStoreSignature @Inject constructor(
    private val keyStoreAliasKey: KeyStoreAliasKey,
    private val keyStoreManager: KeyStoreManager
) : KeyStoreSignature {

    override fun getSignature(): Signature {
        val privateKey = keyStoreManager.getPrivateKey(keyStoreAliasKey.key())
        val signature = Signature.getInstance("SHA256withECDSA")
        signature.initSign(privateKey)
        return signature
    }

}