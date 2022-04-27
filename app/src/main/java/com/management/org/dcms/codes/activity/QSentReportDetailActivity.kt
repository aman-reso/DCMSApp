package com.management.org.dcms.codes.activity

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.app.Dialog
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.adapter.QReportAdapter
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.QReportDetailModel
import com.management.org.dcms.codes.models.TaskItem
import com.management.org.dcms.codes.models.TextReportDetailModel
import com.management.org.dcms.codes.models.WAReportDetailModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.LanguageManager.getStringInfo
import com.management.org.dcms.codes.utility.NetworkImpl
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.utility.Utility.getDateAsRequiredFormat
import com.management.org.dcms.codes.viewmodel.ReportListViewModel
import com.management.org.dcms.databinding.ActivityQsentReportDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

const val FROM_DATE = 1
const val TO_DATE = 2

const val Q_REPORT_INTENT = "4"
const val WA_REPORT_INTENT = "5"
const val TEXT_REPORT_INTENT = "6"

@AndroidEntryPoint
class QSentReportDetailActivity : AppCompatActivity() {
    private var parentBinding: ActivityQsentReportDetailBinding? = null
    private var mRecyclerView: RecyclerView? = null
    private var mProgressBar: ProgressBar? = null
    private var fromDateTV: TextView? = null
    private var toDateTV: TextView? = null
    private var getResultTV: TextView? = null
    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private var year = calendar.get(Calendar.YEAR)
    private var month: Int = calendar.get(Calendar.MONTH)
    private var day: Int = calendar.get(Calendar.DATE)
    private var fromDateString: String = ""
    private var toDateString: String = ""

    private val viewModel: ReportListViewModel by viewModels()
    private val mAdapter: QReportAdapter by lazy { QReportAdapter() }
    private var isQReport = false
    private var isWAReport = false
    private var isTextReport = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parentBinding = DataBindingUtil.setContentView(this, R.layout.activity_qsent_report_detail)
        try {
            getDataFromIntent()

        } catch (e: Exception) {
            e.printStackTrace()
        }
        setUpViews()
        setUpHeaders()
        setUpListeners()
    }


    private fun setUpViews() {
        parentBinding?.apply {
            mRecyclerView = reportRecyclerView
            mProgressBar = progressBar
            toDateTV = selectToDateTV
            fromDateTV = selectFromDateTV
            getResultTV = getResultActionBtn
        }
        mRecyclerView?.adapter = mAdapter
    }

    private fun getDataFromIntent() {
        when {
            intent.hasExtra(Q_REPORT_INTENT) -> {
                isQReport = true
            }
            intent.hasExtra(WA_REPORT_INTENT) -> {
                isWAReport = true
            }
            intent.hasExtra(TEXT_REPORT_INTENT) -> {
                isTextReport = true
            }
            else -> {
                Utility.showToastMessage(getStringInfo(R.string.something_went_wrong))
                finish()
            }
        }
    }

    private fun setUpHeaders() {
        parentBinding?.containerAppBar?.icNavBackIcon?.showHideView(true)
        parentBinding?.containerAppBar?.icNavBackIcon?.setOnClickListener {
            onBackPressed()
        }
        parentBinding?.containerAppBar?.appBarTitleTV?.text = getTitleValue()
    }

    private fun setUpListeners() {
        fromDateTV?.setOnClickListener {
            showDateDialog(FROM_DATE)
        }
        toDateTV?.setOnClickListener {
            showDateDialog(TO_DATE)
        }
        getResultTV?.setOnClickListener {
            when {
                isWAReport -> {
                    setUpNetworkCall(WA_REPORT_INTENT)
                    return@setOnClickListener
                }
                isTextReport -> {
                    setUpNetworkCall(TEXT_REPORT_INTENT)
                    return@setOnClickListener
                }
                isQReport -> {
                    setUpNetworkCall(Q_REPORT_INTENT)
                    return@setOnClickListener
                }
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

    private val myDateListenerFromDate = OnDateSetListener { _, year, month, day ->
        val formattedDate = getDateAsRequiredFormat(year, month, day)
        fromDateString = formattedDate
        updateFromDate(fromDateString)
    }
    private val myDateListenerToDate = OnDateSetListener { _, year, month, day ->
        val formattedDate = getDateAsRequiredFormat(year, month, day)
        toDateString = formattedDate
        updateToDate(toDateString)
    }

    private fun setUpNetworkCall(reqForWhich: String) {
        if (NetworkImpl.checkNetworkStatus()) {
            if (validateInput()) {
                mProgressBar?.showHideView(true)
                viewModel.getQSentDetails(fromDate = fromDateString, toDate = toDateString, reqForWhich) { networkResponse ->
                    lifecycleScope.launch(Dispatchers.Main) {
                        mProgressBar?.showHideView(false)
                        parseNetworkResponse(networkResponse)
                    }
                }
            }
        }
    }

    private fun parseNetworkResponse(networkResponse: GlobalNetResponse<*>) {
        when (networkResponse) {
            is GlobalNetResponse.Success -> {
                when (val successRes = networkResponse.value) {
                    is WAReportDetailModel -> {
                        if (successRes.taskListItem.isNullOrEmpty()) {
                            Utility.showToastMessage(getStringInfo(R.string.no_report_found))
                            return
                        }
                        setUpItemWithRecyclerView(successRes.taskListItem!!)
                    }
                    is TextReportDetailModel -> {
                        if (successRes.taskListItem.isNullOrEmpty()) {
                            Utility.showToastMessage(getStringInfo(R.string.no_report_found))
                            return
                        }
                        setUpItemWithRecyclerView(successRes.taskListItem!!)
                    }
                    is QReportDetailModel -> {
                        if (successRes.taskListItem.isNullOrEmpty()) {
                            Utility.showToastMessage(getStringInfo(R.string.no_report_found))
                            return
                        }
                        setUpItemWithRecyclerView(successRes.taskListItem!!)
                    }
                }
            }
            is GlobalNetResponse.NetworkFailure -> {
                val errMessage = networkResponse.error
                Utility.showToastMessage(errMessage)
            }
        }
    }

    private fun setUpItemWithRecyclerView(taskListItem: ArrayList<TaskItem>) {
        mAdapter.submitList(taskListItem)
    }

    private fun updateFromDate(date: String) {
        fromDateTV?.text = date
    }

    private fun updateToDate(date: String) {
        toDateTV?.text = date
    }

    private fun validateInput(): Boolean {
        if (fromDateString != "") {
            if (toDateString != "") {
                return true
            } else {
                Utility.showToastMessage(getStringInfo(R.string.please_select_to_date))
            }
        } else {
            Utility.showToastMessage(getStringInfo(R.string.please_select_from_date))
        }
        return false
    }

    private fun getTitleValue(): String? {
        return when {
            isQReport -> {
                "Q Report"
            }
            isWAReport -> {
                "WA Report"
            }
            isTextReport -> {
                "Text Report"
            }
            else -> {
                null
            }
        }
    }
}