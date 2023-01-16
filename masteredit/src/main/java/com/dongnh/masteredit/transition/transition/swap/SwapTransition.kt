package com.dongnh.masteredit.transition.transition.swap

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_SWAP

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class SwapTransition(private val context: Context) :
    AbstractTransition(SwapTransition::class.java.simpleName, TRANS_SWAP) {

    var reflection = 0.4f
    var perspective = 0.2f
    var depth = 3.0f

    override fun getDrawer() {
        drawer = SwapTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as SwapTransDrawer).setReflection(reflection)
        (drawer as SwapTransDrawer).setPerspective(perspective)
        (drawer as SwapTransDrawer).setDepth(depth)
    }
}