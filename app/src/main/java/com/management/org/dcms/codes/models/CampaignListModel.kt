package com.management.org.dcms.codes.models

import com.google.gson.annotations.SerializedName

data class CampaignListModel(
    @SerializedName("Message")
    var message: String? = null,
    @SerializedName("sGCampaigns")
    var campItemList:ArrayList<CampaignModel>?=null
)

data class CampaignModel(
    @SerializedName("CampaignId")
    var campId: String? = null,
    @SerializedName("CampName")
    var campName: String? = null
)
//CampaignId
//CampName