package com.wyrmix.giantbombvideoplayer

import android.app.Application
import com.bumptech.glide.request.RequestOptions
import com.facebook.stetho.Stetho
import com.github.s0nerik.glide_bindingadapter.GlideBindingConfig
import com.wyrmix.giantbombvideoplayer.di.appModule
import com.wyrmix.giantbombvideoplayer.di.databaseModule
import com.wyrmix.giantbombvideoplayer.di.networkModule
import org.koin.android.ext.android.startKoin
import org.koin.standalone.KoinComponent
import timber.log.Timber


class GiantbombApp : Application(), KoinComponent {

    override fun onCreate() {
        super.onCreate()

        Stetho.initializeWithDefaults(this)
        startKoin(this, listOf(networkModule, databaseModule, appModule))
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
