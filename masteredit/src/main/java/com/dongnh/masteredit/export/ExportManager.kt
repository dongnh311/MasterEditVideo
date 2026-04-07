package com.dongnh.masteredit.export

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.dongnh.masteredit.const.MEDIA_TYPE_IMAGE
import com.dongnh.masteredit.enums.FormatVideoOut
import com.dongnh.masteredit.model.MediaModel
import com.dongnh.masteredit.model.MusicModel
import timber.log.Timber
import java.io.File
import java.util.Locale

/**
 * Project : MasterEditVideo
 * Manages video export using FFmpeg filter_complex concat + audio mixing.
 */
class ExportManager(private val context: Context) {

    var exportListener: ExportListener? = null

    @Volatile
    private var currentSession: FFmpegSession? = null

    @Synchronized
    fun exportVideo(
        listMedia: MutableList<MediaModel>,
        listMusic: MutableList<MusicModel>,
        resolution: FormatVideoOut,
        outputPath: String
    ) {
        if (currentSession != null) {
            exportListener?.onExportError("Export already in progress")
            return
        }

        if (listMedia.isEmpty()) {
            exportListener?.onExportError("No media to export")
            return
        }

        listMedia.forEach { media ->
            if (media.mediaType == MEDIA_TYPE_IMAGE && media.pathVideoTransform.isEmpty()) {
                exportListener?.onExportError("Image '${media.mediaName}' has not been converted")
                return
            }
        }

        val totalDurationMs = listMedia.sumOf { it.endAt - it.beginAt }
        if (totalDurationMs <= 0) {
            exportListener?.onExportError("Project duration is zero")
            return
        }

        val command = buildCommand(listMedia, listMusic.firstOrNull { !it.pathInLocal.isNullOrEmpty() }, resolution, totalDurationMs, outputPath)

        Timber.d("Export FFmpeg command: $command")

        currentSession = FFmpegKit.executeAsync(
            command,
            { session ->
                synchronized(this) { currentSession = null }
                when {
                    ReturnCode.isSuccess(session.returnCode) -> {
                        Timber.d("Export completed: $outputPath")
                        exportListener?.onExportComplete(outputPath)
                    }
                    ReturnCode.isCancel(session.returnCode) -> {
                        File(outputPath).delete()
                        exportListener?.onExportCancelled()
                    }
                    else -> {
                        Timber.e("Export failed. RC: ${session.returnCode}")
                        Timber.e("FFmpeg output: ${session.output}")
                        File(outputPath).delete()
                        exportListener?.onExportError("Export failed")
                    }
                }
            },
            { log -> Timber.d("FFmpeg: ${log.message}") },
            { statistics ->
                if (totalDurationMs > 0 && statistics.time > 0) {
                    val percent = ((statistics.time.toFloat() / totalDurationMs.toFloat()) * 100)
                        .toInt().coerceIn(0, 100)
                    exportListener?.onExportProgress(percent)
                }
            }
        )
    }

    @Synchronized
    fun cancelExport() {
        currentSession?.let { FFmpegKit.cancel(it.sessionId) }
        currentSession = null
    }

    private fun buildCommand(
        listMedia: MutableList<MediaModel>,
        music: MusicModel?,
        resolution: FormatVideoOut,
        totalDurationMs: Long,
        outputPath: String
    ): String {
        val w = resolution.width
        val h = resolution.height
        val n = listMedia.size

        val sb = StringBuilder()

        // Add each media as a separate input
        listMedia.forEach { media ->
            val path = if (media.mediaType == MEDIA_TYPE_IMAGE) media.pathVideoTransform else media.mediaPath
            sb.append("-i $path ")
        }

        // Add music input if present
        if (music != null) {
            sb.append("-i ${music.pathInLocal} ")
        }

        // Build filter_complex: normalize fps, scale each input, then concat
        sb.append("-filter_complex ")
        val filterParts = StringBuilder()
        for (i in 0 until n) {
            filterParts.append("[$i:v]fps=30,scale=${w}:${h},setsar=1[v$i];")
        }
        for (i in 0 until n) {
            filterParts.append("[v$i]")
        }
        filterParts.append("concat=n=$n:v=1:a=0[outv]")
        sb.append("$filterParts ")

        // Map output
        sb.append("-map [outv] ")

        if (music != null) {
            sb.append("-map ${n}:a ")
            val vol = String.format(Locale.US, "%.2f", music.volume / 100.0f)
            sb.append("-c:a aac -b:a 128k -af volume=$vol ")
            sb.append("-t ${formatSec(totalDurationMs)} ")
        } else {
            sb.append("-an ")
        }

        sb.append("-r 30 -c:v mpeg4 -q:v 5 ")
        sb.append("-movflags +faststart ")
        sb.append("-y $outputPath")

        return sb.toString()
    }

    private fun formatSec(ms: Long): String = String.format(Locale.US, "%.3f", ms / 1000.0)
}
