package com.dongnh.mastereditvideo.utils.interfaces

/**
 * Project : MasterEditVideo
 * Created by DongNH on 11/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface OnItemMediaChoose {
    fun onItemMediaChoose(index: Int)
    fun onItemMediaCancel(index: Int)
    fun onLayerChoose(index: Int)
    fun onItemSpecialChoose(index: Int)
    fun onMusicChoose(index: Int)
}