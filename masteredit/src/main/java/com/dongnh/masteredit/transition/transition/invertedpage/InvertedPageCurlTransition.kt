package com.dongnh.masteredit.transition.transition.invertedpage

import android.content.Context
import com.dongnh.masteredit.base.AbstractTransition
import com.dongnh.masteredit.const.TRANS_INVERTED_PAGE_CURL

/**
 * Created by DongNH on 2/17/21.
 * Discord: BM Dong(9521)
 * Email : hoaidongit5@brickmate.kr or hoaidongit5@gmail.com.
 */
class InvertedPageCurlTransition(private val context: Context) : AbstractTransition(
    InvertedPageCurlTransition::class.java.simpleName,
    TRANS_INVERTED_PAGE_CURL
) {

    override fun getDrawer() {
        drawer = InvertedPageCurlTransDrawer(context)
    }
}
