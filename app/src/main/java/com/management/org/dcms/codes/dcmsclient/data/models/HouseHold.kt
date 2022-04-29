package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class HouseHold(
    @Json(name = "Id")
    val id :Int?=null,
    @Json(name = "VillageId")
    val villageId :Int,
    @Json(name = "VillageName")
    val villageName:String,
    @Json(name = "Name")
    val name:String,
    @Json(name = "MobileNo")
    val mobileNumber:String,
    @Json(name = "WhatsAppNo")
    val whatsappNumber:String,
    @Json(name = "EmailId")
    val emailId:String,
    @Json(name = "WardNumber")
    val wardNumber:String,
    @Json(name = "Landmark")
    val landmark:String,
    @Json(name = "FatherName")
    val fatherName:String,
    @Json(name = "MotherName")
    val motherName:String,
    @Json(name = "Address")
    val address:String,
    @Json(name = "IsDeleted")
    val isDeleted:Boolean?=null,
    @Json(name = "Lattitude")
    val latitude:String?,
    @Json(name = "Longitude")
    val longitude:String?,
    @Json(name = "ImageBase64")
    val imageBase64:String?,
    @Json(name = "extension")
    val extension:String?,
    @Json(name = "DocumentType")
    val documentType:String?,
    @Json(name = "DocumentNo")
    val documentNumber:String?,
    @Json(name = "MobileType")
    val mobileType:String?,


)
