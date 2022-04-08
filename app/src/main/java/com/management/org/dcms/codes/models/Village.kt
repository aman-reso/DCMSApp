package com.management.org.dcms.codes.models

import com.squareup.moshi.Json

data class Village(
    @Json(name = "Id")
    val Id: Int,
    @Json(name = "VillageName")
    val VillageName: String,
    @Json(name = "GramPanchayatId")
    val StateId: Int,
    @Json(name = "IsActive")
    val IsActive: Boolean? = null
)
