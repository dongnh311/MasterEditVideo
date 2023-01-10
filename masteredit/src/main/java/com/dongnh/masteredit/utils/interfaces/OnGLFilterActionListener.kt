package com.dongnh.masteredit.utils.interfaces

import android.graphics.SurfaceTexture
import android.view.Surface
import com.dongnh.masteredit.gl.GLFilterObject

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
interface OnGLFilterActionListener {
    fun onGLFilterAdded(filter: GLFilterObject?)
    fun requestRender()
    fun needRequestRender(surfaceTexture: SurfaceTexture?)
    fun needConfigInputSource(surface: Surface)
}