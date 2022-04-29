package com.management.org.dcms.codes.activity

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.management.org.dcms.codes.utility.LanguageManager
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

}
