package com.dongnh.masteredit.transition.transition.wiperight

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_WIPE_RIGHT

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class WipeRightTransition(private val context: Context) :
    AbstractTransition(WipeRightTransition::class.java.simpleName, TRANS_WIPE_RIGHT) {

    override fun getDrawer() {
        drawer = WipeRightTransDrawer(context)
    }
}
