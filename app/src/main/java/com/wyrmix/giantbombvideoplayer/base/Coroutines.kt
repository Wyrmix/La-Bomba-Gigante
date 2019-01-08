package com.wyrmix.giantbombvideoplayer.base

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.newSingleThreadContext
import timber.log.Timber
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class UncaughtCoroutineExceptionHandler: CoroutineExceptionHandler, AbstractCoroutineContextElement(CoroutineExceptionHandler.Key) {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        Timber.e(exception, "Exception [$exception] handled by $this")
    }
}

fun singleThread(name: String): CoroutineContext {
    return newSingleThreadContext(name).plus(UncaughtCoroutineExceptionHandler())
}
