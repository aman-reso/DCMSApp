package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class Village(
    @Json(name = "Id")
    val id: Int,
    @Json(name = "VillageName")
    val villageName: String,
    @Json(name = "GramPanchayatId")
    val stateId: Int,
    @Json(name = "IsActive")
    val isActive: Boolean? = null
)
