package com.dongnh.mastereditvideo.utils.exts

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

/**
 * Project : MasterEditVideo
 * Created by DongNH on 05/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */


/**
 * Check permission storage
 */
fun Activity.checkPermissionStorage(): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        checkSafePermission(Manifest.permission.READ_MEDIA_IMAGES) &&
                checkSafePermission(Manifest.permission.READ_MEDIA_VIDEO)
    } else {
        checkSafePermission(Manifest.permission.READ_EXTERNAL_STORAGE) &&
                checkSafePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }
}

/**
 * Safe check permission
 *
 * @param targetPermission
 * @return
 */
fun Activity.checkSafePermission(targetPermission: String): Boolean {
    return (ActivityCompat.checkSelfPermission(
        this,
        targetPermission
    ) == PackageManager.PERMISSION_GRANTED)
}