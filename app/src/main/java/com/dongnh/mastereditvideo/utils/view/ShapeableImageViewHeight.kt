package com.dongnh.mastereditvideo.utils.view

import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import com.google.android.material.imageview.ShapeableImageView

/**
 * Project : MasterEditVideo
 * Created by DongNH on 06/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class ShapeableImageViewHeight : ShapeableImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(
        context,
        attrs,
        defStyle
    )

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val thumbnailSize =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 45f, resources.displayMetrics)
                .toInt()
        setMeasuredDimension(thumbnailSize, thumbnailSize)
    }
}