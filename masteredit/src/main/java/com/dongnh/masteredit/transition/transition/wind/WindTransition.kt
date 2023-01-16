package com.dongnh.masteredit.transition.transition.wind

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_WIND

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class WindTransition(private val context: Context) :
    AbstractTransition(WindTransition::class.java.simpleName, TRANS_WIND) {

    var size = 0.2f

    override fun getDrawer() {
        drawer = WindTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as WindTransDrawer).setSize(size)
    }
}
