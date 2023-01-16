package kr.brickmate.clllap.utils.openegl.transition.bounce

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_BOUNCE

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class BounceTransition(private val context: Context) :
    AbstractTransition(BounceTransition::class.java.simpleName, TRANS_BOUNCE) {

    var shadowRed = 0f
    var shadowGreen = 0f
    var shadowBlue = 0f
    var shadowAlpha = 0.6f
    var shadowHeight = 0.075f
    var bounces = 3.0f


    override fun getDrawer() {
        drawer = BounceTransDrawer(context)
    }

    override fun setDrawerParams() {
        (drawer as BounceTransDrawer).setShadowColor(
            shadowRed,
            shadowGreen,
            shadowBlue,
            shadowAlpha
        )
        (drawer as BounceTransDrawer).setShadowHeight(shadowHeight)
        (drawer as BounceTransDrawer).setBounces(bounces)
    }
}