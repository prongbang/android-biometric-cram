package com.prongbang.biometricsignature.signature

abstract class BiometricSignature {
    abstract fun challenge(): String
    open fun nonce(): String = ""
}