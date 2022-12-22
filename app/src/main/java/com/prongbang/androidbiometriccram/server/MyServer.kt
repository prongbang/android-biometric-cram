package com.prongbang.androidbiometriccram.server

import android.util.Base64
import java.security.Signature
import java.util.*

class MyServer {

    companion object {
        private var publicKeyMap = hashMapOf<Int, String?>()
        private var challengeMap = hashMapOf<Int, String>()
    }

    fun registration(userId: Int, publicKey: String?): Boolean {
        publicKeyMap[userId] = publicKey
        return true
    }

    fun challengeRequest(userId: Int): String {
        val challenge = UUID.randomUUID().toString()
        challengeMap[userId] = challenge
        return challenge
    }

    fun challengeVerify(userId: Int, signature: String, challenge: String, nonce: String): Boolean {
        val pk = publicKeyMap[userId]!!
            .replace("\r", "")
            .replace("\n", "")
        val publicKey = KeyPairManager.toPublicKey(pk)

        val sign = Signature.getInstance("SHA256withECDSA")
        val textToSign = challenge + nonce
        sign.initVerify(publicKey)
        sign.update(textToSign.toByteArray(Charsets.UTF_8))
        val respByte = Base64.decode(signature, Base64.DEFAULT)
        val verify = sign.verify(respByte)
        val matchChallenge = challengeMap[userId] == challenge

        return verify == matchChallenge
    }

}