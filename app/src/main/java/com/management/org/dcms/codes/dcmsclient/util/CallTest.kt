package com.management.org.dcms.codes.dcmsclient.util

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.management.org.dcms.R

class CallTest :  AppCompatActivity() {




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_test)



    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun buttonCallLog(view: View?) {
        val textView5 : TextView = findViewById(R.id.textView)
        val mobileNo : TextView = findViewById(R.id.mobileNo)
        textView5.setText("Call Logging Started ... ")
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

            if(stringNumber == ""+mobileNo.text.toString() || stringNumber == "+91"+mobileNo.text.toString() ){
                stringOutput = """${stringOutput}Number: $stringNumber
                                    Name: $stringName
                                    Duration: $stringDuration
                                    Type: $stringType
                                    Location: $stringLocation
                                                               """

            }
     } while (cursorCallLogs.moveToNext())

        textView5.setText(stringOutput)
    }
    }


