package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class District(
    @Json(name = "Id")
    val id: Int,
    @Json(name = "DistrictName")
    val districtName: String,
    @Json(name = "StateId")
    val stateId: Int,
    @Json(name = "IsActive")
    val isActive: Boolean?=null
)
