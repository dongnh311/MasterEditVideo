package com.dongnh.masteredit.transition.transition.hexagonal

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_HEXAGONAL

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class HexagonalTransition(private val context: Context) :
    AbstractTransition(HexagonalTransition::class.java.simpleName, TRANS_HEXAGONAL) {

    var step = 50
    var horizontalHexagons = 20f

    override fun getDrawer() {
        drawer = HexagonalTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as HexagonalTransDrawer).setStep(step)
        (drawer as HexagonalTransDrawer).setHorizontalHexagons(horizontalHexagons)
    }
}
