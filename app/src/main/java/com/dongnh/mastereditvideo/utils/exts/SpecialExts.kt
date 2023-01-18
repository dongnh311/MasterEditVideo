package com.dongnh.mastereditvideo.utils.exts

import com.dongnh.masteredit.const.*
import com.dongnh.masteredit.model.SpecialModel

/**
 * Project : MasterEditVideo
 * Created by DongNH on 18/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */


/**
 * Check is item is not item none
 */
fun isNotItemNone(specialModel: SpecialModel): Boolean {
    return specialModel.id != ITEM_EFFECT_NONE && specialModel.id != ITEM_FILTER_NONE
            && specialModel.id != ITEM_GRAPH_NONE
            && specialModel.id != ITEM_TRANSITION_NONE
            && specialModel.id != ITEM_SPECIAL_NONE
}