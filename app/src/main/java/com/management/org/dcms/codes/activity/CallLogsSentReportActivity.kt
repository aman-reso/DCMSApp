package com.management.org.dcms.codes.activity

import android.Manifest
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.adapter.CallTestAdapter
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.CallSentReportResponse
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.codes.utility.NetworkImpl
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.HomeViewModel
import com.management.org.dcms.databinding.CallTestBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class CallLogsSentReportActivity : AppCompatActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    private var binding: CallTestBinding? = null

    private var mRecyclerView: RecyclerView? = null
    private var mProgressBar: ProgressBar? = null
    private var fromDateTV: TextView? = null
    private var toDateTV: TextView? = null
    private var getResultTV: TextView? = null

    private val callLogsAdapter: CallTestAdapter by lazy { CallTestAdapter() }

    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private var year = calendar.get(Calendar.YEAR)
    private var month: Int = calendar.get(Calendar.MONTH)
    private var day: Int = calendar.get(Calendar.DATE)
    private var fromDateString: String = ""
    private var toDateString: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.call_test)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CALL_LOG),
            PackageManager.PERMISSION_GRANTED
        )
        setUpHeaders()
        setUpClickListener()
        setUpObservers()
    }

    private fun setUpClickListener() {
        binding?.apply {
            mRecyclerView = reportRecyclerView
            mProgressBar = progressBar
            toDateTV = selectToDateTV
            fromDateTV = selectFromDateTV
            getResultTV = getResultActionBtn
        }

        mRecyclerView?.adapter = callLogsAdapter

        fromDateTV?.setOnClickListener {
            showDateDialog(FROM_DATE)
        }
        toDateTV?.setOnClickListener {
            showDateDialog(TO_DATE)
        }
        binding?.getResultActionBtn?.setOnClickListener {
            getResultFromServer()
        }
    }

    private fun setUpHeaders() {
        binding?.containerAppBar?.icNavBackIcon?.showHideView(true)
        binding?.containerAppBar?.icNavBackIcon?.setOnClickListener {
            onBackPressed()
        }
        binding?.containerAppBar?.appBarTitleTV?.text ="Call Report"
    }
    private fun getResultFromServer(){
        if (NetworkImpl.checkNetworkStatus()) {
            if (validateInput()) {
                mProgressBar?.showHideView(true)
                homeViewModel.getCallLogsReport(fromDateString,toDateString)
            }
        }
    }

    private fun setUpObservers() {
        homeViewModel.apply {
            callLogSentReportLiveData.observe(this@CallLogsSentReportActivity) {
                mProgressBar?.showHideView(false)
                parseResponse(it)
            }
        }
    }

    private fun showDateDialog(id: Int) {
        showDialog(id)
    }

    override fun onCreateDialog(id: Int): Dialog? {
        return when (id) {
            FROM_DATE -> {
                DatePickerDialog(this, myDateListenerFromDate, year, month, day)
            }
            TO_DATE -> {
                DatePickerDialog(this, myDateListenerToDate, year, month, day)
            }
            else -> {
                null
            }
        }
    }

    private val myDateListenerFromDate = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        val formattedDate = Utility.getDateAsRequiredFormat(year, month, day)
        fromDateString = formattedDate
        updateFromDate(fromDateString)
    }
    private val myDateListenerToDate = DatePickerDialog.OnDateSetListener { _, year, month, day ->
        val formattedDate = Utility.getDateAsRequiredFormat(year, month, day)
        toDateString = formattedDate
        updateToDate(toDateString)
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

    private fun updateFromDate(date: String) {
        binding?.selectFromDateTV?.text = date
    }

    private fun updateToDate(date: String) {
        binding?.selectToDateTV?.text = date
    }

    private fun validateInput(): Boolean {
        if (fromDateString != "") {
            if (toDateString != "") {
                return true
            } else {
                Utility.showToastMessage(LanguageManager.getStringInfo(R.string.please_select_to_date))
            }
        } else {
            Utility.showToastMessage(LanguageManager.getStringInfo(R.string.please_select_from_date))
        }
        return false
    }


}