package com.management.org.dcms.codes.models

data class SentReportPostModel(
    var HHId: Int = 0,
    var WANo: String,
    var TemplateId: Int,
    var DeviceId: String,
    var Lattitude: String,
    var Longitude: String,
    var Andversion: String,
    var IPAddress: String,
    var CampaignId:String
)

data class SentReportInGroupModel(
    var TemplateId: Int,
    var DeviceId: String,
    var Lattitude: String,
    var Longitude: String,
    var Andversion: String,
    var IPAddress: String,
    var CampaignId:String
)