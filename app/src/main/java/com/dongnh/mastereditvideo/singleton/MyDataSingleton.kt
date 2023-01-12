package com.dongnh.mastereditvideo.singleton

import androidx.lifecycle.MutableLiveData
import com.dongnh.masteredit.model.MediaModel

/**
 * Project : MasterEditVideo
 * Created by DongNH on 05/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
object MyDataSingleton {

    // Save list to main
    val listMediaPick = mutableListOf<MediaModel>()

    // Make flag for reload
    val isAddNewMedia = MutableLiveData(false)

    fun resetValue() {
        listMediaPick.clear()
        isAddNewMedia.value = false
    }
}