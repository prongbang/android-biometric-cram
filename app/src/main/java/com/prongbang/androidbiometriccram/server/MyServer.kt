package com.prongbang.androidbiometriccram.server

import android.util.Base64
import java.security.Signature
import java.util.*

class MyServer {

    companion object {
        private var publicKeyMap = hashMapOf<Int, String?>()
        private val challengeMap = hashMapOf<Int, String>().apply {
            put(1, "challenge-${UUID.randomUUID().toString()}")
        }
    }

    fun registration(userId: Int, publicKey: String?): Boolean {
        publicKeyMap[userId] = publicKey
        return true
    }

    fun challengeRequest(userId: Int): String {
        return challengeMap[userId] ?: ""
    }

    fun challengeVerify(userId: Int, signature: String, challenge: String, nonce: String): Boolean {
        val publicKey = KeyPairManager.toPublicKey(publicKeyMap[userId]!!)
        val sign = Signature.getInstance("SHA256withECDSA")
        val textToSign = challenge + nonce
        sign.initVerify(publicKey)
        sign.update(textToSign.toByteArray(Charsets.UTF_8))
        val respByte = Base64.decode(signature, Base64.NO_WRAP or Base64.URL_SAFE)
        val verify = sign.verify(respByte)
        val matchChallenge = challengeMap[userId] == challenge

        return verify == matchChallenge
    }

}