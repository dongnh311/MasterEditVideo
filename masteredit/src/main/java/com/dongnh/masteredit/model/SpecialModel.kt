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

    // For transition
    var indexBefore = -1
    var indexNext = -1
    var itemIdBefore = -1
    var itemIdNext = -1

    var isChoose = false

    var isAdded = false

    fun cloneItem(inputItem: SpecialModel): SpecialModel {
        val itemClone = SpecialModel()
        itemClone.id = inputItem.id
        itemClone.name = inputItem.name
        itemClone.type = inputItem.type
        itemClone.thumbnail = inputItem.thumbnail
        itemClone.beginAt = inputItem.beginAt
        itemClone.endAt = inputItem.endAt
        itemClone.lut = inputItem.lut
        itemClone.intensity = inputItem.intensity
        itemClone.duration = inputItem.duration
        itemClone.isChoose = inputItem.isChoose
        itemClone.indexBefore = inputItem.indexBefore
        itemClone.indexNext = inputItem.indexNext
        itemClone.itemIdBefore = inputItem.itemIdBefore
        itemClone.itemIdNext = inputItem.itemIdNext
        return itemClone
    }
}