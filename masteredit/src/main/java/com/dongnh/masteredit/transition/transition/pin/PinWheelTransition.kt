package com.dongnh.masteredit.transition.transition.pin

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_PIN_WHEEL

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class PinWheelTransition(private val context: Context) :
    AbstractTransition(PinWheelTransition::class.java.simpleName, TRANS_PIN_WHEEL) {

    var speed = 2.0f

    override fun getDrawer() {
        drawer = PinWheelTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as PinWheelTransDrawer).setSpeed(speed)
    }
}