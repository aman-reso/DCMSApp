package com.management.org.dcms.codes.network_res

sealed class GlobalNetResponse<T> {
    data class Success<T>(var value:T) : GlobalNetResponse<T>()
    data class NetworkFailure<T>(var error: String) : GlobalNetResponse<T>()
}