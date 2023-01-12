package com.dongnh.mastereditvideo.view.pickmusic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.databinding.FragmentPickMusicBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class PickMusicFragment: BottomSheetDialogFragment() {

    lateinit var dataBinding: FragmentPickMusicBinding

     override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.customBottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this@PickMusicFragment.dataBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_pick_music,
            container,
            false
        )
        return this@PickMusicFragment.dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this@PickMusicFragment.dataBinding.lifecycleOwner = this@PickMusicFragment

        this@PickMusicFragment.isCancelable = true
    }
}