package com.dongnh.masteredit.gl

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.GLES20
import com.dongnh.masteredit.utils.glutils.EglUtil

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GLLookUpTableFilterObject : GLFilterObject {

    private var hTex = 0

    private var lutTexture: Bitmap? = null

    private val FRAGMENT_SHADER =
        """precision mediump float;uniform mediump sampler2D lutTexture; 
            uniform lowp sampler2D inputImageTexture; 
            varying highp vec2 textureCoordinate; 
            vec4 sampleAs3DTexture(vec3 uv) {
                float width = 16.;
                float sliceSize = 1.0 / width;
                float slicePixelSize = sliceSize / width;
                float sliceInnerSize = slicePixelSize * (width - 1.0);
                float zSlice0 = min(floor(uv.z * width), width - 1.0);
                float zSlice1 = min(zSlice0 + 1.0, width - 1.0);
                float xOffset = slicePixelSize * 0.5 + uv.x * sliceInnerSize;
                float s0 = xOffset + (zSlice0 * sliceSize);
                float s1 = xOffset + (zSlice1 * sliceSize);
                vec4 slice0Color = texture2D(lutTexture, vec2(s0, uv.y));
                vec4 slice1Color = texture2D(lutTexture, vec2(s1, uv.y));
                float zOffset = mod(uv.z * width, 1.0);
                vec4 result = mix(slice0Color, slice1Color, zOffset);
                return result;
            }
            void main() {
               vec4 pixel = texture2D(inputImageTexture, textureCoordinate);
               vec4 gradedPixel = sampleAs3DTexture(pixel.rgb);
               gradedPixel.a = pixel.a;
               pixel = gradedPixel;
               gl_FragColor = pixel;
             }"""

    constructor(bitmap: Bitmap?) : super() {
        lutTexture = bitmap
        hTex = EglUtil.NO_TEXTURE
    }


    constructor(resources: Resources?, fxID: Int) : super() {
        lutTexture = BitmapFactory.decodeResource(resources, fxID)
        hTex = EglUtil.NO_TEXTURE
    }

    override fun onDraw() {
        val offsetDepthMapTextureUniform: Int = getHandle("lutTexture")
        GLES20.glActiveTexture(GLES20.GL_TEXTURE3)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, hTex)
        GLES20.glUniform1i(offsetDepthMapTextureUniform, 3)
    }

    override fun setup() {
        super.setup()
        loadTexture()
    }

    private fun loadTexture() {
        if (hTex == EglUtil.NO_TEXTURE) {
            hTex =
                lutTexture?.let { EglUtil.loadTexture(it, EglUtil.NO_TEXTURE, false) }!!
        }
    }

    fun releaseLutBitmap() {
        if (lutTexture != null && !lutTexture!!.isRecycled) {
            lutTexture!!.recycle()
            lutTexture = null
        }
    }

    fun reset() {
        hTex = EglUtil.NO_TEXTURE
        hTex = lutTexture?.let { EglUtil.loadTexture(it, EglUtil.NO_TEXTURE, false) }!!
    }
}