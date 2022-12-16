package com.prongbang.biometriccram.signature

import java.security.Signature
import javax.crypto.Cipher
import javax.crypto.SecretKey

interface KeyStoreSignature {
    fun getSignature(): Signature
}