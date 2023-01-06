package com.dongnh.masteredit.view

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.Surface
import com.dongnh.masteredit.const.TypePlayerScale
import com.dongnh.masteredit.gl.GLConfigChooser
import com.dongnh.masteredit.gl.GLContextFactory
import com.dongnh.masteredit.gl.GLFilterObject
import com.dongnh.masteredit.gl.GLLookUpTableFilterObject
import com.dongnh.masteredit.render.GLPlayerRenderer
import com.dongnh.masteredit.utils.exomanager.PlayerEventListener
import com.dongnh.masteredit.utils.interfaces.OnGLFilterActionListener
import com.google.android.exoplayer2.*
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
    var ratioScreen: String = ""
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

            override fun needRequestRender() {
                this@GLPlayerView.requestRender()
            }

            override fun needConfigInputSource(surface: Surface) {
                this@GLPlayerView.surface =  surface
                this@GLPlayerView.exoPlayer?.setVideoSurface(surface)
            }
        }
    }

    /**
     * Config exoplayer
     */
    fun setExoPlayer(player: ExoPlayer): GLPlayerView {
        if (this@GLPlayerView.exoPlayer != null) {
            this@GLPlayerView.exoPlayer!!.release()
            this@GLPlayerView.exoPlayer = null
        }
        this@GLPlayerView.exoPlayer = player
        this@GLPlayerView.surface?.let { player.setVideoSurface(it) }
        this@GLPlayerView.exoPlayer!!.addListener(PlayerEventListener(player))
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

        Timber.d("onMeasure viewWidth = $viewWidth viewHeight = $viewHeight")
        setMeasuredDimension(viewWidth, viewHeight)
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

    override fun onPause() {
        super.onPause()
        renderer.release()
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