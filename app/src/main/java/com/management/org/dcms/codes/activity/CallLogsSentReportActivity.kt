package com.management.org.dcms.codes.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.codes.adapter.CallTestAdapter
import com.management.org.dcms.codes.models.CallSentReportResponse
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.HomeViewModel
import com.management.org.dcms.databinding.CallTestBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CallLogsSentReportActivity : AppCompatActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private var binding: CallTestBinding? = null
    private var recyclerView: RecyclerView? = null

    private val callLogsAdapter: CallTestAdapter by lazy { CallTestAdapter() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = CallTestBinding.inflate(layoutInflater)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CALL_LOG),
            PackageManager.PERMISSION_GRANTED
        )

        setUpClickListener()
        setUpObservers()
    }

    private fun setUpClickListener() {
        recyclerView = binding?.recyclerView
        recyclerView?.adapter = callLogsAdapter
    }

    private fun setUpObservers() {
        homeViewModel.apply {
            callLogSentReportLiveData.observe(this@CallLogsSentReportActivity) {
                parseResponse(it)
            }
            getCallLogsReport()
        }
    }

    private fun parseResponse(it: GlobalNetResponse<CallSentReportResponse>?) {
        when (it) {
            is GlobalNetResponse.Success -> {
                val response = it.value
                if (response.callLogReportModels?.isNotEmpty() == true) {
                    callLogsAdapter.submitCallLogs(response.callLogReportModels!!)
                } else {
                    Utility.showToastMessage("No call logs report found")
                }
            }
            else -> {
                Utility.showToastMessage("Something went wrong")
            }
        }
    }

}