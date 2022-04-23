package com.management.org.dcms.codes.utility

import com.management.org.dcms.R
import com.management.org.dcms.codes.DcmsApplication

object NetworkImpl {
    fun checkNetworkStatus(): Boolean {
        val isOnline = Utility.checkIsOnline()
        if (!isOnline) {//true
            DcmsApplication.getDcmsAppContext()?.getString(R.string.please_connect_with_internet)?.let { Utility.showToastMessage(it) }
        }
        return isOnline
    }
}