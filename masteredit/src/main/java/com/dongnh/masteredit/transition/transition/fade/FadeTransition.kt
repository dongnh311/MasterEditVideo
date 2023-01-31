package com.dongnh.masteredit.transition.transition.fade

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_FADE

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class FadeTransition(private val context: Context) :
    AbstractTransition(FadeTransition::class.java.simpleName, TRANS_FADE) {

    override fun getDrawer() {
        drawer = FadeTransDrawer(context)
    }
}
