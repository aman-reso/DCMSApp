package com.management.org.dcms.codes.dcmsclient.data.models

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Image (
    @Json(name  = "FileName")
    val fileName : String = "",
    @Json(name  = "MediaType")
    val mediaType : String = "",
    @Json(name  = "Buffer")
    val buffer: String = ""
)