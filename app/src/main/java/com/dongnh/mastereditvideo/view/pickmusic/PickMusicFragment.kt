package com.dongnh.mastereditvideo.view.pickmusic

import android.annotation.SuppressLint
import android.app.Dialog
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dongnh.masteredit.enums.NAME_EXIF_MUSIC
import com.dongnh.masteredit.model.MusicModel
import com.dongnh.masteredit.utils.exts.createMusicPath
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.databinding.FragmentPickMusicBinding
import com.dongnh.mastereditvideo.utils.adapter.AdapterMusic
import com.dongnh.mastereditvideo.utils.error.coroutineError
import com.dongnh.mastereditvideo.utils.interfaces.OnMusicActionClick
import com.dongnh.mastereditvideo.utils.interfaces.OnMusicSelectListener
import com.dongnh.mastereditvideo.utils.retrofit.MusicService
import com.dongnh.mastereditvideo.utils.retrofit.createRetrofitService
import com.dongnh.mastereditvideo.utils.retrofit.loadDownloadService
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.*

/**
 * Project : MasterEditVideo
 * Created by DongNH on 12/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class PickMusicFragment: BottomSheetDialogFragment() {

    // Binding
    lateinit var dataBinding: FragmentPickMusicBinding

    // Music player
    private val mediaPlayer: MediaPlayer by lazy {
        MediaPlayer()
    }

    // Music object
    private var musicObject: MusicModel? = null

    // Keep old music player
    private var musicIsPlaying: MusicModel? = null

    // Duration of music
    private var duration = 0

    // Position of music selected
    private var positionOfMusicSelect = -1

    // Position of music player
    private var positionOfMusicPlayer = -1

    // Handle task
    private var handle = Handler(Looper.getMainLooper())

    // Adapter to view
    private val adapterMusic = AdapterMusic()

    companion object {
        // Callback to parent view
        var onMusicSelectListener : OnMusicSelectListener? = null
    }

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

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
        setupFullHeight(sheetContainer)
    }

    /**
     * Set height of dialog with height of screen
     */
    private fun setupFullHeight(bottomSheet: View) {
        val layoutParams = bottomSheet.layoutParams
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT
        bottomSheet.layoutParams = layoutParams
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this@PickMusicFragment.isCancelable = true

        configView()
    }

    private fun configView() {
        // Make it is disable
        this@PickMusicFragment.dataBinding.btnClickDone.isEnabled = false

        // Button back
        this@PickMusicFragment.dataBinding.btnBack.setOnClickListener {
            this.dismiss()
        }

        // Layout manager
        this@PickMusicFragment.dataBinding.viewListMusic.layoutManager = LinearLayoutManager(
            this@PickMusicFragment.requireContext(),
            RecyclerView.VERTICAL,
            false
        )

        // Call back
        adapterMusic.onMusicActionClick = object : OnMusicActionClick {
            override fun onMusicPlay(musicModel: MusicModel, position: Int) {
                if (musicModel.isPlay) {
                    this@PickMusicFragment.mediaPlayer.setVolume(50f, 50f)
                    this@PickMusicFragment.musicIsPlaying = musicModel
                    this@PickMusicFragment.positionOfMusicPlayer = position
                    if (this@PickMusicFragment.mediaPlayer.isPlaying) {
                        this@PickMusicFragment.mediaPlayer.stop()
                        this@PickMusicFragment.mediaPlayer.reset()
                    }
                    try {
                        this@PickMusicFragment.mediaPlayer.reset()
                        this@PickMusicFragment.mediaPlayer.setDataSource(musicModel.urlFile)
                        this@PickMusicFragment.mediaPlayer.prepareAsync()
                        this@PickMusicFragment.mediaPlayer.setOnPreparedListener {
                            it.start()
                            this@PickMusicFragment.duration = it.duration
                            handle.postDelayed(updateSeekBar, 15)
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    this@PickMusicFragment.positionOfMusicPlayer = -1
                    this@PickMusicFragment.mediaPlayer.pause()
                    this@PickMusicFragment.musicIsPlaying = null
                }
            }

            override fun onMusicDownload(musicModel: MusicModel, position: Int) {
                if (musicModel.pathInLocal.isNullOrEmpty()) {
                    val pathOfMusic = createMusicPath(this@PickMusicFragment.requireContext()) + "/" + musicModel.nameMusic + NAME_EXIF_MUSIC
                    Timber.e("onMusicDownload path in local : $pathOfMusic")
                    musicModel.pathInLocal = pathOfMusic
                    if (File(pathOfMusic).exists()) {
                        musicModel.isDownloaded = true
                        Timber.e("Have file, no need download")
                    } else {
                        downloadFileMusic(musicModel, position)
                    }
                } else {
                    musicModel.isDownloaded = true
                }
            }

            override fun onMusicClick(musicModel: MusicModel, position: Int) {
                Timber.e("Music click $musicModel")
                this@PickMusicFragment.dataBinding.btnClickDone.isEnabled = musicModel.isDownloaded
                this@PickMusicFragment.positionOfMusicSelect = position
                if (musicModel.isChoose) {
                    this@PickMusicFragment.musicObject = musicModel
                } else {
                    this@PickMusicFragment.musicObject = null
                }

                this@PickMusicFragment.dataBinding.btnClickDone.isEnabled =
                    musicModel.isDownloaded && musicModel.isChoose
                this@PickMusicFragment.adapterMusic.notifyItemChanged(position)
            }
        }
        this@PickMusicFragment.dataBinding.viewListMusic.adapter = adapterMusic

        // Load list music
        val musicService =
            createRetrofitService<MusicService>("https://raw.githubusercontent.com/dongnh311/mockup-api/")
        // Show dialog
        dataBinding.dialogDownload.visibility = View.VISIBLE
        dataBinding.viewProgress.text = getString(R.string.common_loading)

        // Start load
        CoroutineScope(Dispatchers.Default + coroutineError).launch {
            val result = withContext(Dispatchers.IO) {
                return@withContext musicService.getListMusic()
            }

            lifecycleScope.launch {
                // Hide video loading
                dataBinding.dialogDownload.visibility = View.GONE

                // Setup data to view
                result.result?.data?.let {
                    it.forEach { item ->

                        // Mapping download status
                        val pathOfMusic =
                            createMusicPath(this@PickMusicFragment.requireContext()) + "/" + item.nameMusic + NAME_EXIF_MUSIC
                        if (File(pathOfMusic).exists()) {
                            item.isDownloaded = true
                            item.pathInLocal = pathOfMusic
                        }
                    }
                    adapterMusic.dataList.addAll(it)
                }
                adapterMusic.notifyItemRangeInserted(0, adapterMusic.dataList.size)
            }
        }

        // Button done
        dataBinding.btnClickDone.setOnClickListener {
            this@PickMusicFragment.musicObject?.let { music ->
                onMusicSelectListener?.onMusicChoose(
                    music
                )
                this@PickMusicFragment.dismiss()
            }
        }
    }

    /**
     * Background Runnable thread
     */
    private val updateSeekBar: Runnable = object : Runnable {
        override fun run() {
            val currentDuration = mediaPlayer.currentPosition

            // Calc progress
            val progress = (currentDuration * 1.0 / this@PickMusicFragment.duration) * 100

            // Updating progress bar
            if (this@PickMusicFragment.musicIsPlaying?.progress != progress.toInt()) {
                Timber.e("Progress music player : $progress, total duration : ${this@PickMusicFragment.duration}, current play :$currentDuration ")
                this@PickMusicFragment.musicIsPlaying?.progress = progress.toInt()
                this@PickMusicFragment.adapterMusic.notifyItemChanged(this@PickMusicFragment.positionOfMusicPlayer, progress.toInt())
            }

            // Call this thread again after 15 milliseconds => ~ 1000/60fps
            if (currentDuration < this@PickMusicFragment.duration) {
                handle.postDelayed(this, 15)
            } else {
                this@PickMusicFragment.musicIsPlaying?.isPlay = false
                this@PickMusicFragment.adapterMusic.notifyItemChanged(this@PickMusicFragment.positionOfMusicPlayer, progress.toInt())
            }
        }
    }

    override fun onPause() {
        super.onPause()
        this@PickMusicFragment.mediaPlayer.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        this@PickMusicFragment.handle.removeCallbacksAndMessages(null)
        this@PickMusicFragment.mediaPlayer.stop()
        this@PickMusicFragment.mediaPlayer.release()
    }

    /**
     * Download file
     */
    private fun downloadFileMusic(musicModel: MusicModel, position: Int) {
        // Show dialog download
        dataBinding.dialogDownload.visibility = View.VISIBLE

        // Download
        val downloadService = loadDownloadService()
        CoroutineScope(Dispatchers.IO + coroutineError).launch {
            val response = withContext(Dispatchers.IO) {
                return@withContext downloadService.downloadFileWithDynamicUrlSync(musicModel.urlFile)
            }

            response.body()?.let {
                val result = writeResponseBodyToDisk(it, musicModel)
                if (result) {
                    if (position == this@PickMusicFragment.positionOfMusicSelect) {
                        dataBinding.btnClickDone.isEnabled = true
                    }

                    // Update to adapter
                    musicModel.isDownloaded = true
                    CoroutineScope(Dispatchers.Main).launch {
                        this@PickMusicFragment.adapterMusic.notifyItemChanged(position, Any())
                    }
                } else {
                    Timber.e("Download file is fail")
                    dataBinding.btnClickDone.isEnabled = false
                }

                CoroutineScope(Dispatchers.Main).launch {
                    dataBinding.dialogDownload.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Save file music to local
     */
    @SuppressLint("SetTextI18n")
    private fun writeResponseBodyToDisk(
        body: ResponseBody,
        item: MusicModel
    ): Boolean {
        val outputFile = File(item.pathInLocal.toString())
        if (outputFile.exists()) {
            outputFile.delete()
        }

        // Max byte
        val maxByteOfFile = body.contentLength()
        val fileByteReader = ByteArrayOutputStream()
        var currentByteWrite: Int

        try {
            val outputStream = FileOutputStream(item.pathInLocal)
            try {
                // Write byte to reader
                body.byteStream().use { byte ->
                    byte.copyTo(fileByteReader)
                    currentByteWrite = fileByteReader.size()
                    val percent = currentByteWrite / maxByteOfFile * 100.0
                    CoroutineScope(Dispatchers.Main).launch {
                        dataBinding.viewProgress.text = "Percent $percent %"
                    }
                }

                // Reader save to file
                fileByteReader.use { byteOutPut ->
                    outputStream.use { outputFile ->
                        byteOutPut.writeTo(outputFile)
                    }
                }

                outputStream.flush()
                outputStream.close()

                // Stop show dialog
                Timber.e("Download file is ok")

            } catch (e: IOException) {
                Timber.e(e)
                return false
            } finally {
                outputStream.close()
            }
        } catch (e: IOException) {
            Timber.e(e)
            return false
        }

        return true
    }
}