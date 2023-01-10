package com.dongnh.masteredit.render

import android.graphics.SurfaceTexture
import android.opengl.GLES20
import android.opengl.Matrix
import android.view.Surface
import com.dongnh.masteredit.const.VIEW_SIZE_16_9
import com.dongnh.masteredit.const.VIEW_SIZE_1_1
import com.dongnh.masteredit.const.VIEW_SIZE_9_16
import com.dongnh.masteredit.gl.*
import com.dongnh.masteredit.utils.glutils.EglUtil
import com.dongnh.masteredit.utils.interfaces.OnGLFilterActionListener
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.video.VideoSize
import timber.log.Timber
import javax.microedition.khronos.egl.EGLConfig
import kotlin.math.abs

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GLPlayerRenderer : GLFrameBufferObjectRenderer(), SurfaceTexture.OnFrameAvailableListener {

    // GL view
    var previewTexture: GLSurfaceTexture? = null
    var surface: Surface? = null
    private var updateSurface = false
    private var textureId = -1

    // Exo player
    var exoPlayer: ExoPlayer? = null

    // Matrix
    private val mvpMatrix = FloatArray(16)
    private val projMatrix = FloatArray(16)
    private val mMatrix = FloatArray(16)
    private val vMatrix = FloatArray(16)
    private val stMatrix = FloatArray(16)
    var matrix: android.graphics.Matrix = android.graphics.Matrix()
    private var matrixPanZoom = floatArrayOf()

    // View
    private var rotation = 0
    private var flipVertical = false
    private var flipHorizontal = false
    private var ratioScreen = ""
    private var aspectRatio = 1f

    // Frame buffer draw
    private var filterFramebufferObject: GLFramebufferObject? = null
    private var previewFilter: GLPreviewFilterObject? = null
    private var glFilter: GLFilterObject? = null

    private var isNewFilter = false

    // View of preview
    private var width: Float = 0.0F
    private var height: Float = 0.0F

    // Callback
    var onGLFilterActionListener : OnGLFilterActionListener? = null

    // Add filter
    fun addGLFilter(glFilterObject: GLFilterObject?) {
        onGLFilterActionListener?.onGLFilterAdded(glFilterObject)

        glFilter = glFilterObject
        isNewFilter = true
        onGLFilterActionListener?.requestRender()
    }

    /**
     * Config view when surface is created
     */
    @Synchronized
    override fun onSurfaceCreated(config: EGLConfig?) {
        // Create texture to view
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f)
        val args = IntArray(1)
        GLES20.glGenTextures(args.size, args, 0)
        textureId = args[0]
        previewTexture = GLSurfaceTexture(textureId)
        previewTexture!!.setOnFrameAvailableListener(this)
        GLES20.glBindTexture(previewTexture!!.getTextureTarget(), textureId)
        // GL_TEXTURE_EXTERNAL_OES
        EglUtil.setupSampler(
            previewTexture!!.getTextureTarget(),
            GLES20.GL_LINEAR,
            GLES20.GL_NEAREST
        )
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        filterFramebufferObject = GLFramebufferObject()
        // GL_TEXTURE_EXTERNAL_OES

        // Create preview with texture
        previewFilter = GLPreviewFilterObject(previewTexture!!.getTextureTarget())
        previewFilter!!.setup()
        surface?.release()
        surface = Surface(previewTexture!!.getSurfaceTexture())

        // Init input source to preview
        onGLFilterActionListener?.needConfigInputSource(surface!!)
        exoPlayer?.setVideoSurface(surface)

        Matrix.setLookAtM(
            vMatrix, 0,
            0.0f, 0.0f, 5.0f,
            0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f
        )
        synchronized(this) { updateSurface = false }
        if (glFilter != null) {
            isNewFilter = true
        }
        GLES20.glGetIntegerv(GLES20.GL_MAX_TEXTURE_SIZE, args, 0)
    }

    /**
     * Config draw when surface is ready
     */
    override fun onSurfaceChanged(width: Int, height: Int) {
        Timber.e("onSurfaceChanged Width : $width, Height : $height")
        filterFramebufferObject!!.setup(width, height)
        previewFilter!!.setFrameSize(width, height)
        if (glFilter != null) {
            glFilter!!.setFrameSize(width, height)
        }
        aspectRatio = width.toFloat() / height
        this@GLPlayerRenderer.width = width.toFloat()
        this@GLPlayerRenderer.height = height.toFloat()
        Matrix.frustumM(projMatrix, 0, -aspectRatio, aspectRatio, -1f, 1f, 5f, 7f)
        Matrix.setIdentityM(mMatrix, 0)
        matrix = android.graphics.Matrix()
        matrix.postTranslate(this@GLPlayerRenderer.width / 2f, this@GLPlayerRenderer.height / 2f)
    }

    /**
     * Draw
     */
    override fun onDrawFrame(fbo: GLFramebufferObject?) {
        synchronized(this) {
            if (updateSurface) {
                previewTexture?.updateTexImage()
                previewTexture?.getTransformMatrix(stMatrix)
                updateSurface = false
            }
        }
        if (isNewFilter) {
            if (glFilter != null) {
                glFilter!!.setup()
                if (fbo != null) {
                    glFilter!!.setFrameSize(fbo.getWidth(), fbo.getHeight())
                }
            }
            isNewFilter = false
        }
        if (glFilter != null) {
            filterFramebufferObject?.enable()
            filterFramebufferObject?.let {
                filterFramebufferObject?.getWidth()?.let { it1 ->
                    GLES20.glViewport(
                        0,
                        0,
                        it1,
                        it.getHeight()
                    )
                }
            }
        }

        val xMatrixScale = (if (flipHorizontal) -1 else 1).toFloat()
        val yMatrixScale: Float = if (flipVertical) -1f else 1.toFloat()
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
        Matrix.multiplyMM(mvpMatrix, 0, vMatrix, 0, mMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projMatrix, 0, mvpMatrix, 0)

        Matrix.rotateM(mvpMatrix, 0, rotation.toFloat(), 0f, 0f, 1f)
        if (flipHorizontal || flipVertical) {
            Matrix.scaleM(mvpMatrix, 0, xMatrixScale, yMatrixScale, 1f)
        }
        if (rotation < 0 || rotation == 0 || rotation == 180 || rotation == 360 || rotation > 360) {
            Matrix.scaleM(mvpMatrix, 0, 1f, 1f, 1f)
        } else {
            when (ratioScreen) {
                VIEW_SIZE_16_9 -> if (aspectRatio < 1) {
                    Matrix.scaleM(mvpMatrix, 0, 1 / aspectRatio, 1f, 1f)
                } else {
                    Matrix.scaleM(mvpMatrix, 0, 1 / aspectRatio, 1 / aspectRatio, 1f)
                }
                VIEW_SIZE_9_16 -> if (aspectRatio > 1) {
                    Matrix.scaleM(mvpMatrix, 0, 1 / aspectRatio, aspectRatio, 1f)
                } else {
                    Matrix.scaleM(mvpMatrix, 0, aspectRatio, aspectRatio, 1f)
                }
                VIEW_SIZE_1_1 -> if (aspectRatio > 1) {
                    Matrix.scaleM(mvpMatrix, 0, 1 / aspectRatio, aspectRatio, 1f)
                } else {
                    Matrix.scaleM(mvpMatrix, 0, 1 / aspectRatio, 1f, 1f)
                }
            }
        }

        if (matrixPanZoom.size >= 9) {
            // get OpenGL matrix with the applied transformations, from finger gestures
            matrix.setValues(matrixPanZoom)

            val scaleX: Float = matrixPanZoom[android.graphics.Matrix.MSCALE_X]
            val scaleY: Float = matrixPanZoom[android.graphics.Matrix.MSCALE_Y]
            val tranX: Float = abs(matrixPanZoom[android.graphics.Matrix.MTRANS_X])
            val tranY: Float = abs(matrixPanZoom[android.graphics.Matrix.MTRANS_Y])

            val haftScreenWidth = this@GLPlayerRenderer.width * (scaleX * 0.8f) / 2
            val haftScreenHeight = this@GLPlayerRenderer.height * (scaleY * 0.8f) / 2

            var tranXCorrect = 0f
            var tranYCorrect = 0f

            // Calc new move
            if (scaleX != 1.0F) {
                tranXCorrect = if (tranX > haftScreenWidth) {
                    -((tranX - haftScreenWidth) / haftScreenWidth)
                } else {
                    ((haftScreenWidth - tranX) / haftScreenWidth)
                }

                tranYCorrect = if (tranY < haftScreenHeight) {
                    -((haftScreenHeight - tranY) / haftScreenHeight)
                } else {
                    ((tranY - haftScreenHeight) / haftScreenHeight)
                }

                // Keep move on X min and max
                if (tranXCorrect < -0.5f) {
                    tranXCorrect = -0.5f
                } else if (tranXCorrect > 0.5f) {
                    tranXCorrect = 0.5f
                }

                tranXCorrect *= aspectRatio

                // Keep move on Y min and max
                if (tranYCorrect < -1f) {
                    tranYCorrect = -1f
                } else if (tranYCorrect > 1f) {
                    tranYCorrect = 1f
                }

                tranYCorrect *= aspectRatio
            }

            Matrix.scaleM(mvpMatrix, 0, scaleX, scaleY, 1f)
            Matrix.translateM(
                mvpMatrix,
                0,
                tranXCorrect,
                tranYCorrect,
                0f
            )  // - of x is go to right max is 0.5, - of y is go top
        }

        previewFilter?.draw(textureId, mvpMatrix, stMatrix, aspectRatio)

        if (glFilter != null) {
            fbo?.enable()
            GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)
            filterFramebufferObject?.let { glFilter?.draw(it.getTexName(), fbo) }
        }
    }

    /**
     * Config exoplayer
     */
    fun configPlayer(exoPlayer: ExoPlayer) {
        this@GLPlayerRenderer.exoPlayer = exoPlayer
        surface?.let {
            this@GLPlayerRenderer.exoPlayer!!.setVideoSurface(it)
            onGLFilterActionListener?.requestRender()
        }
    }

    @Synchronized
    override fun onFrameAvailable(surfaceTexture: SurfaceTexture?) {
        updateSurface = true
        onGLFilterActionListener?.needRequestRender(surfaceTexture)
    }

    /**
     * Release object
     */
    fun release() {
        if (glFilter != null) {
            glFilter?.release()
        }
        if (previewTexture != null) {
            previewTexture?.release()
        }
    }

    /**
     * Set rotation if need
     */
    fun setRotation(
        rotation: Int,
        ratioScreen: String,
        isFlipVertical: Boolean,
        isFlipHorizontal: Boolean
    ) {
        this.rotation = rotation
        this.ratioScreen = ratioScreen
        flipVertical = isFlipVertical
        flipHorizontal = isFlipHorizontal
    }

    /**
     * Update matrix for view
     */
    fun setMatrixAdj(matrixValuesLocal: FloatArray) {
        this@GLPlayerRenderer.matrixPanZoom = matrixValuesLocal
    }

}