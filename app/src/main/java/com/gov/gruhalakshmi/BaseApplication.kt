package com.gov.gruhalakshmi

import android.app.Application
import android.content.res.Configuration
import androidx.core.app.ActivityCompat.recreate
import com.gov.gruhalakshmi.common.PreferenceHelper
import java.util.Locale

class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()

    }

    fun getLanguageCode():String {
        val prefs = PreferenceHelper.defaultPrefs(this)
        val languageCode = prefs.getString("LAN","en")
        return languageCode!!
    }
}