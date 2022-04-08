package com.management.org.dcms.codes.network.path

import com.management.org.dcms.codes.network_res.GlobalNetResponse
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException


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

suspend fun <T> safeApiCallNew(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.Success(apiCall.invoke())
        } catch (throwable: Throwable) {
            Timber.e(throwable)
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    val errorResponse = convertErrorBody(throwable)
                    ResultWrapper.GenericError(code, errorResponse)
                }
                else -> {
                    ResultWrapper.GenericError(null, null)
                }
            }
        }
    }
}

private fun convertErrorBody(throwable: HttpException): ErrorResponse? {
    return try {
        throwable.response()?.errorBody()?.source()?.let {
            Timber.e(it.toString())
            val moshiAdapter = Moshi.Builder().build().adapter(ErrorResponse::class.java)
            moshiAdapter.fromJson(it)
        }
    } catch (exception: Exception) {
        null
    }
}

sealed class UiState {
    data class Success<T>(val data: T? = null) : UiState()
    data class Failed(val message: String?) : UiState()
    object Loading : UiState()
    object Empty : UiState()
}

@JsonClass(generateAdapter = true)
data class ErrorResponse(
    @Json(name = "message")
    val error: String // this is the translated error shown to the user directly from the API
)

sealed class ResultWrapper<out T> {
    data class Success<out T>(val value: T) : ResultWrapper<T>()
    data class GenericError(val code: Int? = null, val error: ErrorResponse? = null) :
        ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()
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
