package com.dongnh.masteredit.gl

import android.graphics.SurfaceTexture
import android.opengl.GLES11Ext.GL_TEXTURE_EXTERNAL_OES

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GLSurfaceTexture(textureId: Int) : SurfaceTexture.OnFrameAvailableListener {
    private val surfaceTexture: SurfaceTexture = SurfaceTexture(textureId)
    private var onFrameAvailableListener: SurfaceTexture.OnFrameAvailableListener? = null

    fun setOnFrameAvailableListener(listener: SurfaceTexture.OnFrameAvailableListener?) {
        onFrameAvailableListener = listener
    }

    fun getTextureTarget(): Int {
        return GL_TEXTURE_EXTERNAL_OES
    }

    fun updateTexImage() {
        surfaceTexture.updateTexImage()
    }

    fun getTransformMatrix(mtx: FloatArray?) {
        surfaceTexture.getTransformMatrix(mtx)
    }

    fun getSurfaceTexture(): SurfaceTexture {
        return surfaceTexture
    }

    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        if (onFrameAvailableListener != null) {
            onFrameAvailableListener!!.onFrameAvailable(this.surfaceTexture)
        }
    }

    fun release() {
        surfaceTexture.release()
    }

    init {
        surfaceTexture.setOnFrameAvailableListener(this)
    }
}