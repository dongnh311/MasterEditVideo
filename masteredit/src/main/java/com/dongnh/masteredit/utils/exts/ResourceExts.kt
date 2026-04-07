package com.dongnh.masteredit.utils.exts

import android.content.Context
import com.dongnh.masteredit.enums.NAME_FOLDER_IMAGE2VIDEO
import com.dongnh.masteredit.enums.NAME_SUB_FOLDER_EXPORT
import com.dongnh.masteredit.enums.NAME_SUB_FOLDER_IMAGE
import com.dongnh.masteredit.enums.NAME_SUB_FOLDER_MUSIC
import timber.log.Timber
import java.io.File

/**
 * Project : MasterEditVideo
 * Created by DongNH on 11/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */

fun pathOfMedia(context: Context): String {
    val filePath = context.getDir(NAME_FOLDER_IMAGE2VIDEO, Context.MODE_PRIVATE)
    filePath.mkdir()
    val file = File(filePath.path)
    if (!file.exists()) file.mkdir()
    return file.path
}

private fun createSubPath(context: Context, subfolder: String): String {
    val file = File(pathOfMedia(context), subfolder)
    if (!file.exists()) file.mkdir()
    return file.path
}

fun createMediaTransformPath(context: Context): String = createSubPath(context, NAME_SUB_FOLDER_IMAGE)

fun createMusicPath(context: Context): String = createSubPath(context, NAME_SUB_FOLDER_MUSIC)

fun createExportOutputPath(context: Context): String = createSubPath(context, NAME_SUB_FOLDER_EXPORT)

fun deleteFolderIfExit(filePath: String): Boolean {
    return try {
        val file = File(filePath)
        if (file.exists()) {
            file.deleteRecursively()
            true
        } else {
            false
        }
    } catch (e: Exception) {
        Timber.e(e)
        false
    }
}
