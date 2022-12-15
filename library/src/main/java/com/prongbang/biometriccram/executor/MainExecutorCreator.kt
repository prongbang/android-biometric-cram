package com.prongbang.biometriccram.executor

import android.content.Context
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor
import javax.inject.Inject

class MainExecutorCreator @Inject constructor() : ExecutorCreator {

    override fun create(context: Context): Executor = ContextCompat.getMainExecutor(context)
}