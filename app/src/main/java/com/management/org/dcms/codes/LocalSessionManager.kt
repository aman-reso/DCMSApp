package com.management.org.dcms.codes

import java.net.IDN
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class LocalSessionManager @Inject constructor() {
    var selectedCampId:String?="-1"
    internal fun setSelectedCamp(id: String){
        this.selectedCampId=id
    }
    open fun getSelectedCampaignId():String{
        return if (selectedCampId.equals("-1")){
            "1"
        } else{
            selectedCampId!!;
        }
    }
}