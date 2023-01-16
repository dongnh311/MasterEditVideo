package com.dongnh.masteredit.transition.transition.circle

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_CIRCLE_OPEN

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class CircleOpenTransition(private val context: Context) :
    AbstractTransition(CircleOpenTransition::class.java.simpleName, TRANS_CIRCLE_OPEN) {

    var smoothness = 0.3f
    var opening = true

    override fun getDrawer() {
        drawer = context?.let { CircleOpenTransDrawer(it) }
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as CircleOpenTransDrawer).setSmoothness(smoothness)
        (drawer as CircleOpenTransDrawer).setOpening(opening)
    }
}