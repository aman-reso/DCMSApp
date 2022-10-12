package com.management.org.dcms.codes.models


import com.google.gson.annotations.SerializedName

data class CallSentReportResponse(
    @SerializedName("RptCallDetailsModels")
    var callLogReportModels: ArrayList<CallLogReportModel>? = null,
    @SerializedName("Message")
    var message: String, // string
    var status: Int // 0
) {
    data class CallLogReportModel(
        @SerializedName("CallTime")
        var callTime: String, // string
        @SerializedName("MobileNo")
        var mobileNo: String,
        @SerializedName("CampaignName")
        var campaignName: String,
        @SerializedName("ThemeName")
        var themeName:String
    )
}
