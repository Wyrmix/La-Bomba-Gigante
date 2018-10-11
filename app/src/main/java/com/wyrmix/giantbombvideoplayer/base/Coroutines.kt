package com.wyrmix.giantbombvideoplayer.base

import kotlinx.coroutines.experimental.CoroutineExceptionHandler
import kotlinx.coroutines.experimental.newSingleThreadContext
import timber.log.Timber
import kotlin.coroutines.experimental.AbstractCoroutineContextElement
import kotlin.coroutines.experimental.CoroutineContext

class UncaughtCoroutineExceptionHandler: CoroutineExceptionHandler, AbstractCoroutineContextElement(CoroutineExceptionHandler.Key) {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        Timber.e(exception, "Exception [$exception] handled by $this")
    }
}

fun singleThread(name: String): CoroutineContext {
    return newSingleThreadContext(name).plus(UncaughtCoroutineExceptionHandler())
}
