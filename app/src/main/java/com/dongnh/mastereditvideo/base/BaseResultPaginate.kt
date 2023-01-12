package com.dongnh.mastereditvideo.base

import com.dongnh.mastereditvideo.model.MetaPageModel

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
abstract class BaseResultPaginate<T> {
    var data: ArrayList<T> = arrayListOf()
    val meta: MetaPageModel = MetaPageModel()
}