package com.dongnh.masteredit.transition.transition.simplezoom

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_SIMPLE_ZOOM

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class SimpleZoomTransition(private val context: Context) :
    AbstractTransition("SimpleZoomTransition", TRANS_SIMPLE_ZOOM) {

    val zoomQuickness = 0.8f

    override fun getDrawer() {
        drawer = SimpleZoomTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as SimpleZoomTransDrawer).setZoomQuickness(zoomQuickness)
    }
}