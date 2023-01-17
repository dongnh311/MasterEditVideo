package com.dongnh.masteredit.model

import com.dongnh.masteredit.const.SPECIAL_TYPE_NONE

/**
 * Project : MasterEditVideo
 * Created by DongNH on 16/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class SpecialModel {
    var id = -1L
    var name = ""
    var type = SPECIAL_TYPE_NONE
    var thumbnail: String = ""

    var beginAt = 0L
    var endAt = 0L

    // For filter lut
    var lut: String = ""
    var intensity: Float = 0.0F

    // Duration on view
    var duration: Long = -1

    var isChoose = false
}