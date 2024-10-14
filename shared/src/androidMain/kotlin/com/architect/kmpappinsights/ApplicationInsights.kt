package com.architect.kmpappinsights.services

import android.app.Application
import com.architect.kmpessentials.KmpAndroid

class ApplicationInsights {
    companion object {
        fun initialize(appContext: Application) {
            KmpAndroid.preRegisterApplicationContext(appContext)
        }
    }
}