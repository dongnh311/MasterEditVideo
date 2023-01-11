package com.dongnh.mastereditvideo.utils.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import androidx.core.content.ContextCompat
import androidx.core.database.getLongOrNull
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.dongnh.mastereditvideo.R
import com.dongnh.mastereditvideo.const.*
import com.dongnh.mastereditvideo.databinding.ItemMediaBinding
import com.dongnh.masteredit.model.MediaObject
import com.dongnh.mastereditvideo.utils.interfaces.OnImageClick
import timber.log.Timber

/**
 * Project : MasterEditVideo
 * Created by DongNH on 05/01/2023.
 * Email : hoaidongit5@gmail.com or hoaidongit5@dnkinno.com.
 * Phone : +84397199197.
 */
class AdapterMedia : BaseAdapter() {

    val arrayImage: ArrayList<MediaObject> = arrayListOf()

    var onImageClick: OnImageClick? = null

    override fun getCount(): Int {
        return arrayImage.size
    }

    override fun getItem(position: Int): MediaObject? {
        return if (position > arrayImage.size) {
            null
        } else {
            arrayImage[position]
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: ItemMediaBinding?
        val view: View?
        if (convertView == null) {
            binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent?.context),
                R.layout.item_media,
                parent,
                false
            )
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as ItemMediaBinding
            view = convertView
        }

        binding?.let { ViewHandle(it, arrayImage[position]).bindingData() }
        binding?.imageViewToPick?.setOnClickListener {
            // Select multi
            if (arrayImage[position].isChoose) {
                arrayImage[position].isChoose = false
                this@AdapterMedia.onImageClick?.imageClear(arrayImage[position])
            } else {
                arrayImage[position].isChoose = true
                this@AdapterMedia.onImageClick?.imageClick(arrayImage[position])
            }

            notifyDataSetChanged()
        }

        return view
    }

    class ViewHandle(private val binding: ItemMediaBinding, private val imageObject: MediaObject) {
        fun bindingData() {
            // image
            if (binding.imageViewToPick.drawable == null || (binding.viewName.tag as String?) != null && binding.viewName.tag != imageObject.mediaPath) {
                Glide.with(binding.imageViewToPick.context).clear(binding.imageViewToPick)
                Glide.with(binding.imageViewToPick.context).load(imageObject.mediaPath)
                    .thumbnail(0.01f)
                    .apply(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .skipMemoryCache(false)
                    )
                    .into(binding.imageViewToPick)
                binding.viewName.tag = imageObject.mediaPath
            }

            if (imageObject.mediaType.contains(MEDIA_TYPE_VIDEO)) {
                binding.btnPlayVideo.visibility = View.VISIBLE
                binding.viewName.text =
                    convertDurationToString(
                        imageObject.mediaDuration
                    )
            } else {
                binding.btnPlayVideo.visibility = View.GONE
                binding.viewName.text = imageObject.mediaName
            }

            if (imageObject.isChoose) {
                showIsSelect(binding)
            } else {
                showIsNotSelect(binding)
            }
        }

        // Change view to select state
        private fun showIsSelect(binding: ItemMediaBinding) {
            binding.cornerMedia.background = ContextCompat.getDrawable(
                binding.imageViewToPick.context,
                R.drawable.bg_media_corner_sletect
            )
            binding.viewChecked.visibility = View.VISIBLE
            binding.viewChecked.bringToFront()
        }

        // Disable status select
        private fun showIsNotSelect(binding: ItemMediaBinding) {
            binding.cornerMedia.background = null
            binding.viewChecked.visibility = View.GONE
        }

        // Show duration
        private fun convertDurationToString(duration: Long): String {
            val durationNeed = (duration / 1000.0).toInt()
            val stringView: String
            if (durationNeed.toDouble() == 60.0) {
                stringView = "01:00"
            } else if (durationNeed.toDouble() < 10) {
                stringView = "00:0${durationNeed}"
            } else {
                stringView = if (durationNeed >= 60) {
                    val minute = durationNeed / 60
                    val second = durationNeed - minute * 60
                    if (second < 10) {
                        "0$minute:0${second}"
                    } else {
                        "0$minute:${second}"
                    }
                } else {
                    "00:${durationNeed}"
                }
            }
            return stringView
        }
    }

    /**
     * Getting All Images Path.
     *
     * @param activity the activity
     */
    @Suppress("DEPRECATION")
    fun getAllShownImagesPath(
        activity: Activity,
        modeSelectMedia: Int
    ) {
        val EXTERNAL_STRING = "external"
        val FILE = "file://"
        val SORT_ORDER = " DESC"
        val DURATION = "duration"
        val columns = arrayOf(
            MediaStore.Files.FileColumns._ID,
            MediaStore.Files.FileColumns.DATA,
            MediaStore.Files.FileColumns.DISPLAY_NAME,
            MediaStore.Files.FileColumns.DATE_ADDED,
            MediaStore.Files.FileColumns.MEDIA_TYPE,
            MediaStore.Files.FileColumns.MIME_TYPE,
            MediaStore.Files.FileColumns.TITLE,
            DURATION
        )

        var selection = ""
        if (modeSelectMedia == MEDIA_IMAGE) {
            selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)
        } else if (modeSelectMedia == MEDIA_VIDEO) {
            selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                    + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
        }

        val queryUri = MediaStore.Files.getContentUri(EXTERNAL_STRING)
        MediaScannerConnection.scanFile(
            activity, arrayOf(EXTERNAL_STRING), null
        ) { _, _ ->
            Timber.e("Reload media")
        }
        activity.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse(
                    FILE
                            + Environment.getExternalStorageDirectory()
                )
            )
        )
        val imageCursor: Cursor = activity.managedQuery(
            queryUri,
            columns,
            selection,
            null,  // Selection args (none).
            MediaStore.Files.FileColumns.DATE_ADDED + SORT_ORDER // Sort order.
        )
        val imageColumnIndex = imageCursor.getColumnIndex(MediaStore.Files.FileColumns._ID)
        val count = imageCursor.count
        for (i in 0 until count) {
            imageCursor.moveToPosition(i)
            val id = imageCursor.getInt(imageColumnIndex)
            val dataColumnIndex = imageCursor.getColumnIndex(MediaStore.Files.FileColumns.DATA)
            val typeString = imageCursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)
            val indexName = imageCursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)
            val name = imageCursor.getString(indexName)
            val stringType = imageCursor.getString(typeString)
            val durationString = imageCursor.getColumnIndex(DURATION)
            val duration = imageCursor.getLongOrNull(durationString)
            val path = imageCursor.getString(dataColumnIndex)
            val imageObject = MediaObject()
            imageObject.mediaPath = path
            imageObject.mediaId = id
            imageObject.mediaType = stringType
            imageObject.mediaName = name
            if (duration != null) {
                imageObject.mediaDuration = duration
            }

            if (duration != null && stringType.contains(MEDIA_TYPE_VIDEO)) {
                // Only get video with duration < 60s
                if (duration in 1..599999) {
                    imageObject.endTime = imageObject.mediaDuration
                    imageObject.endAt = imageObject.mediaDuration
                    imageObject.mediaType = MEDIA_TYPE_VIDEO
                    arrayImage.add(imageObject)
                }
            } else {
                if (stringType.contains(MEDIA_TYPE_IMAGE)) {
                    imageObject.mediaDuration = 2000
                    imageObject.endTime = 2000
                    imageObject.endAt = 2000
                    imageObject.mediaType = MEDIA_TYPE_IMAGE
                    arrayImage.add(imageObject)
                }
            }
        }

        this@AdapterMedia.notifyDataSetChanged()
    }
}