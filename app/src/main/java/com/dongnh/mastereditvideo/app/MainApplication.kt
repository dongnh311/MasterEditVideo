package com.dongnh.mastereditvideo.app

import android.app.Application
import com.dongnh.mastereditvideo.BuildConfig
import timber.log.Timber

/**
 * Project : MasterEditVideo
 * Created by DongNH on 05/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        setupTimber()
    }

    /**
     * Setup log by timber
     */
    private fun setupTimber() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}