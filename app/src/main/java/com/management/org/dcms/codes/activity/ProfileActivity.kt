package com.management.org.dcms.codes.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import com.management.org.dcms.R
import com.management.org.dcms.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private var binding: ActivityProfileBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)

        setUpViews()
    }

    private fun setUpViews() {
        binding?.containerAppBar?.toolbar?.visibility = View.GONE
        binding?.containerAppBar?.icNavBackIcon?.visibility = View.VISIBLE
        binding?.containerAppBar?.appBarTitleTV?.visibility = View.VISIBLE

        binding?.containerAppBar?.icNavBackIcon?.setOnClickListener { onBackPressed() }
    }
}