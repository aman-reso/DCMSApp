package com.management.org.dcms.codes.activity


import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.RecyclerView
import com.management.org.dcms.R
import com.management.org.dcms.codes.adapter.CallListAdapter
import com.management.org.dcms.codes.extensions.showHideView
import com.management.org.dcms.codes.models.QContactsMainModel
import com.management.org.dcms.codes.models.QContactsModel
import com.management.org.dcms.codes.models.UserCallLogsModel
import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.codes.utility.Utility
import com.management.org.dcms.codes.viewmodel.MessageTemplateViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CallDetailsActivity : BaseActivity() {
    private val messageTemplateViewModel: MessageTemplateViewModel? by viewModels()
    private val contactsListAdapter: CallListAdapter by lazy { CallListAdapter(callback = callback) }
    private var callContactsRecyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    private var textView: TextView? = null
    private var userCallArrayList: ArrayList<UserCallLogsModel> = ArrayList()
    private val calendar: Calendar by lazy { Calendar.getInstance() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.call_detail)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CALL_LOG), PackageManager.PERMISSION_GRANTED)
        val dateNow = Date()
        calendar.time = dateNow
        calendar[Calendar.HOUR_OF_DAY] = 0
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        calendar[Calendar.MILLISECOND] = 0
        getDataFromIntent()
        setUpViews()
        setUpObservers()

    }

    private fun getCallLogForParticularNumber() {
        //+91 is mandatory
        getLogsByNumber("+91 8210463654") {
            if (it.isEmpty()) {
                //empty call log
            } else {
                messageTemplateViewModel?.submitCallReport(it)
            }
        }
    }

    private fun getDataFromIntent() {
        try {
            if (intent != null && intent.data != null) {
                //urlToBeLoad = intent.getStringExtra(URL_TO_BE_LOAD)
            }
        } catch (e: Exception) {
            Utility.showToastMessage(LanguageManager.getStringInfo(R.string.something_went_wrong))
            finish()
        }
    }

    private fun setUpViews() {
        callContactsRecyclerView = findViewById(R.id.callContactsRecyclerView)
        callContactsRecyclerView?.adapter = contactsListAdapter
        progressBar = findViewById(R.id.progressBar)

        val navIcon: ImageView? = findViewById(R.id.icNavBackIcon)
        navIcon?.showHideView(true)
        navIcon?.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setUpObservers() {
        messageTemplateViewModel?.apply {
            wAMessageTemplateLiveData.observe(this@CallDetailsActivity) { response ->
                if (response != null) {
                    //  parseNetworkResponseForMessageTemplate(response)
                }
            }
            qContactListLiveData.observe(this@CallDetailsActivity) { networkResponse ->
                if (networkResponse != null) {
                    progressBar?.showHideView(false)
                    parseNetworkResponse(networkResponse)
                }
            }
        }
        progressBar?.showHideView(true)
        messageTemplateViewModel?.getContactsListForQuestion()
    }

    private fun parseNetworkResponse(networkResponse: GlobalNetResponse<QContactsMainModel>?) {
        progressBar?.showHideView(false)
        when (networkResponse) {
            is GlobalNetResponse.NetworkFailure -> {
                Utility.showToastMessage(LanguageManager.getStringInfo(R.string.something_went_wrong))
            }
            is GlobalNetResponse.Success -> {
                val contactsList = networkResponse.value
                if (contactsList?.qContactsList != null) {
                    contactsListAdapter.submitData(contactsList = contactsList?.qContactsList!!)
                }
            }
        }
    }

    private var callback = fun(contactsMainModel: QContactsModel) {
        if (contactsMainModel.MobileNo != "") {
//            val intent = Intent(Intent.ACTION_DIAL)
//            intent.data = Uri.parse("tel:${contactsMainModel.MobileNo}")
//            startActivity(intent)
            getCallLogForParticularNumber()
        }
    }

    private fun getLogsByNumber(strNumber: String, callback: (ArrayList<UserCallLogsModel>) -> Unit) {
        try {
            val list = ArrayList<UserCallLogsModel>()
            val cursor: Cursor? = contentResolver.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ", arrayOf(strNumber), CallLog.Calls.DATE)
            if (cursor?.moveToFirst() == true) {
                while (cursor.moveToNext()) {
                    if (cursor.columnCount > 0) {
                        val dateInLong = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))
                        val date=getDate(dateInLong)
                        System.out.println("date-->"+date)
                        if (calendar.timeInMillis < dateInLong) {
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
        } catch (e: java.lang.Exception) {
            callback.invoke(ArrayList())
        }
    }

    private fun getCallTypeAsString(type: Int): String {
        when (type) {
            1 -> return "incoming type"
            2 -> return "outgoing type"
            3 -> return " missed type"
        }
        return "not mapped"
    }

    private fun getDate(milliSeconds: Long): String {
        try {
            val formatter = SimpleDateFormat(DATE_FORMAT, Locale.US)
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }catch (e:java.lang.Exception){
            return milliSeconds.toString()
        }
    }



}
const val DATE_FORMAT="dd/MM/yyyy hh:mm:ss.SSS"