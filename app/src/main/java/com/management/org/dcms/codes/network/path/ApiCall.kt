package com.management.org.dcms.codes.network.path

import com.management.org.dcms.codes.network_res.GlobalNetResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


suspend fun <T> safeApiCall(apiCall: suspend () -> T): GlobalNetResponse<T> {
    return withContext(Dispatchers.IO) {
        try {
            GlobalNetResponse.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is HttpException -> {
                    val code = throwable.code()
//                    val errorResponse = convertErrorBody(throwable)
                    GlobalNetResponse.NetworkFailure("failure")
                }
                else -> {
                    GlobalNetResponse.NetworkFailure("")
                }
            }
        }
    }
}
//
//private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
//    return try {
//        throwable.response()?.errorBody()?.source()?.let {
//            val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
//            moshiAdapter.fromJson(it)
//        }
//    } catch (exception: Exception) {
//        null
//    }
//}
