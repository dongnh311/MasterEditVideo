package com.dongnh.masteredit.transition.transition.fade

import android.content.Context
import com.dongnh.masteredit.base.AbstractDrawerTransition

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class FadeTransDrawer(context: Context) :
    AbstractDrawerTransition(context) {

    override fun getTransitionShader(context: Context) {
        transitionShader = FadeTransShader(context)
    }
}