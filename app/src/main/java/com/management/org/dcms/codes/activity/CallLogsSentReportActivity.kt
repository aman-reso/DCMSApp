package com.management.org.dcms.codes.activity

import android.Manifest
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import com.management.org.dcms.R
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.CallLog
import android.widget.Button
import com.management.org.dcms.codes.models.UserCallLogsModel
import java.util.ArrayList

class CallLogsSentReportActivity : AppCompatActivity() {
    private var textView: TextView? = null
    private var userCallArrayList: ArrayList<UserCallLogsModel> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calllogs)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), PackageManager.PERMISSION_GRANTED)
        textView = findViewById(R.id.textView)

        findViewById<Button>(R.id.button).setOnClickListener {
            buttonCallLog()
        }

    }

    @SuppressLint("Range")
    fun buttonCallLog() {
        userCallArrayList.clear()
        val uriCallLogs = Uri.parse("content://call_log/calls")
        val cursorCallLogs = contentResolver.query(uriCallLogs, null, null, null)
        cursorCallLogs!!.moveToFirst()
        do {
            val stringNumber = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER))
            val stringName = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME))
            val stringDuration = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DURATION))
            val stringType = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE))
            val stringLocation = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION))
            userCallArrayList.add(UserCallLogsModel(num = stringNumber, dur = stringDuration))

        } while (cursorCallLogs.moveToNext())

        var x = getCallLogsBasedOnMobileNum("8210463654")
    }

    //use this function for getting call log for specific number
    private fun getCallLogsBasedOnMobileNum(mobileNum: String): ArrayList<UserCallLogsModel> {
        if (userCallArrayList.size > 0) {
           val filteredLog=  userCallArrayList.filter {
                it.num.contains(mobileNum)
            } as ArrayList

            return filteredLog
        }
        return ArrayList()
    }
}