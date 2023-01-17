package com.dongnh.masteredit.model

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class MusicModel {
    var id: Int = -1
    var nameMusic: String = ""
    var urlFile: String = ""
    var urlThumb: String = ""

    var subCategoryId: Long? = -1
    var categoryId: Long? = -1

    var pathInLocal: String? = null
    var duration: Long = 0
    var beginAt = 0L
    var endAt = 0L
    var nameSinger: String = ""
    var singerId: Long? = -1

    var volume: Float = 50f

    var progress: Int = 0
    var isChoose: Boolean = false
    var isPlay = false
    var isDownloaded = false
}