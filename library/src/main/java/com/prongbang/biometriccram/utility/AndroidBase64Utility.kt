package com.prongbang.biometriccram.utility

import android.util.Base64
import javax.inject.Inject

class AndroidBase64Utility @Inject constructor() : Base64Utility {

    override fun encode(input: ByteArray): String {
        return Base64.encodeToString(input, Base64.DEFAULT)
    }

    override fun decode(input: String): ByteArray {
        return Base64.decode(input, Base64.DEFAULT)
    }
}