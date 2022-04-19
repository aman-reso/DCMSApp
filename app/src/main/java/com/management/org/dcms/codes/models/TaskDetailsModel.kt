package com.management.org.dcms.codes.models

import com.google.gson.annotations.SerializedName

data class TaskDetailsModel(
    @SerializedName("Message") var Message: String? = null,
    @SerializedName("status") var status: Int? = null,
    @SerializedName("Task") var Task: Task?
)

data class Task(
    var Id: Int, var CampaignId: Int, var ThemeId: Int, var WAMessages: Boolean, var Questionaires: Boolean, var Instructions: String, var CampName: String, var ThemeName: String,
    var DataCollection:Boolean,var TextMessage:Boolean
)
