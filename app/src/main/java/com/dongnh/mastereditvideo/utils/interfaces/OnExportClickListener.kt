package com.dongnh.mastereditvideo.utils.interfaces

import com.dongnh.masteredit.enums.FormatVideoOut

/**
 * Project : MasterEditVideo
 * Callback for export dialog actions.
 */
interface OnExportClickListener {
    fun onStartExport(resolution: FormatVideoOut)
    fun onCancelExport()
}
