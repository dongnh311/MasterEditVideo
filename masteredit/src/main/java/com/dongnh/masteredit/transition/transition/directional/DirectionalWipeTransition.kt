package com.dongnh.masteredit.transition.transition.directional

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_DIRECTIONAL_WIPE

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class DirectionalWipeTransition(private val context: Context) : AbstractTransition(
    DirectionalWipeTransition::class.java.simpleName,
    TRANS_DIRECTIONAL_WIPE
) {

    var directionX = 1f
    var directionY = -1f
    var smoothness = 0.5f


    override fun getDrawer() {
        drawer = DirectionalWipeTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as DirectionalWipeTransDrawer).setDirection(directionX, directionY)
        (drawer as DirectionalWipeTransDrawer).setSmoothness(smoothness)
    }
}