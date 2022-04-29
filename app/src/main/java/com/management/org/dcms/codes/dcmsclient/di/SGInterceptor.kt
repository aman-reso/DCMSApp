package com.management.org.dcms.codes.dcmsclient.di

import okhttp3.Interceptor
import okhttp3.Response

class SGInterceptor: Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        System.out.println(chain.request().url)
        val requestBuilder = chain.request().newBuilder()
        requestBuilder.addHeader(
            "Authorization",
            "Bearer token"
        )
        return chain.proceed(requestBuilder.build())
    }
}