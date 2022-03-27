package com.management.org.dcms.codes.models

import com.google.gson.annotations.SerializedName

data class LoginResponseData(
    @SerializedName("Message")
    var message: String? = null,

    @SerializedName("Status")
    var status: Int? = null,

    @SerializedName("DisplayName")
    var displayName: String? = null,

    @SerializedName("AuthToken")
    var authToken: String? = null
)