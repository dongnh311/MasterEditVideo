package com.dongnh.masteredit.transition.transition.wipedown

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_WIPE_DOWN

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class WipeDownTransition(private val context: Context) : AbstractTransition(
    WipeDownTransition::class.java.simpleName,
    TRANS_WIPE_DOWN
) {

    override fun getDrawer() {
        drawer = WipeDownTransDrawer(context)
    }
}