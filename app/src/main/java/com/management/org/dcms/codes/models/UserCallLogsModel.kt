package com.management.org.dcms.codes.models

data class UserCallLogsModel(
    var HHId: Int,
    var MobileNo: String,
    var CallStartTime: String,
    var Duration: Int,
    var CallTypeCode: String,
    var CallType: String,
    var GeoCodeLocation: String
)
