package com.dongnh.masteredit.transition.transition.dreamyzoom

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_DREAMY_ZOOM

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class DreamyZoomTransition(private val context: Context) : AbstractTransition(
    DreamyZoomTransition::class.java.simpleName,
    TRANS_DREAMY_ZOOM
) {

    val rotation = 6f
    val scale = 1.2f

    override fun getDrawer() {
        drawer = DreamyZoomTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as DreamyZoomTransDrawer).setRotation(rotation)
        (drawer as DreamyZoomTransDrawer).setScale(scale)
    }
}