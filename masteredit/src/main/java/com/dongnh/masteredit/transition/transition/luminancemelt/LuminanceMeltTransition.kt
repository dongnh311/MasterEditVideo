package com.dongnh.masteredit.transition.transition.luminancemelt

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_LUMINANCE_MELT

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class LuminanceMeltTransition(private val context: Context) : AbstractTransition(
    LuminanceMeltTransition::class.java.simpleName,
    TRANS_LUMINANCE_MELT
) {

    val TAG = "LuminanceMeltTransition"

    val down = true
    val threshold = 0.8f
    val above = false

    override fun getDrawer() {
        drawer = LuminanceMeltTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as LuminanceMeltTransDrawer).setAbove(above)
        (drawer as LuminanceMeltTransDrawer).setDirection(down)
        (drawer as LuminanceMeltTransDrawer).setThreshold(threshold)
    }
}
