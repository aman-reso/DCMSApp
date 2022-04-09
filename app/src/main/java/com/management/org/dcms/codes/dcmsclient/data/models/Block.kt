package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class Block(
    @Json(name = "Id")
    val id:Int,
    @Json(name = "BlockName")
    val blockName:String,
    @Json(name =  "DistrictId")
    val districtId:Int,
    @Json(name  = "IsActive")
    val isActive:Boolean?=null,
)