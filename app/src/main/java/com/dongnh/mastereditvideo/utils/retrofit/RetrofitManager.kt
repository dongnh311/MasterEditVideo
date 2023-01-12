package com.dongnh.mastereditvideo.utils.retrofit

import com.dongnh.mastereditvideo.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */

/**
 * Make retrofit default
 */
val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https:google.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

/**
 * Load retrofit download
 */
fun loadDownloadService() = retrofit.create<DownloadService>()

/**
 * Load retrofit download
 */
inline fun <reified T> loadRetrofitService() = retrofit.create<T>()

/**
 * Create retrofit by class service
 */
inline fun <reified T> createRetrofitService(url: String = "https:google.com/") =
    retrofitApi(url).create<T>()

/**
 * Make retrofit call api
 */
fun retrofitApi(url: String): Retrofit = Retrofit.Builder()
    .baseUrl(url)
    .addConverterFactory(GsonConverterFactory.create())
    .client(createOkHttpClient(createInterceptor()))
    .build()

private fun createOkHttpClient(requestInterceptor: Interceptor): OkHttpClient {
    val builder = OkHttpClient.Builder()
        .addInterceptor(requestInterceptor)
        .addInterceptor(log())
    return builder.build()
}

private fun createInterceptor() = Interceptor { chain ->
    val original = chain.request()
    val builder = original.newBuilder()
        .method(original.method, original.body)

    val newRequest = builder.build()
    return@Interceptor chain.proceed(newRequest)
}

private fun log(): Interceptor {
    val logging = HttpLoggingInterceptor()
    logging.level =
        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
    return logging
}