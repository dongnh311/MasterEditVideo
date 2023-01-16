package com.dongnh.masteredit.transition.transition.crosszoom

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_CROSS_ZOOM

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class CrossZoomTransition(private val context: Context) : AbstractTransition(
    "CrossZoomTransition",
    TRANS_CROSS_ZOOM
) {

    val stength = 0.4f

    override fun getDrawer() {
        drawer = CrossZoomTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as CrossZoomTransDrawer).setStrength(stength)
    }
}