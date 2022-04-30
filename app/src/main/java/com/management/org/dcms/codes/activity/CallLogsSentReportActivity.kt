package com.management.org.dcms.codes.activity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.management.org.dcms.R
import com.management.org.dcms.codes.models.UserCallLogsModel
import java.text.SimpleDateFormat
import java.util.*


class CallLogsSentReportActivity : AppCompatActivity() {
    private var textView: TextView? = null
    private var userCallArrayList: ArrayList<UserCallLogsModel> = ArrayList()
    private val calendar: Calendar by lazy { Calendar.getInstance() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calllogs)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), PackageManager.PERMISSION_GRANTED)
        textView = findViewById(R.id.textView)
        calendar.add(Calendar.DAY_OF_YEAR, 0);
        calendar.add(Calendar.HOUR, 0)
        calendar.add(Calendar.MINUTE, 0)

        findViewById<Button>(R.id.button).setOnClickListener {
            getLogsByNumber("8210463654") {
                 if (it.isEmpty()){
                     //empty call log
                 }else{

                 }
            }
        }

    }

    @SuppressLint("Range")
    fun buttonCallLog() {

    }

    private fun getLogsByNumber(strNumber: String, callback: (ArrayList<UserCallLogsModel>) -> Unit) {
        try {
            val list = ArrayList<UserCallLogsModel>()
            val cursor: Cursor? = contentResolver.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ", arrayOf(strNumber), CallLog.Calls.DEFAULT_SORT_ORDER)
            if (cursor?.moveToFirst() == true) {
                while (cursor.moveToNext()) {
                    if (cursor.columnCount > 0) {
                        var date = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))
                        if (calendar.timeInMillis < date) {
                            val number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER))
                            val duration = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION))
                            val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.LOCATION))
                            } else {
                                "not eligible"
                            }
                            val type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                            val callType = getCallTypeAsString(type = type)
                            list.add(UserCallLogsModel(1, number, date.toString(), duration, type.toString(), callType, location))
                        }
                    }
                }
                callback.invoke(list)
            }
        } catch (e: Exception) {
            callback.invoke(ArrayList())
        }
    }

    //use this function for getting call log for specific number
//    private fun getCallLogsBasedOnMobileNum(mobileNum: String): ArrayList<UserCallLogsModel> {
//        if (userCallArrayList.size > 0) {
//            val filteredLog = userCallArrayList.filter {
//                it..contains(mobileNum)
//            } as ArrayList

    //  return filteredLog
//        }
//        return ArrayList()
//    }

    private fun getCallTypeAsString(type: Int): String {
        when (type) {
            1 -> return "incoming type"
            2 -> return "outgoing type"
            3 -> return " missed type"
        }
        return "not mapped"
    }

}