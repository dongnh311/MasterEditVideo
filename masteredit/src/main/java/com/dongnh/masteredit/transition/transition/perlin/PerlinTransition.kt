package com.dongnh.masteredit.transition.transition.perlin

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_PERLIN

/**
 * Created by DongNH on 16/01/2023.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@dnkinno.com or hoaidongit5@gmail.com.
 */
class PerlinTransition(private val context: Context) :
    AbstractTransition(PerlinTransition::class.java.simpleName, TRANS_PERLIN) {

    var scale = 4.0f
    var smoothness = 0.01f
    var seed = 12.9898f

    override fun getDrawer() {
        drawer = PerlinTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as PerlinTransDrawer).setScale(scale)
        (drawer as PerlinTransDrawer).setSmoothness(smoothness)
        (drawer as PerlinTransDrawer).setSeed(seed)
    }
}