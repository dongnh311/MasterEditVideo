package com.dongnh.masteredit.transition.transition.wipedown

import android.content.Context
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class WipeDownTransDrawer(private val context: Context) :
    AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = WipeDownTransShader(context)
    }
}
