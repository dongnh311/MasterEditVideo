package com.dongnh.mastereditvideo.utils.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import com.dongnh.masteredit.enums.FormatVideoOut
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.utils.interfaces.OnExportClickListener

/**
 * Project : MasterEditVideo
 * Dialog for export settings and progress.
 */
class DialogExport(private val context: Context) {

    private var dialog: Dialog? = null

    var onExportClickListener: OnExportClickListener? = null

    private var selectedResolution: FormatVideoOut = FormatVideoOut.RESOLUTION_HD

    // Views
    private var radioResolution: RadioGroup? = null
    private var progressBar: ProgressBar? = null
    private var textProgress: TextView? = null
    private var textStatus: TextView? = null
    private var btnExport: TextView? = null
    private var btnCancel: TextView? = null

    fun showDialogExport() {
        dialog?.dismiss()

        dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.dialog_export)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window?.setLayout(
                (context.resources.displayMetrics.widthPixels * 0.55).toInt(),
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setCancelable(false)
        }

        // Bind views
        radioResolution = dialog?.findViewById(R.id.radio_resolution)
        progressBar = dialog?.findViewById(R.id.progress_bar)
        textProgress = dialog?.findViewById(R.id.text_progress)
        textStatus = dialog?.findViewById(R.id.text_status)
        btnExport = dialog?.findViewById(R.id.btn_export)
        btnCancel = dialog?.findViewById(R.id.btn_cancel)

        // Reset state
        selectedResolution = FormatVideoOut.RESOLUTION_HD
        progressBar?.visibility = View.GONE
        textProgress?.visibility = View.GONE
        textStatus?.visibility = View.GONE
        btnExport?.visibility = View.VISIBLE
        btnExport?.isEnabled = true
        radioResolution?.isEnabled = true
        btnCancel?.text = context.getString(R.string.common_btn_cancel)

        radioResolution?.setOnCheckedChangeListener { _, checkedId ->
            selectedResolution = when (checkedId) {
                R.id.radio_hd -> FormatVideoOut.RESOLUTION_HD
                R.id.radio_full_hd -> FormatVideoOut.RESOLUTION_FULL_HD
                R.id.radio_qhd -> FormatVideoOut.RESOLUTION_Q_HD
                else -> FormatVideoOut.RESOLUTION_HD
            }
        }

        btnExport?.setOnClickListener {
            radioResolution?.isEnabled = false
            btnExport?.isEnabled = false
            progressBar?.visibility = View.VISIBLE
            progressBar?.progress = 0
            textProgress?.visibility = View.VISIBLE
            textProgress?.text = context.getString(R.string.export_progress, 0)
            onExportClickListener?.onStartExport(selectedResolution)
        }

        btnCancel?.setOnClickListener {
            onExportClickListener?.onCancelExport()
            dismiss()
        }

        dialog?.show()
    }

    fun dismiss() {
        dialog?.dismiss()
    }

    fun updateProgress(percent: Int) {
        progressBar?.progress = percent
        textProgress?.text = context.getString(R.string.export_progress, percent)
    }

    fun showExportComplete() {
        progressBar?.visibility = View.GONE
        textProgress?.visibility = View.GONE
        textStatus?.visibility = View.VISIBLE
        textStatus?.text = context.getString(R.string.export_complete)
        textStatus?.setTextColor(context.getColor(R.color.colorPrimary))
        btnCancel?.text = context.getString(R.string.common_btn_ok)
        btnExport?.visibility = View.GONE
    }

    fun showExportError(message: String) {
        progressBar?.visibility = View.GONE
        textProgress?.visibility = View.GONE
        textStatus?.visibility = View.VISIBLE
        textStatus?.text = message
        textStatus?.setTextColor(context.getColor(R.color.colorWhite))
        btnExport?.isEnabled = true
        radioResolution?.isEnabled = true
    }
}
