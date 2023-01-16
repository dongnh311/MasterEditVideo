package com.dongnh.masteredit.transition.transition.crosshatch

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_CROSS_HATCH

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class CrossHatchTransition(private val context: Context) :
    AbstractTransition("CrossHatchTransition", TRANS_CROSS_HATCH) {

    val TAG = "CrossHatchTransition"

    val centerX = 0.5f
    var centerY = 0.5f
    val threshold = 3.0f
    val fadeEdge = 0.1f

    override fun getDrawer() {
        drawer = CrossHatchTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as CrossHatchTransDrawer).setCenter(centerX, centerY)
        (drawer as CrossHatchTransDrawer).setThreshold(threshold)
        (drawer as CrossHatchTransDrawer).setFadeEdge(fadeEdge)
    }
}