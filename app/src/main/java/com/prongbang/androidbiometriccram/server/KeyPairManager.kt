package com.prongbang.androidbiometriccram.server

import com.prongbang.biometriccram.extensions.decodeBase64
import java.security.KeyFactory
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec

object KeyPairManager {

    fun toPublicKey(publicKey: String): PublicKey {
        val pkByteArray = publicKey.decodeBase64()
        return toPublicKey(pkByteArray)
    }

    fun toPublicKey(pkb: ByteArray): PublicKey {
        val fact = KeyFactory.getInstance("EC")
        return fact.generatePublic(X509EncodedKeySpec(pkb))
    }
}