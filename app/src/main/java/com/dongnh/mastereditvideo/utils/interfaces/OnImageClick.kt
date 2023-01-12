package com.dongnh.mastereditvideo.utils.interfaces

import com.dongnh.masteredit.model.MediaModel

/**
 * Project : MasterEditVideo
 * Created by DongNH on 05/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface OnImageClick {
    fun imageClick(imageObject: MediaModel)
    fun imageClear(imageObject: MediaModel)
}