package com.dongnh.masteredit.transition.transition.wipeup

import android.content.Context
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class WipeUpTransDrawer(private val context: Context) :
    AbstractDrawerTransition(context) {

    override fun getTransitionShader() {
        transitionShader = WipeUpTransShader(context)
    }
}
