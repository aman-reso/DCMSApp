package com.management.org.dcms.codes.utility

import com.management.org.dcms.codes.models.LanguageDataClass

open class LanguageSettingImpl {

   internal class Builder {
        private var langName = ENGLISH
        private var langCode = EN_CODE

        fun setLanguageName(langName: String) = apply {
            this.langName = langName
        }

        fun setLanguageCode(langCode: String) = apply {
            this.langCode = langCode
        }

        fun build(successCallback: (Boolean) -> Unit) = apply {
            val languageDataClass = LanguageDataClass(langCode = langCode, languageName = langName)
            LanguageManager.setLanguageInfo(languageDataClass = languageDataClass) {
                successCallback.invoke(it)
            }
        }
    }
}
