package com.management.org.dcms.codes.dcmsclient.di

import okhttp3.Interceptor
import okhttp3.Response

class SGInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()
        if (true) {
            requestBuilder.addHeader(
                "Authorization",
                "Bearer token"
            )
        }
        return chain.proceed(requestBuilder.build())
    }
}