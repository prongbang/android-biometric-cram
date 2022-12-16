package com.prongbang.biometriccram.signature

abstract class BiometricSignature {
    abstract fun challenge(): String
    open fun nonce(): String = ""
}