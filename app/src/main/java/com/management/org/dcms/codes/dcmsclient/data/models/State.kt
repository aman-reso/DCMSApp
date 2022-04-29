package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json


data class State(
    @Json(name = "Id")
    val id: Int,
    @Json(name = "StateName")
    val stateName: String,
    @Json(name = "IsActive")
    val isActive: Boolean
)