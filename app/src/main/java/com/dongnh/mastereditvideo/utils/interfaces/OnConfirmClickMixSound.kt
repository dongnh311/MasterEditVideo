package com.dongnh.mastereditvideo.utils.interfaces

import com.dongnh.mastereditvideo.model.MixSoundModel

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface OnConfirmClickMixSound {
    fun onConfirmClick(listMedia: MutableList<MixSoundModel>)
}