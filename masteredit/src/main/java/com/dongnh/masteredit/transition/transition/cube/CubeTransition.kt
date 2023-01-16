package com.dongnh.masteredit.transition.transition.cube

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_CUBE

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class CubeTransition(private val context: Context) :
    AbstractTransition(CubeTransition::class.java.simpleName, TRANS_CUBE) {

    var perspective = 0.7f
    var unzoom = 0.3f
    var reflection = 0.4f
    var floating = 3.0f

    override fun getDrawer() {
        drawer = CubeTransDrawer(context)
    }

    override fun setDrawerParams() {
        super.setDrawerParams()
        (drawer as CubeTransDrawer).setPerspective(perspective)
        (drawer as CubeTransDrawer).setUnzoom(unzoom)
        (drawer as CubeTransDrawer).setReflection(reflection)
        (drawer as CubeTransDrawer).setFloating(floating)
    }
}