package com.desmond.codelab.core

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class AppExecutor(
    val dbExecutor: Executor,
    val networkExecutor: Executor,
    val mainThread: Executor
) {
    @Inject
    constructor() : this(
        Executors.newSingleThreadExecutor(),
        Executors.newFixedThreadPool(4),
        object :
            Executor {
            val mainLooper = Handler(Looper.getMainLooper())
            override fun execute(action: Runnable?) {
                mainLooper.post(action ?: return)
            }
        }
    )
}
