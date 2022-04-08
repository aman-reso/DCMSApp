package com.management.org.dcms.codes.di.factory

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.management.org.dcms.codes.network.path.DcmsApiInterface
import com.management.org.dcms.codes.utility.GeneralUtils
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.io.IOException

import okhttp3.Interceptor.Chain

import okhttp3.Interceptor
import okhttp3.Request


@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

    @Provides
    @Singleton
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder().setLenient()
        return gsonBuilder.create()
    }

    /*
     * The method returns the Cache object
     * */
    @Provides
    @Singleton
    fun provideCache(application: Application): Cache {
        val httpCacheDirectory = File(application.cacheDir, "responses")
        return Cache(httpCacheDirectory, cacheSize)
    }

    /*
     * The method returns the Okhttp object
     * */
    @Provides
    @Singleton
    fun provideOkhttpClient(cache: Cache): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.cache(cache)
        httpClient.connectTimeout(30, TimeUnit.SECONDS)
        httpClient.readTimeout(30, TimeUnit.SECONDS)
        return httpClient.build()
    }


    /*
     * The method returns the Retrofit object
     * */
    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit {
        Log.d("TAG", "provideRetrofit: $BASE_URL")
        return Retrofit.Builder()
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .baseUrl(BASE_URL).client(getUnsafeOkHttpClient())
            .build()
    }


    /**
     * gives stockMarket
     */
    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): DcmsApiInterface = retrofit.create(DcmsApiInterface::class.java)


    companion object {
        private var BASE_URL = GeneralUtils.httpsValue + GeneralUtils.baseUrl
        const val cacheSize = (5 * 1024 * 1024).toLong()//5MB
    }
}

private fun getUnsafeOkHttpClient(): OkHttpClient {
    return try {
        // Create a trust manager that does not validate certificate chains
        val trustAllCerts = arrayOf<TrustManager>(
            @SuppressLint("CustomX509TrustManager")
            object : X509TrustManager {
                @Throws(CertificateException::class)
                override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                @Throws(CertificateException::class)
                override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
                }

                override fun getAcceptedIssuers(): Array<X509Certificate> {
                    return arrayOf()
                }
            }
        )

        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, trustAllCerts, SecureRandom())
        val sslSocketFactory = sslContext.socketFactory
//        val httpClient=OkHttpClient()
//        httpClient.networkInterceptors().add(Interceptor { chain ->
//            val requestBuilder: Request.Builder = chain.request().newBuilder()
//            requestBuilder.header("Content-Type", "application/json")
//            chain.proceed(requestBuilder.build())
//        })
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
        builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
        builder.hostnameVerifier { _, _ -> true }
        builder.build()
    } catch (e: Exception) {
        throw RuntimeException(e)
    }

}
