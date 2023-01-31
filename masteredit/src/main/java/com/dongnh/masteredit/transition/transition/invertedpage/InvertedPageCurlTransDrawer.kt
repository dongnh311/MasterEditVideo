package com.dongnh.masteredit.transition.transition.invertedpage

import android.content.Context
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class InvertedPageCurlTransDrawer(context: Context) :
    AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = InvertedPageCurlTransShader(context)
    }
}