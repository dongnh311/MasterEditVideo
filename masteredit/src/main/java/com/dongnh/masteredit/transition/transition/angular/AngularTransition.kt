package com.dongnh.masteredit.transition.transition.angular

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_ANGULAR

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class AngularTransition(private val context: Context) :
    AbstractTransition(AngularTransition::class.java.simpleName, TRANS_ANGULAR) {

    var startAngle = 90.0f

    override fun checkRational(): Boolean {
        return startAngle in 0.0..360.0
    }

    override fun getDrawer() {
        drawer = context?.let { AngularTransDrawer(it) }
    }

    override fun setDrawerParams() {
        (drawer as AngularTransDrawer).setStartAngular(startAngle)
    }
}
