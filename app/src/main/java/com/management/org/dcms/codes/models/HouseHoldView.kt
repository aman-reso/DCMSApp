package com.management.org.dcms.codes.models

import com.squareup.moshi.Json
import org.json.JSONObject

data class houseHoldView(
    @Json(name = "Id")
    val Id :Int?=0,
    @Json(name = "VillageId")
    val VillageId :Int,
    @Json(name = "VillageName")
    val VillageName:String,
    @Json(name = "Name")
    val Name:String,
    @Json(name = "MobileNo")
    val MobileNumber:String,
    @Json(name = "WhatsAppNo")
    val WhatsappNumber:String,
    @Json(name = "EmailId")
    val EmailId:String,
    @Json(name = "WardNumber")
    val WardNumber:String,
    @Json(name = "Landmark")
    val Landmark:String,
    @Json(name = "FatherName")
    val FatherName:String,
    @Json(name = "MotherName")
    val MotherName:String,
    @Json(name = "Address")
    val Address:String,
    @Json(name = "IsDeleted")
    val IsDeleted:Boolean?=true,
    @Json(name = "Lattitude")
    val Latitude:String?,
    @Json(name = "Longitude")
    val Longitude:String?,
    @Json(name = "ImageBase64")
    val ImageBase64:String?,
    @Json(name = "extension")
    val extension:String?,
    @Json(name = "DocumentType")
    val DocumentType:String?,
    @Json(name = "DocumentNo")
    val DocumentNumber:String?,
    @Json(name = "MobileType")
    val MobileType:String?,


)
