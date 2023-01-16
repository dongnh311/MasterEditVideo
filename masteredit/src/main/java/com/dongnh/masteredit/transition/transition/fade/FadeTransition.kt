package com.dongnh.masteredit.transition.transition.fade

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_FADE

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class FadeTransition(private val context: Context) :
    AbstractTransition(FadeTransition::class.java.simpleName, TRANS_FADE) {

    override fun getDrawer() {
        drawer = FadeTransDrawer(context)
    }
}
