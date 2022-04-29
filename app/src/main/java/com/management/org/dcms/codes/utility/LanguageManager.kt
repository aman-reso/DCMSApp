package com.management.org.dcms.codes.utility

import android.content.Context
import android.os.Build
import com.management.org.dcms.codes.DcmsApplication
import com.management.org.dcms.codes.authConfig.AuthConfigManager
import com.management.org.dcms.codes.models.LanguageDataClass
import java.util.*

const val ENGLISH = "English"
const val HINDI = "Hindi"
const val EN_CODE = "en"
const val HI_CODE = "hi"
const val URDU = "Urdu"
const val URDU_CODE = "ur"

object LanguageManager {
    private val sharedPref by lazy { DcmsApplication.getDcmsAppContext()?.getSharedPreferences(AuthConfigManager.shared_pref_location, Context.MODE_PRIVATE) }
    private val langName: String = "langName"
    private val langCode: String = "langCode"
    private val doesShownLanguageSettingFirstTime = "doesShownLangSettingFirstTime"

    fun setLanguageInfo(languageDataClass: LanguageDataClass, callback: (Boolean) -> Unit) {
        sharedPref?.edit()?.putString(langName, languageDataClass.languageName)?.apply()
        sharedPref?.edit()?.putString(langCode, languageDataClass.langCode)?.apply()
        callback.invoke(true)
    }

    fun getLanguageCode(): String {
        return sharedPref?.getString(langCode, EN_CODE) ?: EN_CODE
    }

    fun getLanguageName(): String {
        return sharedPref?.getString(langCode, ENGLISH) ?: ENGLISH
    }

    fun setDoesShownLanguageSettingFirstTime(isShown: Boolean) {
        sharedPref?.edit()?.putBoolean(doesShownLanguageSettingFirstTime, isShown)?.apply()
    }

    fun getShownLangSettingFirstTime(): Boolean {
        return sharedPref?.getBoolean(doesShownLanguageSettingFirstTime, false) ?: false
    }

     fun getStringInfo(int: Int):String {
        return DcmsApplication.getDcmsAppContext()?.getString(int)?:""
    }
     fun setUpLanguage(languageCode: String,context: Context) {
        val config = context.resources.configuration
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        config.setLocale(locale)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.createConfigurationContext(config)
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}