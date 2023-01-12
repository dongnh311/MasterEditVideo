package com.dongnh.mastereditvideo.utils.error

import kotlinx.coroutines.CoroutineExceptionHandler
import timber.log.Timber

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */

/**
 * Make handle on top applocation
 */
val coroutineError = CoroutineExceptionHandler { _, throwable ->
    Timber.e(throwable)
}