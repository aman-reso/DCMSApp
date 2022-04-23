package com.management.org.dcms.codes.activity

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.forEachIndexed
import androidx.databinding.DataBindingUtil
import com.management.org.dcms.R
import com.management.org.dcms.codes.HomeLandingMainActivity
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.utility.*
import com.management.org.dcms.codes.viewmodel.ProfileViewModel
import com.management.org.dcms.databinding.ActivityChangeLanguageBinding
import com.management.org.dcms.databinding.ActivityChangePasswordBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class ChangeLanguageActivity : BaseActivity() {

    private var parentBinding: ActivityChangeLanguageBinding? = null
    private var radioGroup: RadioGroup? = null
    private var changeLanguageActionBtn: AppCompatButton? = null
    private val viewModel: ProfileViewModel? by viewModels()
    private var defaultLanguageCode: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_language)
        setUpHeaders()
        setUpViews()
        setUpClickListeners()
        setUpObservers()
    }

    private fun setUpHeaders() {
        parentBinding?.containerAppBar?.icNavBackIcon?.showHideView(true)
        parentBinding?.containerAppBar?.icNavBackIcon?.setOnClickListener {
            onBackPressed()
        }
        parentBinding?.containerAppBar?.appBarTitleTV?.text = getString(R.string.choose_language)
    }

    private fun setUpViews() {
        radioGroup = parentBinding?.languageRadioGroup
        changeLanguageActionBtn = parentBinding?.changeLanguageActionBtn
    }

    private fun setUpClickListeners() {
        changeLanguageActionBtn?.setOnClickListener {
            when (radioGroup?.checkedRadioButtonId) {
                R.id.hindiLanguageBtn -> {
                    changeLanguage(HI_CODE, HINDI)
                }
                R.id.englishLanguageBtn -> {
                    changeLanguage(EN_CODE, ENGLISH)
                }
                R.id.urduLanguageBtn -> {
                    changeLanguage(URDU_CODE, URDU)
                }
                else -> {

                }
            }
        }
    }

    private fun changeLanguage(languageCode: String, langName: String) {
        if (defaultLanguageCode?.equals(languageCode) == false) {
            val config = resources.configuration
            val locale = Locale(languageCode)
            Locale.setDefault(locale)
            config.setLocale(locale)
            LanguageSettingImpl.Builder().setLanguageCode(langCode = languageCode).setLanguageName(langName = langName).build {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                    createConfigurationContext(config)
                resources.updateConfiguration(config, resources.displayMetrics)
            }
        }
        LanguageManager.setDoesShownLanguageSettingFirstTime(true)
        navigateForward()
    }

    private fun setUpObservers() {
        viewModel?.languageCodeLiveData?.observe(this) { langCode ->
            defaultLanguageCode = langCode
            when (langCode) {
                EN_CODE -> {
                    setRadioGroupBasedOnId(R.id.englishLanguageBtn)
                }
                HI_CODE -> {
                    setRadioGroupBasedOnId(R.id.hindiLanguageBtn)
                }
                URDU_CODE -> {
                    setRadioGroupBasedOnId(R.id.urduLanguageBtn)
                }
            }
        }
        viewModel?.getDefaultLanguageSelectedByUser()
    }

    private fun setRadioGroupBasedOnId(id: Int) {
        radioGroup?.check(id)
    }

    private fun navigateForward() {
        if (Utility.isUserLoggedIn()) {
            startHomeLandingActivity()
        } else {
            startLoginActivity()
        }
    }

    private fun startHomeLandingActivity() {
        val homeLandingIntent = Intent(this, HomeLandingMainActivity::class.java)
        startActivity(homeLandingIntent)
        finish()
    }

    private fun startLoginActivity() {
        val homeLandingIntent = Intent(this, LoginActivity::class.java)
        startActivity(homeLandingIntent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}