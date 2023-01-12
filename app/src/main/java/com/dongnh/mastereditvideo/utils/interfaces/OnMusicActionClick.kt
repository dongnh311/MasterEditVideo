package com.dongnh.mastereditvideo.utils.interfaces

import com.dongnh.masteredit.model.MusicModel

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface OnMusicActionClick {
    fun onMusicClick(musicModel: MusicModel, position: Int)
    fun onMusicPlay(musicModel: MusicModel, position: Int)
    fun onMusicDownload(musicModel: MusicModel, position: Int)
}