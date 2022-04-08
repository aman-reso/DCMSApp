package com.management.org.dcms.codes.models

import com.squareup.moshi.Json

data class Ward(
    @Json(name = "Id")
    val Id:Int,
    @Json(name = "WardNo")
    val WardNo:String,
    @Json(name =  "VillageId")
    val VillageId:Int,
    @Json(name = "VillageName")
    val VillageName:String?,
    @Json(name  = "IsActive")
    val IsActive:Boolean?=null,
)
