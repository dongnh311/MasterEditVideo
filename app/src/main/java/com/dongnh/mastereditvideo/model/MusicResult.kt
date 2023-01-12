package com.dongnh.mastereditvideo.model

import com.dongnh.masteredit.model.MusicModel
import com.dongnh.mastereditvideo.base.BaseResultModel
import com.dongnh.mastereditvideo.base.BaseResultPaginate

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class MusicResult : BaseResultModel<ListMusic>() {
}

class ListMusic : BaseResultPaginate<MusicModel>()