package com.vlxx.myges

import android.app.Application
import com.vlxx.myges.core.di.appModule
import com.vlxx.myges.core.di.dataModule
import com.vlxx.myges.data.utils.LineNumberDebugTree
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(LineNumberDebugTree())
        }

        startKoin {
            androidContext(this@App)
            modules(
                dataModule,
                appModule
            )
        }
    }
}

