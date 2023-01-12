package com.dongnh.mastereditvideo.model

import com.google.gson.annotations.SerializedName

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
data class MetaPageModel(
    @SerializedName("current_page")
    var currentPage: Int? = -1,

    @SerializedName("count")
    var count: Int? = -1,

    @SerializedName("per_page")
    var perPage: Int? = -1,

    @SerializedName("total")
    var total: Int? = -1,

    @SerializedName("total_pages")
    var totalPages: Int? = -1,

    @SerializedName("links")
    var links: LinkModel? = LinkModel()
)