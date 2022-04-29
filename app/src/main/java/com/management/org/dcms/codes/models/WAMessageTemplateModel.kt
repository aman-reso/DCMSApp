package com.management.org.dcms.codes.models

data class WAMessageTemplateModel(var Message: String, var Status: Int, var WAMessage: WAMessage)
data class WAMessage(
    var Id: Int, var Template: String?="", var ThemeId: Int, var CampaignId: Int, var CampName: String, var ThemeName: String, var Mode: String,
    var MediaURL: String?=null
)

data class TextMessageTemplateModel(var Message: String, var Status: Int, var TextMessage: TextMessage? = null)
data class TextMessage(var Id: Int, var Template: String, var ThemeId: Int, var CampaignId: Int, var CampName: String, var ThemeName: String)



