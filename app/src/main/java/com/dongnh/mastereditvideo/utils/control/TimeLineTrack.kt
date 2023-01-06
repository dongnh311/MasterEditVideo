package com.dongnh.mastereditvideo.utils.control

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.databinding.ItemTrackViewBinding
import com.dongnh.mastereditvideo.databinding.LayoutTimeLineBinding
import com.dongnh.mastereditvideo.utils.interfaces.OnDurationTrackScrollListener
import com.google.android.flexbox.FlexboxLayout
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
}