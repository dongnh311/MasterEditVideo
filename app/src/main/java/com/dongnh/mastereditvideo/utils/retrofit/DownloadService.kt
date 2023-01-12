package com.dongnh.mastereditvideo.utils.retrofit

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Streaming
import retrofit2.http.Url

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface DownloadService {
    @Streaming
    @GET
    fun downloadFileWithDynamicUrlSync(@Url fileUrl: String): Response<ResponseBody>
}