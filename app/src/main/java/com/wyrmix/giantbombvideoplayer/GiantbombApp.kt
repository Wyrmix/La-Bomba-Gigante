package com.wyrmix.giantbombvideoplayer

import android.app.Application
import com.bumptech.glide.request.RequestOptions
import com.facebook.stetho.Stetho
import com.github.s0nerik.glide_bindingadapter.GlideBindingConfig
import com.wyrmix.giantbombvideoplayer.di.appModule
import org.koin.android.ext.android.startKoin
import timber.log.Timber


class GiantbombApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
        startKoin(this, listOf(appModule))
        registerGlideConfigs()

        // todo inject this in debug module and run callback to plant in debug
        Timber.plant(Timber.DebugTree())
    }

    private fun registerGlideConfigs() {
        GlideBindingConfig.registerProvider("default") { iv, request ->
            val options = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)

            request.apply(options)
        }
        GlideBindingConfig.setDefault("default")
    }
}
