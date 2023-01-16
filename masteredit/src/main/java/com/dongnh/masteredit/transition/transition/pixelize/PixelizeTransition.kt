package com.dongnh.masteredit.transition.transition.pixelize

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_PIXELIZE

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class PixelizeTransition(private val context: Context) :
    AbstractTransition(PixelizeTransition::class.java.simpleName, TRANS_PIXELIZE) {

    var width = 20
    var height = 20
    var step = 50

    override fun getDrawer() {
        drawer = PixelizeTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as PixelizeTransDrawer).setSquaresMin(width, height)
        (drawer as PixelizeTransDrawer).setStep(step)
    }
}