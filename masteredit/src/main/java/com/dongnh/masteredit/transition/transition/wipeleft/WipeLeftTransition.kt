package com.dongnh.masteredit.transition.transition.wipeleft

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_WIPE_LEFT

/**
 * Created by DongNH on 2/3/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class WipeLeftTransition(private val context: Context) : AbstractTransition(
    WipeLeftTransition::class.java.simpleName,
    TRANS_WIPE_LEFT
) {

    override fun getDrawer() {
        drawer = WipeLeftTransDrawer(context)
    }
}
