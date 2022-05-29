package com.management.org.dcms.codes.activity

import android.Manifest
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.EasyWayLocation.LOCATION_SETTING_REQUEST_CODE
import com.example.easywaylocation.Listener
import com.google.android.gms.location.LocationRequest
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.management.org.dcms.R
import com.management.org.dcms.codes.activity.LocationValue.latitude
import com.management.org.dcms.codes.activity.LocationValue.longitude
import com.management.org.dcms.codes.utility.LanguageManager
import com.management.org.dcms.codes.utility.Utility


open class BaseActivity : AppCompatActivity(), Listener {
    var easyWayLocation: EasyWayLocation? = null
    val request = LocationRequest()
    var count=0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpPermissions()
        request.interval = 10000
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    }

    override fun locationOn() {

    }

    override fun currentLocation(location: Location?) {
       if (location!=null){
           latitude=location.latitude.toString()
           longitude=location.longitude.toString()
           System.out.println("lat-->$latitude")
           System.out.println("long-->$longitude")
       }

    }

    override fun locationCancelled() {
        setUpPermissions()
    }
    private fun makeLocationRequest(){
        if (easyWayLocation==null) {
            easyWayLocation = EasyWayLocation(this, request, false, false, this)
            easyWayLocation?.startLocation()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            LOCATION_SETTING_REQUEST_CODE -> easyWayLocation?.onActivityResult(resultCode)
        }
    }

    private fun setUpPermissions() {
        Dexter.withContext(this)
            .withPermissions(
                Manifest.permission.SEND_SMS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_CALL_LOG
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {
                        if (it.areAllPermissionsGranted()) {
                            makeLocationRequest()
                        } else  if (it.isAnyPermissionPermanentlyDenied){
                            Utility.showToastMessage(LanguageManager.getStringInfo(R.string.please_allow_all_above_permission))
                            setUpPermissions()
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(permissions: List<PermissionRequest?>?, token: PermissionToken?) { /* ... */
                    token?.continuePermissionRequest()
                }
            }).withErrorListener{
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            }
            .check()
    }
}

interface GetLocationListener {
    fun didGetLocation(latitude: String?, longitude: String?)
    fun failToGetLocation()
}

object LocationValue {
    var latitude = "23.5"
    var longitude = "25.6"
}