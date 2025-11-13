package com.productivitystreak

import android.app.Application
import com.productivitystreak.debug.GlobalExceptionHandler

class NeverZeroApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Thread.setDefaultUncaughtExceptionHandler(GlobalExceptionHandler(this))
    }
}
