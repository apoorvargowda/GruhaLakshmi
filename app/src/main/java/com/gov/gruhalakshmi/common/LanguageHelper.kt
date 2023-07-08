package com.gov.gruhalakshmi.common

import android.content.Context
import android.content.res.Configuration
import java.util.Locale


object LanguageHelper {
    fun wrapContext(context: Context, languageCode: String?): Context {
        val configuration = Configuration(context.resources.configuration)
        val newLocale = Locale(languageCode)
        configuration.setLocale(newLocale)
        return context.createConfigurationContext(configuration)
    }
}