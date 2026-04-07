package com.dongnh.masteredit.export

/**
 * Project : MasterEditVideo
 * Callback interface for video export progress and completion.
 */
interface ExportListener {
    fun onExportProgress(percent: Int)
    fun onExportComplete(outputPath: String)
    fun onExportError(message: String)
    fun onExportCancelled()
}
