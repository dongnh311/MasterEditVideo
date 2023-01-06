package com.dongnh.masteredit.model

/**
 * Project : MasterEditVideo
 * Created by DongNH on 05/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class MediaObject(
    var mediaName: String = "",
    var mediaPath: String = "",
    var mediaType: String = "",

    // For check transition id for object
    var objectTransitionId: Int = -1,

    var mediaDuration: Long = 0L,
    var isChoose: Boolean = false,
    var isSpecial: Boolean = false,

    var startTime: Long = 0L,
    var endTime: Long = 0L,

    var volume: Float = 1f,

    // For layer start time in timeline
    var currentDuration: Long = 0L,

    // Id of item, for check in all adapter select or view
    var mediaId: Int = -1,

    // Position item in list media
    var position: Int = -1

) : Cloneable {

    var rotate: Int = 0

    var filters: ArrayList<Int> = arrayListOf()

    var speed: Double = 1.0

    var brightness: Float = 0.5f

    var contrast: Float = 0.5f

    var gammar: Float = 0.5f

    var rotateCalculation: Int = 0

    var flipVertical: Boolean = false

    var flipHorizontal: Boolean = false

    var matrixValuesLocal: FloatArray = floatArrayOf()

    var beginAt: Long = 0L
    var endAt: Long = 0L

    public override fun clone(): Any {
        return super.clone()
    }
}