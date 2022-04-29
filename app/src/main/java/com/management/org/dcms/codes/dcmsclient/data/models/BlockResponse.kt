package com.management.org.dcms.codes.dcmsclient.data.models

import com.management.org.dcms.codes.dcmsclient.data.models.Block
import com.squareup.moshi.Json

data class BlockResponse(
    @Json(name = "Message")
    val message:String,
    @Json(name = "status")
    val status:Int,
    @Json(name = "Blocks")
    val blocks:List<Block>
)