package com.management.org.dcms.codes.activity

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.management.org.dcms.R
import com.management.org.dcms.codes.dcmsclient.viewitem.ViewItemActivity
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.databinding.ViewEntriesBinding

class ViewEntriesActivity : BaseActivity() {
    var binding:ViewEntriesBinding?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.view_entries)

        val textView = findViewById<View>(R.id.viewEntries) as TextView
        textView.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        setUpHeaders()
        onClickListeners()
    }

    private fun setUpHeaders() {
        binding?.containerAppBar?.icNavBackIcon?.showHideView(true)
        binding?.containerAppBar?.icNavBackIcon?.setOnClickListener {
            onBackPressed()
        }
        binding?.containerAppBar?.appBarTitleTV?.text = LanguageManager.getStringInfo(R.string.view_entries)
    }


    private fun onClickListeners()
    {
        var seeAddedHouseHoldTv = findViewById<TextView>(R.id.seeAddedHouseHoldTv)
        seeAddedHouseHoldTv.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                startSeeAddedHouseHoldIntent()
            }
        })

        var seeQReportDetailActionBtn = findViewById<TextView>(R.id.seeQReportDetailActionBtn)
        seeQReportDetailActionBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                seeSentReport(Q_REPORT_INTENT)
            }
        })

        var seeWAReportDetailActionBtn = findViewById<TextView>(R.id.seeWAReportDetailActionBtn)
        seeWAReportDetailActionBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                seeSentReport(WA_REPORT_INTENT)
            }
        })

        var seeTextReportDetailActionBtn = findViewById<TextView>(R.id.seeTextReportDetailActionBtn)
        seeTextReportDetailActionBtn.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View?) {
                seeSentReport(TEXT_REPORT_INTENT)
            }
        })

        var callReport = findViewById<TextView>(R.id.callReport)
        callReport.setOnClickListener { callReportClass() }
    }

    private fun startSeeAddedHouseHoldIntent() {
        val intent = Intent(this, ViewItemActivity::class.java)
        startActivity(intent)
    }

    private fun seeSentReport(key: String) {
        val intent = Intent(this, QSentReportDetailActivity::class.java)
        intent.putExtra(key, key)
        startActivity(intent)
    }

    private fun callReportClass(){
        val intent = Intent(this, CallLogsSentReportActivity::class.java)
        startActivity(intent)
    }

}