package com.management.org.dcms.codes.models

import com.google.gson.annotations.SerializedName

data class QReportDetailModel(
    var Message: String, var status: Int,
    @SerializedName("RptQViewModels")
    var taskListItem: ArrayList<TaskItem>? = null
)

data class TaskItem(
    var CampaignName: String?=null,
    var WANo: String?=null,
    var AttemptTime: String?=null,
    var FinishTime: String?=null,
    var ThemeName: String?=null,
    var SentTime:String?=null
)

data class TextReportDetailModel(
    var Message: String, var status: Int,
    @SerializedName("RptTextViewModels")
    var taskListItem: ArrayList<TaskItem>? = null
)

data class WAReportDetailModel(
    var Message: String, var status: Int,
    @SerializedName("RptWAViewModels")
    var taskListItem: ArrayList<TaskItem>? = null
)
