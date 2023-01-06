package com.dongnh.masteredit.gl

import android.opengl.GLES20
import android.util.Pair
import java.util.ArrayList

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GlFilterGroupObject(private val filters: Collection<GLFilterObject?>) : GLFilterObject() {

    val list: ArrayList<Pair<GLFilterObject?, GLFramebufferObject?>> =
        ArrayList<Pair<GLFilterObject?, GLFramebufferObject?>>()


    override fun setup() {
        super.setup()
        val max = filters.size
        var count = 0
        for (shader in filters) {
            if (null != shader) {
                shader.setup()
                val fbo: GLFramebufferObject? = if (count + 1 < max) {
                    GLFramebufferObject()
                } else {
                    null
                }
                list.add(Pair.create<GLFilterObject?, GLFramebufferObject?>(shader, fbo))
                count++
            }
        }
    }

    override fun release() {
        for (pair in list) {
            if (pair.first != null) {
                pair.first!!.release()
            }
            if (pair.second != null) {
                pair.second!!.release()
            }
        }
        list.clear()
        super.release()
    }

    override fun setFrameSize(width: Int, height: Int) {
        super.setFrameSize(width, height)
        for (pair in list) {
            if (pair.first != null) {
                pair.first!!.setFrameSize(width, height)
            }
            if (pair.second != null) {
                pair.second!!.setup(width, height)
            }
        }
    }

    var prevTexName = 0

    override fun draw(texName: Int, fbo: GLFramebufferObject?) {
        prevTexName = texName
        for (pair in list) {
            if (pair.second != null) {
                if (pair.first != null) {
                    pair.second!!.enable()
                    GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
                    pair.first!!.draw(prevTexName, pair.second)
                }
                prevTexName = pair.second!!.getTexName()
            } else {
                if (fbo != null) {
                    fbo.enable()
                } else {
                    GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
                }
                if (pair.first != null) {
                    pair.first!!.draw(prevTexName, fbo)
                }
            }
        }
    }
}
