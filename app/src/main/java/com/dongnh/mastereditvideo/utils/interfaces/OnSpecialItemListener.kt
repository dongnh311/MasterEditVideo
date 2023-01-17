package com.dongnh.mastereditvideo.utils.interfaces

import com.dongnh.masteredit.model.SpecialModel

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface OnSpecialItemListener {
    fun onItemSpecialTouchDown(itemSpecial: SpecialModel, position: Int)
    fun onItemSpecialTouchUp(itemSpecial: SpecialModel, position: Int)
    fun onItemSpecialClick(itemSpecial: SpecialModel, position: Int)
}