package com.dongnh.masteredit.transition.transition.wipeup

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_WIPE_UP

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class WipeUpTransition(private val context: Context) : AbstractTransition(
    WipeUpTransition::class.java.simpleName,
    TRANS_WIPE_UP
) {

    override fun getDrawer() {
        drawer = WipeUpTransDrawer(context)
    }
}