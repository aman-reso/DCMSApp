package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json

data class Gp(
    @Json(name = "Id")
    val id: Int,
    @Json(name = "GramPanchaytName")
    val GpName: String,
    @Json(name = "BlockId")
    val stateId: Int,
    @Json(name = "IsActive")
    val isActive: Boolean? = null
)