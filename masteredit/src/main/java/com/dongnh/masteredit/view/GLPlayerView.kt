package com.dongnh.masteredit.view

import android.content.Context
import android.graphics.SurfaceTexture
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.Surface
import com.dongnh.masteredit.enums.TypePlayerScale
import com.dongnh.masteredit.const.VIEW_SIZE_16_9
import com.dongnh.masteredit.const.VIEW_SIZE_9_16
import com.dongnh.masteredit.gl.GLConfigChooser
import com.dongnh.masteredit.gl.GLContextFactory
import com.dongnh.masteredit.gl.GLFilterObject
import com.dongnh.masteredit.gl.GLLookUpTableFilterObject
import com.dongnh.masteredit.render.GLPlayerRenderer
import com.dongnh.masteredit.utils.interfaces.OnGLFilterActionListener
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.video.VideoSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class GLPlayerView(context: Context, attrs: AttributeSet?) : GLSurfaceView(context, attrs), Player.Listener {
    // Render
    private var renderer: GLPlayerRenderer = GLPlayerRenderer()

    var videoAspect = 1f
    var heightVideo = 0f
    var widthVideo = 0f
    var ratioScreen: String = VIEW_SIZE_16_9
    private var surface: Surface? = null
    private var typePlayerScale = TypePlayerScale.RESIZE_FIT_WIDTH

    // Using Exoplayer
    private var exoPlayer: ExoPlayer? = null

    init {
        setEGLContextFactory(GLContextFactory())
        setEGLConfigChooser(GLConfigChooser(false))
        setRenderer(renderer)

        renderer.onGLFilterActionListener = object : OnGLFilterActionListener {
            override fun onGLFilterAdded(filter: GLFilterObject?) {
                if (filter != null) {
                    filter.release()
                    if (filter is GLLookUpTableFilterObject) {
                        filter.releaseLutBitmap()
                    }
                }
            }

            override fun needRequestRender(surfaceTexture: SurfaceTexture?) {
                CoroutineScope(Dispatchers.Main).launch {
                    this@GLPlayerView.requestRender()
                }
            }

            override fun requestRender() {
                CoroutineScope(Dispatchers.Main).launch {
                    this@GLPlayerView.requestRender()
                }
            }

            override fun needConfigInputSource(surface: Surface) {
                this@GLPlayerView.surface = surface
                this@GLPlayerView.exoPlayer?.setVideoFrameMetadataListener { presentationTimeUs, releaseTimeNs, format, mediaFormat ->
                    Timber.e(
                        "Data : $presentationTimeUs, releaseTimeNs : $releaseTimeNs, format : $format, mediaFormat : $mediaFormat"
                    )
                }
            }
        }

        renderMode = RENDERMODE_WHEN_DIRTY
    }

    /**
     * Config exoplayer
     */
    fun setExoPlayer(player: ExoPlayer): GLPlayerView {
        if (this@GLPlayerView.exoPlayer != null) {
            this@GLPlayerView.exoPlayer = null
        }
        this@GLPlayerView.exoPlayer = player
        this@GLPlayerView.renderer.configPlayer(player)
        return this
    }

    /**
     * On create view
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val measuredWidth: Int = measuredWidth
        val measuredHeight: Int = measuredHeight
        var viewWidth = measuredWidth
        var viewHeight = measuredHeight
        when (typePlayerScale) {
            TypePlayerScale.RESIZE_FIT_WIDTH -> viewHeight = (measuredWidth / videoAspect).toInt()
            TypePlayerScale.RESIZE_FIT_HEIGHT -> viewWidth = (measuredHeight * videoAspect).toInt()
            else -> {
            }
        }

        Timber.e("onMeasure viewWidth = $viewWidth viewHeight = $viewHeight")
        setMeasuredDimension(viewWidth, viewHeight)
    }

    /**
     * Config video size to view
     */
    fun configSizeOfVideoToView(videoSize: VideoSize) {
        videoAspect = videoSize.width.toFloat() / videoSize.height * videoSize.pixelWidthHeightRatio
        if (ratioScreen == VIEW_SIZE_9_16) {
            if (videoSize.width > videoSize.height) {
                typePlayerScale = TypePlayerScale.RESIZE_FIT_WIDTH
            }
        } else if (ratioScreen == VIEW_SIZE_16_9) {
            if (videoSize.height > videoSize.width) {
                typePlayerScale = TypePlayerScale.RESIZE_FIT_HEIGHT
            }
        } else {
            typePlayerScale = if (videoSize.width > videoSize.height) {
                TypePlayerScale.RESIZE_FIT_WIDTH
            } else {
                TypePlayerScale.RESIZE_FIT_HEIGHT
            }
        }
        heightVideo = videoSize.height.toFloat()
        widthVideo = videoSize.width.toFloat()
        Timber.e("configSizeOfVideoToView viewWidth = $widthVideo viewHeight = $heightVideo")
        requestLayout()
    }

    /**
     * Add filter
     */
    fun addFilterRender(glFilter: GLFilterObject) {
        renderer.addGLFilter(glFilter)
    }

    /**
     * Config type for scale
     */
    fun setTypePlayerScale(TypePlayerScale: TypePlayerScale) {
        this@GLPlayerView.typePlayerScale = TypePlayerScale
        requestLayout()
    }

    /**
     * Make render continue draw
     */
    override fun onResume() {
        super.onResume()
        renderer.allowDraw()
    }

    /**
     * Make render stop draw
     */
    override fun onPause() {
        super.onPause()
        renderer.stopDraw()
    }

    /**
     * Set rotate for render
     */
    fun setRotate(
        rotate: Int,
        ratioScreen: String,
        isFlipVertical: Boolean,
        isFlipHorizontal: Boolean
    ) {
        this@GLPlayerView.ratioScreen = ratioScreen
        this@GLPlayerView.renderer.setRotation(
            rotate,
            ratioScreen,
            isFlipVertical,
            isFlipHorizontal
        )
    }

    /**
     * Release render
     */
    fun releaseAll() {
        renderer.release()
        exoPlayer?.release()
    }
}