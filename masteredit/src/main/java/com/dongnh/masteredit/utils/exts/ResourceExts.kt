package com.dongnh.masteredit.utils.exts

import android.content.Context
import com.dongnh.masteredit.enums.NAME_FOLDER_IMAGE2VIDEO
import com.dongnh.masteredit.enums.NAME_SUB_FOLDER_IMAGE
import timber.log.Timber
import java.io.File

/**
 * Project : MasterEditVideo
 * Created by DongNH on 11/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */

/**
 * Create path private for save image video
 */
fun createMediaTransformPath(context: Context) : String {
    val file = File(pathOfMedia(context) + "/" + NAME_SUB_FOLDER_IMAGE)
    if (!file.exists()) {
        file.mkdir()
    }
    return file.path
}

/**
 * Find path folder media in app
 *
 * @param context
 * @return : path
 */
fun pathOfMedia(context: Context): String {
    val filePath = context.getDir(NAME_FOLDER_IMAGE2VIDEO, Context.MODE_PRIVATE)
    filePath.mkdir()
    val file =
        File(filePath.path)
    if (!file.exists()) file.mkdir()
    return file.path
}

/**
 * Check file is exist
 *
 * @param filePath : target file, this is string
 * @return : true if exist
 */
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