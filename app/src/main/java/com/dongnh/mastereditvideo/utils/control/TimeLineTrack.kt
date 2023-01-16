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
import com.dongnh.masteredit.model.MediaModel
import com.dongnh.masteredit.model.MusicModel
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.MEDIA_TYPE_VIDEO
import com.dongnh.mastereditvideo.databinding.ItemTrackBinding
import com.dongnh.mastereditvideo.databinding.ItemTrackViewBinding
import com.dongnh.mastereditvideo.databinding.LayoutTimeLineBinding
import com.dongnh.mastereditvideo.utils.interfaces.OnDurationTrackScrollListener
import com.dongnh.mastereditvideo.utils.interfaces.OnItemMediaChoose
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
class TimeLineTrack : FrameLayout {
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

    // Adj to check add more thumb
    private var adjToPlugThumb = 0.5f

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
    private val listMedia: MutableList<MediaModel> = mutableListOf()

    // List Music
    private val listMusics = mutableListOf<MusicModel>()

    // Lister
    var onItemMediaChoose: OnItemMediaChoose? = null

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
                textView.textSize = 6f
                textView.setTextColor(context.getColor(R.color.colorTextSelected))
            }

            textView.gravity = Gravity.START
            val layoutParam = FlexboxLayout.LayoutParams(
                (thumbnailSize + (marginItem)),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Set margin to center item
            if (i == 0) {
                layoutParam.setMargins(marginToCenter - thumbnailSize, 0, 0, 0)
            } else if ((i + 1) == sizeToCreate) {
                layoutParam.setMargins(0, 0, marginToCenter, 0)
            } else {
                layoutParam.setMargins((marginItem) / 2, 0, 0, 0)
            }

            this@TimeLineTrack.itemTimeLineBinding.layoutDuration.addView(
                textView, layoutParam
            )
        }

        maxWidthOfDuration =
            (totalThumbnailCreated * thumbnailSize) + (marginToCenter * 2) - thumbnailSize + (marginItem)
        val layoutOfDuration = ConstraintLayout.LayoutParams(
            maxWidthOfDuration,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
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
            val durationCalc = (scrollX / (thumbnailSize.toDouble() - (marginItem / 4))) * 1000L
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
            val layoutMedia: ItemTrackViewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_track_view,
                null,
                false
            )

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
                else -> {
                    layoutMedia.root.id = R.id.track_music
                    // Only music is vertical
                    layoutMedia.mainAddTrack.orientation = LinearLayout.VERTICAL
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

        val layoutOfDuration = ConstraintLayout.LayoutParams(
            maxWidthOfDuration,
            ConstraintLayout.LayoutParams.WRAP_CONTENT
        )
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
        this@TimeLineTrack.listMusics.clear()
        this@TimeLineTrack.totalThumbnailCreated = 0

        // Re draw timeline
        initializeViewForDuration()

        this@TimeLineTrack.itemTimeLineBinding.layoutEdit.removeAllViews()
        this@TimeLineTrack.itemTimeLineBinding.layoutEdit.invalidate()
        // Re draw track
        initTimeLineForType()
    }

    /**
     * Add media to view
     */
    fun addMediaAndCreateItemView(listMedia: MutableList<MediaModel>) {
        this@TimeLineTrack.listMedia.clear()
        this@TimeLineTrack.listMedia.addAll(listMedia)
        this@TimeLineTrack.totalDuration = 0
        var totalThumb = 0
        this@TimeLineTrack.listMedia.forEach {
            var thumb = (it.mediaDuration / 1000.0).toInt()
            if ((it.mediaDuration - (thumb * 1000.0)) / 1000.0 > adjToPlugThumb) {
                thumb += 1
            }
            totalThumb += thumb
            this@TimeLineTrack.totalDuration += (it.endAt - it.beginAt)
        }

        this@TimeLineTrack.totalThumbnailCreated = totalThumb

        // Re draw timeline
        initializeViewForDuration()

        // Draw track
        drawTrackToView()

        // draw music
        if (listMusics.isNotEmpty()) {
            val list = mutableListOf<MusicModel>()
            list.addAll(listMusics)
            addMusicToTrackView(list)
        }
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
        // Re draw track
        initTimeLineForType()

        val layoutOfTrack: LinearLayout =
            this@TimeLineTrack.itemTimeLineBinding.layoutEdit[0] as LinearLayout
        this@TimeLineTrack.listMedia.forEachIndexed { index, mediaObject ->
            // Image thumb
            val timeToGet = calcThumbCanCreateByItem(mediaObject)

            // Create main layout
            val linearLayoutAddThumb: ItemTrackBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_track,
                null,
                false
            )
            linearLayoutAddThumb.mainAddView.orientation = LinearLayout.HORIZONTAL
            linearLayoutAddThumb.mainAddView.gravity = Gravity.CENTER

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

                linearLayoutAddThumb.mainAddView.addView(imageView)
            }

            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            // Margin first item
            if (index == 0) {
                layoutParams.setMargins(marginToCenter - thumbnailSize + marginItem, 0, 0, 0)
            } else {
                layoutParams.setMargins(0, 0, 0, 0)
            }

            linearLayoutAddThumb.mainAddView.layoutParams = layoutParams
            linearLayoutAddThumb.mainAddView.requestLayout()

            // Set tag for item
            linearLayoutAddThumb.mainAddView.tag = 1
            linearLayoutAddThumb.viewIndex.tag = index
            linearLayoutAddThumb.viewObject.tag = mediaObject
            setUpEventLickChooseMedia(linearLayoutAddThumb)
            linearLayoutAddThumb.mainAddView.setBackgroundResource(R.drawable.bg_media_normal)

            layoutOfTrack.addView(linearLayoutAddThumb.mainAddView)
            layoutOfTrack.requestLayout()
        }
    }

    /**
     * Add music
     */
    fun addMusicToTrackView(listMusic: MutableList<MusicModel>) {
        this@TimeLineTrack.listMusics.clear()
        this@TimeLineTrack.listMusics.addAll(listMusic)

        // Add more line
        for (i in 0 until listMusic.size - 1) {
            val layoutMedia: ItemTrackViewBinding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_track_view,
                null,
                false
            )
            val marginForCurrent = marginItem - marginItem / 4

            val layoutOfChild = LinearLayout.LayoutParams(maxWidthOfDuration, thumbnailSize)
            layoutOfChild.setMargins(0, marginForCurrent, 0, 0)

            this@TimeLineTrack.itemTimeLineBinding.layoutEdit.addView(
                layoutMedia.root, layoutOfChild
            )

        }

        // Need reset value
        scrollToStart()
        this@TimeLineTrack.isScrollToStart = false

        try {

            // Add music
            val adjIndex = 4
            listMusic.forEachIndexed { index, musicModel ->
                val layoutOfTrack: LinearLayout =
                    this@TimeLineTrack.itemTimeLineBinding.layoutEdit[adjIndex + index] as LinearLayout
                layoutOfTrack.orientation = LinearLayout.VERTICAL
                layoutOfTrack.gravity = Gravity.START or Gravity.CENTER

                // Create main layout
                val linearLayoutViewMusic: ItemTrackBinding = DataBindingUtil.inflate(
                    LayoutInflater.from(context),
                    R.layout.item_track,
                    null,
                    false
                )

                linearLayoutViewMusic.mainAddView.orientation = LinearLayout.HORIZONTAL
                linearLayoutViewMusic.mainAddView.gravity = Gravity.START or Gravity.CENTER

                val draw = ContextCompat.getDrawable(context, R.drawable.visualize_sound_time_line)
                val width = draw?.intrinsicWidth
                Timber.e("Image music width : $width")

                // Set it is not choose
                linearLayoutViewMusic.mainAddView.tag = 1

                // Add thumb
                var totalCreate = 0
                if (width != null) {
                    for (time in 0 until 100000) {
                        totalCreate += width
                        if (totalCreate < maxWidthOfDuration) {
                            val imageView = ImageView(context)
                            val imageLayout =
                                LayoutParams(
                                    LinearLayout.LayoutParams.WRAP_CONTENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT
                                )
                            imageView.layoutParams = imageLayout
                            imageView.scaleType = ImageView.ScaleType.FIT_CENTER
                            imageView.background = draw
                            linearLayoutViewMusic.mainAddView.background = draw

                            var marginStart = 0
                            if (time == 0) marginStart = marginToCenter - thumbnailSize + marginItem
                            imageLayout.setMargins(marginStart, 0, 0, 0)

                            linearLayoutViewMusic.mainAddView.addView(imageView)
                        } else {
                            break
                        }
                    }
                }

                linearLayoutViewMusic.mainAddView.requestLayout()

                linearLayoutViewMusic.viewObject.tag = musicModel
                linearLayoutViewMusic.viewIndex.tag = index

                // Set margin for item
                val layout = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
                )

                val marginForCurrent = if (index == 0) {
                    0
                } else {
                    marginItem - marginItem / 4
                }

                layout.setMargins(0, marginForCurrent, marginToCenter, 0)
                linearLayoutViewMusic.mainAddView.layoutParams = layout

                setUpEventMusicChoose(linearLayoutViewMusic)

                // Add to parent view
                layoutOfTrack.addView(linearLayoutViewMusic.root)
            }

            // Make layout edit is scale
            val layoutOfDuration = ConstraintLayout.LayoutParams(
                maxWidthOfDuration,
                ConstraintLayout.LayoutParams.WRAP_CONTENT
            )
            layoutOfDuration.setMargins(marginToCenter, 0, marginToCenter, 0)
            this@TimeLineTrack.itemTimeLineBinding.layoutEdit.layoutParams = layoutOfDuration

            this@TimeLineTrack.itemTimeLineBinding.layoutEdit.invalidate()
            this@TimeLineTrack.itemTimeLineBinding.layoutEdit.requestLayout()
        } catch (ex: java.lang.Exception) {
            Timber.e(ex)
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
    private fun calcThumbCanCreateByItem(mediaMainObject: MediaModel): Int {
        var duration = mediaMainObject.endAt - mediaMainObject.beginAt
        if (duration == 0L) {
            duration = mediaMainObject.mediaDuration
        }
        if (duration < 2000) {
            duration = 2000
        }

        var thumb = (duration / 1000.0).toInt()
        if ((duration - (thumb * 1000.0)) / 1000.0 > adjToPlugThumb) {
            thumb += 1
        }

        return thumb
    }

    /**
     * Load image to image view
     */
    private fun loadThumbnailToImageView(
        imageView: ImageView,
        index: Int,
        mediaMainObject: MediaModel
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

    /**
     * Scroll list track when play
     */
    fun updateScrollOfMainView() {
        val duration = (thumbnailSize + (marginItem / 4)) / 1000.0 * 10.0
        this@TimeLineTrack.currentDurationInView += duration
        this@TimeLineTrack.itemTimeLineBinding.horizontalScroll.smoothScrollTo(
            (currentDurationInView).toInt(),
            0
        )
    }

    /**
     * Add event click on item
     */
    private fun setUpEventLickChooseMedia(
        itemTrackBinding: ItemTrackBinding
    ) {
        val linearLayout = itemTrackBinding.mainAddView
        // Allow child click to main
        for (index in 0 until linearLayout.childCount) {
            linearLayout.getChildAt(index).setOnClickListener {
                if (linearLayout.getChildAt(0).tag == null || linearLayout.getChildAt(0).tag == 0) {
                    linearLayout.getChildAt(0).tag = 1
                    linearLayout.setBackgroundResource(R.drawable.bg_media_click)
                    onItemMediaChoose?.onItemMediaChoose(
                        itemTrackBinding.viewIndex.tag as Int
                    )
                } else {
                    linearLayout.getChildAt(0).tag = 0
                    linearLayout.setBackgroundResource(R.drawable.bg_media_normal)
                    onItemMediaChoose?.onItemMediaCancel(
                        itemTrackBinding.viewIndex.tag as Int
                    )
                }

                // Reset color of view
                val parentView = linearLayout.parent as LinearLayout
                for (indexInParent in 0 until parentView.childCount) {
                    val child = parentView.getChildAt(indexInParent) as LinearLayout
                    if (child.tag != linearLayout.tag) {
                        child.getChildAt(0).tag = 0
                        child.setBackgroundResource(R.drawable.bg_media_normal)
                    }
                }
            }
        }
    }

    /**
     * Change color if need
     */
    private fun setUpEventMusicChoose(childViewMusic: ItemTrackBinding) {
        childViewMusic.mainAddView.setOnClickListener {
            if (childViewMusic.mainAddView.tag == null || childViewMusic.mainAddView.tag == 0) {
                childViewMusic.mainAddView.tag = 1
                childViewMusic.mainAddView.setBackgroundResource(R.drawable.bg_media_click)
                onItemMediaChoose?.onMusicChoose(
                    childViewMusic.viewIndex.tag as Int
                )
            } else {
                childViewMusic.mainAddView.tag = 0
                childViewMusic.mainAddView.setBackgroundResource(android.R.color.transparent)
                onItemMediaChoose?.onItemMediaCancel(
                    childViewMusic.viewIndex.tag as Int
                )
            }
        }
    }

}