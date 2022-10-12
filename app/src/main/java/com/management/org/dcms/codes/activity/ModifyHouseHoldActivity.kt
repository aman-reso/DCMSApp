package com.management.org.dcms.codes.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.google.gson.JsonObject
import com.management.org.dcms.R
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.ModifyHouseHoldViewModel
import com.management.org.dcms.databinding.ModifyHouseholdLayoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ModifyHouseHoldActivity : BaseActivity() {
    private val houseHoldViewModel: ModifyHouseHoldViewModel by viewModels()
    private var binding: ModifyHouseholdLayoutBinding? = null
    private var hhId: String = "-1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.modify_household_layout)
        if (intent.hasExtra("hhId")) {
            hhId = intent.getIntExtra("hhId", -1)?.toString()
        }
        setUpObservers()

    }

    private fun setClickListeners() {

    }

    private fun setUpHeaders() {
        binding?.containerAppBar?.icNavBackIcon?.showHideView(true)
        binding?.containerAppBar?.icNavBackIcon?.setOnClickListener {
            onBackPressed()
        }
        binding?.containerAppBar?.appBarTitleTV?.text =
            LanguageManager.getStringInfo(R.string.Modify)
    }


    private fun setUpObservers() {
        houseHoldViewModel.apply {
            houseHoldDetailsLiveData.observe(this@ModifyHouseHoldActivity) {
                parseHouseHoldDetailsResponse(it)
            }
        }
        if (hhId == "-1") {
            Utility.showToastMessage("Something went wrong")
        } else {
            houseHoldViewModel.getHouseHoldDetailsById(hhId)
        }
    }

    private fun parseHouseHoldDetailsResponse(response: GlobalNetResponse<JsonObject>?) {
        when (response) {
            is GlobalNetResponse.Success -> {
                val successResponse = response.value
                bindData(successResponse)
            }
            is GlobalNetResponse.NetworkFailure -> {
                Utility.showToastMessage("Something went wrong")
            }
        }
    }

    private fun bindData(successResponse: JsonObject) {
        System.out.println("json-->$successResponse")
    }
}

