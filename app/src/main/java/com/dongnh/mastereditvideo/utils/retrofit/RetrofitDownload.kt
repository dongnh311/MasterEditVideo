package com.dongnh.mastereditvideo.utils.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */

private val retrofit: Retrofit = Retrofit.Builder()
    .baseUrl("https:google.com/")
    .addConverterFactory(GsonConverterFactory.create())
    .build()

fun loadDownloadService() = retrofit.create<DownloadService>()