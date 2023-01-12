package com.dongnh.mastereditvideo.base

import com.google.gson.annotations.SerializedName

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
abstract class BaseResultModel<T> {
    @SerializedName("data")
    var result: T? = null

    @SerializedName("status")
    var status: Boolean? = null

    @SerializedName("message")
    var message: String? = null
}