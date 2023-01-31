package com.dongnh.masteredit.transition.transition.windowslice

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_WINDOW_SLICE

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class WindowSliceTransition(private val context: Context) : AbstractTransition(
    "WindowSliceTransition",
    TRANS_WINDOW_SLICE
) {

    val count = 10.0f
    val smoothness = 0.5f

    override fun getDrawer() {
        drawer = WindowSliceTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as WindowSliceTransDrawer).setCount(count)
        (drawer as WindowSliceTransDrawer).setSmoothness(smoothness)
    }
}
