package com.sandeep.readnfc

import android.app.Application
import com.facebook.flipper.plugins.leakcanary2.FlipperLeakEventListener
import leakcanary.LeakCanary

class ReadNfcApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        LeakCanary.config = LeakCanary.config.run {
            copy(eventListeners = eventListeners + FlipperLeakEventListener())
        }
        if (BuildConfig.DEBUG) {
            FlipperInitializr.getInstance(applicationContext);
        }
    }
}