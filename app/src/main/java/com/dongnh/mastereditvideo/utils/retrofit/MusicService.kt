package com.dongnh.mastereditvideo.utils.retrofit

import com.dongnh.mastereditvideo.model.MusicResult
import retrofit2.http.GET

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface MusicService {
    /**
     * Get list Music
     */
    @GET("main/music_list.json")
    suspend fun getListMusic(): MusicResult
}