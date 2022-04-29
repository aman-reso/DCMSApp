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
import android.view.View
import com.management.org.dcms.codes.models.CallLogClass
import java.util.ArrayList

class CallLogs : AppCompatActivity() {
    private var textView: TextView? = null

    private var callArrayList: ArrayList<CallLogClass> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calllogs)
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.READ_CALL_LOG),
            PackageManager.PERMISSION_GRANTED
        )
        textView = findViewById(R.id.textView)
    }

    @SuppressLint("Range")
    fun buttonCallLog(view: View?) {
        callArrayList.clear()
        val uriCallLogs = Uri.parse("content://call_log/calls")
        val cursorCallLogs = contentResolver.query(uriCallLogs, null, null, null)
        cursorCallLogs!!.moveToFirst()
        do {
            val stringNumber = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER))
            val stringName = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME))
            val stringDuration = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DURATION))
            val stringType = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE))
            val stringLocation = cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION))
            callArrayList.add(CallLogClass(stringNumber, dur = stringDuration))

        } while (cursorCallLogs.moveToNext())

        System.out.println("callArray-->" + callArrayList)
    }

    //use this function for getting call log for specific number
    private fun getCallLogsBasedOnMobileNum(mobileNum: String):ArrayList<CallLogClass> {
        if (callArrayList.size > 0) {
            callArrayList.filter{
                it.num==mobileNum
            }
        }
        return ArrayList()
    }
}