package com.management.org.dcms.codes.models

data class WAMessageTemplateModel(var Message: String, var Status: Int, var WAMessage: WAMessage)
data class WAMessage(var Id: Int, var Template: String, var ThemeId: Int, var CampaignId: Int, var CampName: String, var ThemeName: String)



