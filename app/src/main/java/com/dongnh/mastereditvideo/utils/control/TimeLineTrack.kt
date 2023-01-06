package com.dongnh.mastereditvideo.utils.control

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.get
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.MEDIA_TYPE_IMAGE
import com.dongnh.mastereditvideo.const.MEDIA_TYPE_VIDEO
import com.dongnh.mastereditvideo.databinding.ItemTrackViewBinding
import com.dongnh.mastereditvideo.databinding.LayoutTimeLineBinding
import com.dongnh.mastereditvideo.utils.interfaces.OnDurationTrackScrollListener
import com.dongnh.mastereditvideo.utils.view.ShapeableImageViewHeight
import com.google.android.flexbox.FlexboxLayout
import com.google.android.material.shape.CornerFamily
import timber.log.Timber

/**
 * Project : MasterEditVideo
 * Created by DongNH on 05/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class TimeLineTrack: FrameLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    // Binding
    private val itemTimeLineBinding: LayoutTimeLineBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context),
        R.layout.layout_time_line,
        null,
        false
    )

    // Old point click
    private var tempScroll = 0f

    private var totalThumbnailCreated = 0
    private var totalDuration = 0L
    private var maxWidthOfDuration = 0

    // Thumbnail width
    private val thumbnailSize =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45f, resources.displayMetrics)
            .toInt()

    // Margin
    private val marginItem =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)
            .toInt()

    // Margin left to draw center
    private val marginToCenter = context.resources.displayMetrics.widthPixels / 2

    // Callback to view
    var onDurationTrackScrollListener: OnDurationTrackScrollListener? = null

    // Check is start or not
    var isScrollToStart = false

    // This duration for seek
    var currentDurationInVideo: Long = 0
    var currentDurationInView: Double = 0.0

    // Conner of image view
    private val connerImageView =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8f, resources.displayMetrics)

    // List media
    private var listMedia: MutableList<MediaObject> = mutableListOf()

    // Create view
    init {
        addView(itemTimeLineBinding.root)

        // TODO : For test
        totalThumbnailCreated = 20
        initializeViewForDuration()
        initTimeLineForType()

        initializeListener()
        handelUpdateProgress()
    }

    /**
     * Init duration view
     */
    private fun initializeViewForDuration() {
        this@TimeLineTrack.itemTimeLineBinding.layoutDuration.removeAllViews()
        val sizeToCreate =
            totalThumbnailCreated * 2 + 1
        for (i in 0 until sizeToCreate) {
            val textView = TextView(context)
            if (i % 2 == 0) {
                textView.text = (i / 2).toString()
                textView.textSize = 6f
                textView.setTextColor(context.getColor(R.color.colorWhite))
            } else {
                textView.text = "■"
                textView.textSize = 4f
                textView.setTextColor(context.getColor(R.color.colorTextSelected))
            }

            textView.gravity = Gravity.START
            val layoutParam = FlexboxLayout.LayoutParams(
                (thumbnailSize + (marginItem / 4)) / 2,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Set margin to center item
            if (i == 0) {
                layoutParam.setMargins(marginToCenter - thumbnailSize, 0, 0, 0)
            } else if ((i + 1) == sizeToCreate) {
                layoutParam.setMargins(0, 0, marginToCenter, 0)
            }

            this@TimeLineTrack.itemTimeLineBinding.layoutDuration.addView(
                textView, layoutParam
            )
        }

        maxWidthOfDuration = (totalThumbnailCreated * thumbnailSize) + (marginToCenter * 2) - thumbnailSize + (marginItem / 2)
        val layoutOfDuration = ConstraintLayout.LayoutParams(maxWidthOfDuration, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        layoutOfDuration.setMargins(marginToCenter, 0, marginToCenter, 0)

        this@TimeLineTrack.itemTimeLineBinding.layoutDuration.layoutParams = layoutOfDuration
        this@TimeLineTrack.itemTimeLineBinding.layoutDuration.invalidate()
        this@TimeLineTrack.itemTimeLineBinding.layoutDuration.requestLayout()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initializeListener() {
        this@TimeLineTrack.itemTimeLineBinding.horizontalScroll.setOnTouchListener { _, event ->
            when (event?.action) {
                MotionEvent.ACTION_MOVE -> {
                    if (tempScroll != event.x) {
                        Timber.e("Scroll to ${event.x}")
                    }
                    tempScroll = event.x
                }
            }
            false
        }
    }

    /**
     * Send progress to view
     */
    private fun handelUpdateProgress() {
        this@TimeLineTrack.itemTimeLineBinding.horizontalScroll.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            val durationCalc = (scrollX / thumbnailSize.toDouble()) * 1000L
            val durationSeek = durationCalc.toLong()
            if (currentDurationInVideo != durationSeek && !isScrollToStart) {
                currentDurationInVideo = durationSeek
                onDurationTrackScrollListener?.onDurationSeekChange(currentDurationInVideo)
            }

            onDurationTrackScrollListener?.onScrollXOfView(scrollX.toLong())
        }
    }

    /**
     * Init line track for view
     */
    private fun initTimeLineForType() {
        // Add item to view
        for (i in 0 until 5) {
            val layoutMedia: ItemTrackViewBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.item_track_view, null, false)

            // Set ID
            when (i) {
                0 -> {
                    layoutMedia.root.id = R.id.track_media
                }
                1 -> {
                    layoutMedia.root.id = R.id.track_effect
                }
                2 -> {
                    layoutMedia.root.id = R.id.track_graphy
                }
                3 -> {
                    layoutMedia.root.id = R.id.track_filter
                }
                4 -> {
                    layoutMedia.root.id = R.id.track_music
                }
            }

            val marginForCurrent = if (i == 0) {
                thumbnailSize - marginItem - marginItem / 8
            } else {
                marginItem - marginItem / 4
            }

            val layoutOfChild = LinearLayout.LayoutParams(maxWidthOfDuration, thumbnailSize)
            layoutOfChild.setMargins(0, marginForCurrent, 0, 0)

            this@TimeLineTrack.itemTimeLineBinding.layoutEdit.addView(
                layoutMedia.root, layoutOfChild
            )
        }

        val layoutOfDuration = ConstraintLayout.LayoutParams(maxWidthOfDuration, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        layoutOfDuration.setMargins(marginToCenter, 0, marginToCenter, 0)

        this@TimeLineTrack.itemTimeLineBinding.layoutEdit.layoutParams = layoutOfDuration

        this@TimeLineTrack.itemTimeLineBinding.layoutEdit.invalidate()
        this@TimeLineTrack.itemTimeLineBinding.layoutEdit.requestLayout()
    }

    /**
     * Clear all media in view
     */
    fun clearAllMediaAdded() {
        this@TimeLineTrack.listMedia.clear()
        this@TimeLineTrack.totalThumbnailCreated = 0

        // Re draw timeline
        initializeViewForDuration()

        // Re draw track
        initTimeLineForType()
    }

    /**
     * Add media to view
     */
    fun addMediaAndCreateItemView(listMedia : MutableList<MediaObject>) {
        this@TimeLineTrack.listMedia.addAll(listMedia)
        var totalDuration = 0L
        this@TimeLineTrack.listMedia.forEach {
            totalDuration += it.mediaDuration
        }

        this@TimeLineTrack.totalDuration = totalDuration
        this@TimeLineTrack.totalThumbnailCreated = (this@TimeLineTrack.totalDuration / 1000.0).toInt()

        // Set new width for track
        maxWidthOfDuration = (totalThumbnailCreated * thumbnailSize) + (marginToCenter * 2) - thumbnailSize + (marginItem / 2)

        // Re draw timeline
        initializeViewForDuration()

        // Draw track
        drawTrackToView()
    }

    /**
     * Draw track item
     */
    private fun drawTrackToView() {
        // Need reset value
        scrollToStart()
        this@TimeLineTrack.isScrollToStart = false

        this@TimeLineTrack.itemTimeLineBinding.layoutEdit.removeAllViews()
        this@TimeLineTrack.itemTimeLineBinding.layoutEdit.invalidate()

        val layoutOfTrack: LinearLayout = this@TimeLineTrack.itemTimeLineBinding.layoutEdit[0] as LinearLayout
        this@TimeLineTrack.listMedia.forEachIndexed { index, mediaObject ->
            // Image thumb
            val timeToGet = calcThumbCanCreateByItem(mediaObject)

            // Create main layout
            val linearLayoutAddThumb = LinearLayout(this@TimeLineTrack.context)
            linearLayoutAddThumb.orientation = LinearLayout.HORIZONTAL
            linearLayoutAddThumb.gravity = Gravity.CENTER

            // Create item thumb
            for (indexThumb in 1..timeToGet) {
                val imageView = ShapeableImageViewHeight(context)
                val imageLayout =
                    LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                imageView.layoutParams = imageLayout
                imageView.scaleType = ImageView.ScaleType.CENTER_CROP
                imageView.visibility = View.VISIBLE
                imageView.background =
                    ContextCompat.getDrawable(context, android.R.color.transparent)
                imageView.requestLayout()

                // Set corner
                when (indexThumb) {
                    1 -> {
                        imageView.shapeAppearanceModel = imageView.shapeAppearanceModel
                            .toBuilder()
                            .setTopLeftCorner(CornerFamily.ROUNDED, connerImageView)
                            .setBottomLeftCorner(CornerFamily.ROUNDED, connerImageView)
                            .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
                            .setTopRightCorner(CornerFamily.ROUNDED, 0f)
                            .build()
                    }
                    timeToGet -> {
                        imageView.shapeAppearanceModel = imageView.shapeAppearanceModel
                            .toBuilder()
                            .setTopLeftCorner(CornerFamily.ROUNDED, 0f)
                            .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
                            .setBottomRightCorner(CornerFamily.ROUNDED, connerImageView)
                            .setTopRightCorner(CornerFamily.ROUNDED, connerImageView)
                            .build()
                    }
                    else -> {
                        imageView.shapeAppearanceModel = imageView.shapeAppearanceModel
                            .toBuilder()
                            .setTopLeftCorner(CornerFamily.ROUNDED, 0f)
                            .setBottomLeftCorner(CornerFamily.ROUNDED, 0f)
                            .setBottomRightCorner(CornerFamily.ROUNDED, 0f)
                            .setTopRightCorner(CornerFamily.ROUNDED, 0f)
                            .build()
                    }
                }
                imageView.clipToOutline = true
                imageView.outlineProvider = ViewOutlineProvider.BACKGROUND

                // Load thumb
                loadThumbnailToImageView(imageView, indexThumb, mediaObject)

                linearLayoutAddThumb.addView(imageView)
            }

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Margin first item
            if (index == 0) {
                layoutParams.setMargins(marginToCenter, 0, 0, 0)
            }

            linearLayoutAddThumb.layoutParams = layoutParams
            linearLayoutAddThumb.requestLayout()

            // Set tag for item
            linearLayoutAddThumb.tag = index
            //setUpEventLickChooseMedia(linearLayoutAddThumb)

            layoutOfTrack.addView(linearLayoutAddThumb)
            layoutOfTrack.requestLayout()
        }
    }

    /**
     * Scroll to start
     */
    fun scrollToStart() {
        this@TimeLineTrack.isScrollToStart = true
        this@TimeLineTrack.currentDurationInVideo = 0
        this@TimeLineTrack.itemTimeLineBinding.horizontalScroll.smoothScrollTo(
            0,
            0
        )
    }

    /**
     * Calc thumb can be create for view media
     */
    private fun calcThumbCanCreateByItem(mediaMainObject: MediaObject): Int {
        var duration = mediaMainObject.endTime - mediaMainObject.startTime
        if (duration == 0L) {
            duration = mediaMainObject.mediaDuration
        }
        if (duration < 2000) {
            duration = 2000
        }

        val adjThumb = if (duration % mediaMainObject.speed != 0.0) 1 else 0

        return (duration / 1000 / mediaMainObject.speed).toInt() + adjThumb
    }

    /**
     * Load image to image view
     */
    private fun loadThumbnailToImageView(
        imageView: ImageView,
        index: Int,
        mediaMainObject: MediaObject
    ) {

        var requestOptions = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)

        if (mediaMainObject.mediaType.contains(MEDIA_TYPE_VIDEO)) {
            requestOptions = RequestOptions
                .frameOf(index * 1000L)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .skipMemoryCache(false)
        }

        Glide.with(context).clear(imageView)
        Glide.with(context)
            .asBitmap()
            .load(mediaMainObject.mediaPath)
            .apply(requestOptions)
            .thumbnail(0.01f)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Timber.e(e)
                    return true
                }

                override fun onResourceReady(
                    resource: Bitmap?,
                    model: Any?,
                    target: Target<Bitmap>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    imageView.setImageBitmap(resource)
                    return false
                }
            }).into(imageView)
    }
}