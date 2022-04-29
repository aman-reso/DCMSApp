package com.management.org.dcms

import android.content.Context
import android.location.Location
import com.example.easywaylocation.EasyWayLocation
import com.example.easywaylocation.Listener
import com.google.android.gms.location.LocationRequest

open class LocationBuilder(var context: Context) : Listener {
    var easyWayLocation: EasyWayLocation? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    val request by lazy { LocationRequest() }

    private fun buildEasyWayLocation(): EasyWayLocation? {
        request.interval = 10000;
        request.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY;
        easyWayLocation = EasyWayLocation(context,   request,false, false, this)
        return easyWayLocation
    }

    fun startTracingLocation() {
        buildEasyWayLocation()?.startLocation();
    }

    fun endUpdatesForLocation() {
        buildEasyWayLocation()?.endUpdates();
    }

    override fun locationOn() {

    }


    override fun locationCancelled() {

    }

    override fun currentLocation(location: Location) {
        this.latitude = location.latitude
        this.longitude = location.longitude
    }

    fun getLocation(): Pair<Double, Double>? {
        if (latitude != null && longitude != null) {
            return Pair(latitude!!, longitude!!)
        }
        return null
    }
}