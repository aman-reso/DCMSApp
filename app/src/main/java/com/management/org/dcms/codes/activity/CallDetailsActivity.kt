package com.management.org.dcms.codes.activity


import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.management.org.dcms.codes.viewmodel.FAILURE
import com.management.org.dcms.codes.viewmodel.MessageTemplateViewModel
import com.management.org.dcms.codes.viewmodel.SUCCESS
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


@AndroidEntryPoint
class CallDetailsActivity : BaseActivity() {
    private val messageTemplateViewModel: MessageTemplateViewModel? by viewModels()
    private val contactsListAdapter: CallListAdapter by lazy { CallListAdapter(callback = callback) }
    private var callContactsRecyclerView: RecyclerView? = null
    private var progressBar: ProgressBar? = null

    private val calendar: Calendar by lazy { Calendar.getInstance() }
    private var itemPosition: Int? = -1
    private var arrayList: ArrayList<QContactsModel> = arrayListOf()

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
        checkPermission(Manifest.permission.READ_CALL_LOG, PERMISSION_CODE_CALL_LOG)
    }

    private fun getCallLogForParticularNumber(mobileNo: String, hhId: Int, position: Int) {
        //+91 is mandatory
        val numberWithCountryCode: String = "+91$mobileNo"
        getLogsByNumber(numberWithCountryCode, mobileNo, hhId) {
            if (it.isEmpty()) {
                Utility.showToastMessage("No call log detects")
                //empty call log
            } else {
                showLoader(true)
                itemPosition = position
                messageTemplateViewModel?.submitCallReport(it, hhId)
            }
        }
    }

    private fun checkPermission(permission: String, requestCode: Int) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        } else {
            Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLoader(canShow: Boolean) {
        progressBar?.showHideView(canShow = canShow)
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
            qContactListLiveData.observe(this@CallDetailsActivity) { networkResponse ->
                if (networkResponse != null) {
                    progressBar?.showHideView(false)
                    parseNetworkResponse(networkResponse)
                }
            }
            liveDataForCallLog.observe(this@CallDetailsActivity) {
                showLoader(false)
                when (it) {
                    SUCCESS -> {
                        Utility.showToastMessage("Data uploaded successfully")
                        if (itemPosition != null && itemPosition != -1) {
                            try {
                                arrayList[itemPosition!!].QStatus=1
                            }catch (e:Exception){

                            }
                            contactsListAdapter.notifyDataSetChanged()
                        }
                    }
                    FAILURE -> {
                        Utility.showToastMessage("Something went wrong")
                    }
                    else -> {
                        Utility.showToastMessage(it)
                    }
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
                    arrayList.addAll(contactsList?.qContactsList!!)
                    contactsListAdapter.submitData(contactsList = contactsList?.qContactsList!!)
                }
            }
        }
    }

    private var callback = fun(contactsMainModel: QContactsModel, position: Int) {
        if (contactsMainModel.MobileNo != "") {
//            val intent = Intent(Intent.ACTION_DIAL)
//            intent.data = Uri.parse("tel:${contactsMainModel.MobileNo}")
//            startActivity(intent)

            getCallLogForParticularNumber(contactsMainModel.MobileNo, contactsMainModel.HHId, position)
        }
    }

    private fun getLogsByNumber(strNumberWithCountryCode: String, originalMobNum: String, hhId: Int, callback: (ArrayList<UserCallLogsModel>) -> Unit) {
        try {
            val list = ArrayList<UserCallLogsModel>()
            val order = CallLog.Calls.DATE + " DESC"

            val cursor: Cursor? = contentResolver.query(CallLog.Calls.CONTENT_URI, null, CallLog.Calls.NUMBER + " = ? ", arrayOf(strNumberWithCountryCode), order)
            if (cursor?.moveToFirst() == true) {
                while (cursor.moveToNext()) {
                    if (cursor.columnCount > 0) {
                        val dateInLong = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE))
                        val date = getDate(dateInLong)
                        if (calendar.timeInMillis < dateInLong) {
                            println("date-->$date")
                            val duration = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION))
                            val location = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.LOCATION))
                            } else {
                                "not eligible"
                            }
                            val type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE))
                            val callType = getCallTypeAsString(type = type)
                            list.add(UserCallLogsModel(hhId, originalMobNum, date, duration, type.toString(), callType, location))
                        }
                    }
                }
                callback.invoke(list)
            }
            cursor?.close()
            callback.invoke(list)
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
        } catch (e: java.lang.Exception) {
            return milliSeconds.toString()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE_CALL_LOG) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Call log Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}

const val PERMISSION_CODE_CALL_LOG = 101
const val DATE_FORMAT = "dd/MM/yyyy hh:mm:ss.SSS"