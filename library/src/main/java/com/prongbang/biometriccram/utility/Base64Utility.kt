package com.prongbang.biometriccram.utility

interface Base64Utility {
    fun encode(input: ByteArray): String
    fun decode(input: String): ByteArray
}