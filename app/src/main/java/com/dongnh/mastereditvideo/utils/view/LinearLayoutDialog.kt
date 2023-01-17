package com.dongnh.mastereditvideo.utils.view

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import kotlin.math.roundToInt

/**
 * Project : MasterEditVideo
 * Created by DongNH on 17/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class LinearLayoutDialog : LinearLayout {
    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var width = context.resources.displayMetrics.widthPixels
        width = (0.8 * width).roundToInt()
        setMeasuredDimension(width, measuredHeight)
    }
}