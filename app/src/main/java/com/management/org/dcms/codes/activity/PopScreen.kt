package com.management.org.dcms.codes.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.management.org.dcms.R
import com.management.org.dcms.codes.dcmsclient.viewitem.ViewItemActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PopScreen : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.popup_window)

    }

    fun btnCancelEvent (view: View?){
       val intent = Intent(this, ViewItemActivity::class.java)
        startActivity(intent)
        finish()

    }

}

