package com.management.org.dcms.codes.activity

import android.Manifest
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

    private var callArray: ArrayList<CallLogClass> = ArrayList()

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

    fun buttonCallLog(view: View?) {
        textView!!.text = "Call Logging Started ... "
        var stringOutput = ""
        val uriCallLogs = Uri.parse("content://call_log/calls")
        val cursorCallLogs = contentResolver.query(uriCallLogs, null, null, null)
        cursorCallLogs!!.moveToFirst()
        do {
            val stringNumber =
                cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.NUMBER))
            val stringName =
                cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.CACHED_NAME))
            val stringDuration =
                cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.DURATION))
            val stringType =
                cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.TYPE))
            val stringLocation =
                cursorCallLogs.getString(cursorCallLogs.getColumnIndex(CallLog.Calls.GEOCODED_LOCATION))
            stringOutput = """${stringOutput} Number: $stringNumber
                                             Name: $stringName
                                             Duration: $stringDuration
                                             Type: $stringType
                                             Location: $stringLocation
                           """
        } while (cursorCallLogs.moveToNext())
        textView!!.text = stringOutput
    }
}