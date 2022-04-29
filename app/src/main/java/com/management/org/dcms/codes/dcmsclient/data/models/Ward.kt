package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class Ward(
    @Json(name = "Id")
    val id:Int,
    @Json(name = "WardNo")
    val wardNo:String,
    @Json(name =  "VillageId")
    val villageId:Int,
    @Json(name = "VillageName")
    val villageName:String?,
    @Json(name  = "IsActive")
    val isActive:Boolean?=null,
)
