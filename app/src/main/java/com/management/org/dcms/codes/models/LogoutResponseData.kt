package com.management.org.dcms.codes.models

data class LogoutResponseData(
    var Message: String,
    var Status: Int,
    var DisplayName: String,
    var AuthToken: String
)