package com.dongnh.masteredit.utils.exts

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.transition.transition.angular.AngularTransition
import com.dongnh.masteredit.transition.transition.circle.CircleOpenTransition
import com.dongnh.masteredit.transition.transition.crosshatch.CrossHatchTransition
import com.dongnh.masteredit.transition.transition.crosszoom.CrossZoomTransition
import com.dongnh.masteredit.transition.transition.cube.CubeTransition
import com.dongnh.masteredit.transition.transition.directional.DirectionalWipeTransition
import com.dongnh.masteredit.transition.transition.dreamyzoom.DreamyZoomTransition
import com.dongnh.masteredit.transition.transition.fade.FadeTransition
import com.dongnh.masteredit.transition.transition.hexagonal.HexagonalTransition
import com.dongnh.masteredit.transition.transition.invertedpage.InvertedPageCurlTransition
import com.dongnh.masteredit.transition.transition.luminancemelt.LuminanceMeltTransition
import com.dongnh.masteredit.transition.transition.perlin.PerlinTransition
import com.dongnh.masteredit.transition.transition.pin.PinWheelTransition
import com.dongnh.masteredit.transition.transition.pixelize.PixelizeTransition
import com.dongnh.masteredit.transition.transition.simplezoom.SimpleZoomTransition
import com.dongnh.masteredit.transition.transition.swap.SwapTransition
import com.dongnh.masteredit.transition.transition.wind.WindTransition
import com.dongnh.masteredit.transition.transition.windowslice.WindowSliceTransition
import com.dongnh.masteredit.transition.transition.wipedown.WipeDownTransition
import com.dongnh.masteredit.transition.transition.wipeleft.WipeLeftTransition
import com.dongnh.masteredit.transition.transition.wiperight.WipeRightTransition
import com.dongnh.masteredit.transition.transition.wipeup.WipeUpTransition
import kr.brickmate.clllap.utils.openegl.transition.bounce.BounceTransition

/**
 * Project : MasterEditVideo
 * Created by DongNH on 31/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */

/**
 * Create transition by id
 */
fun getTransitionById(id: Int, context: Context?): AbstractTransition? {
    var transition: AbstractTransition? = null
    when (id) {
        600001 -> if (context != null) {
            transition = WindowSliceTransition(context)
        }
        600002 -> if (context != null) {
            transition = SimpleZoomTransition(context)
        }
        600003 -> if (context != null) {
            transition = CrossZoomTransition(context)
        }
        600004 -> if (context != null) {
            transition = LuminanceMeltTransition(context)
        }
        600005 -> if (context != null) {
            transition = CrossHatchTransition(context)
        }
        600006 -> if (context != null) {
            transition = WipeRightTransition(context)
        }
        600007 -> if (context != null) {
            transition = WipeLeftTransition(context)
        }
        600008 -> if (context != null) {
            transition = WipeDownTransition(context)
        }
        600009 -> if (context != null) {
            transition = WipeUpTransition(context)
        }
        600010 -> if (context != null) {
            transition = DreamyZoomTransition(context)
        }
        600011 -> if (context != null) {
            transition = FadeTransition(context)
        }
        600012 -> if (context != null) {
            transition = DirectionalWipeTransition(context)
        }
        600013 -> if (context != null) {
            transition = WindTransition(context)
        }
        600014 -> if (context != null) {
            transition = InvertedPageCurlTransition(context)
        }
        600015 -> if (context != null) {
            transition = SwapTransition(context)
        }
        600016 -> if (context != null) {
            transition = CubeTransition(context)
        }
        600017 -> if (context != null) {
            transition = CircleOpenTransition(context)
        }
        600018 -> if (context != null) {
            transition = PinWheelTransition(context)
        }
        600019 -> if (context != null) {
            transition = AngularTransition(context)
        }
        600020 -> if (context != null) {
            transition = HexagonalTransition(context)
        }
        600021 -> if (context != null) {
            transition = PixelizeTransition(context)
        }
        600022 -> if (context != null) {
            transition = PerlinTransition(context)
        }
        600023 -> if (context != null) {
            transition = BounceTransition(context)
        }
        else -> {
        }
    }
    return transition
}