package com.management.org.dcms.codes.models


import com.google.gson.annotations.SerializedName

data class CallSentReportResponse(
    var callLogReportModels: ArrayList<CallLogReportModel>?=null,
    @SerializedName("Message")
    var message: String, // string
    var status: Int // 0
) {
    data class CallLogReportModel(
        @SerializedName("CallStartTime")
        var callStartTime: String, // 2022-05-30T04:11:47.888Z
        @SerializedName("CallType")
        var callType: String, // string
        @SerializedName("CallTypeCode")
        var callTypeCode: Int, // 0
        @SerializedName("Duration")
        var duration: Int, // 0
        @SerializedName("GeoCodeLocation")
        var geoCodeLocation: String, // string
        @SerializedName("HHId")
        var hHId: Int, // 0
        @SerializedName("MobileNo")
        var mobileNo: String // string
    )
}