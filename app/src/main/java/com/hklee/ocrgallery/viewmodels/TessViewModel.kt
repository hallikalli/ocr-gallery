package com.hklee.ocrgallery.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope


import androidx.paging.Pager

import androidx.paging.PagingConfig

import androidx.paging.PagingData

import androidx.paging.*


import com.hklee.musicplayer.base.BaseViewModel
import com.hklee.ocrgallery.data.OcrPhoto
import com.hklee.ocrgallery.data.OcrPhotoRepository
import com.hklee.ocrgallery.utils.TesseractOcr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import java.sql.Date


class TessViewModel @ViewModelInject constructor(
    private val photoRepository: OcrPhotoRepository,
    private val tesseract: TesseractOcr
) : BaseViewModel() {

    var searchFlow: Flow<PagingData<OcrPhoto>>? = null
    private val _loading = MutableLiveData<Boolean>().apply {
        value = false
    }
    val loading: LiveData<Boolean> get() = _loading
    val currentPosition = MutableLiveData<Int>()
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN,
    )
    private val selection =
        "UPPER(${MediaStore.Images.Media.DISPLAY_NAME}) LIKE UPPER('%Screenshot%')"
    private val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"

    fun searchPhoto(word: String): Flow<PagingData<OcrPhoto>> {
        searchFlow = photoRepository.search(word).cachedIn(viewModelScope)
        return photoRepository.search(word)
    }

    fun sync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            _loading.postValue(true)
            var list = loadScreenShots(context)
            //스크린샷 추가
            for (uri in list) {
                if (photoRepository.isUriExist(uri.toString()))
                    continue
                val text = convertBitmap(
                    context.contentResolver,
                    Uri.parse(uri)
                )?.let { tesseract.toOcrText(it) }
                var ocrPhoto = OcrPhoto(uri, text ?: "")
                photoRepository.insert(ocrPhoto)
            }
            //스크린샷 삭제
            for (ocrPhoto in photoRepository.loadAll()) {
                if (!list.contains(ocrPhoto.uri)) {
                    photoRepository.remove(ocrPhoto)
                }
            }
            _loading.postValue(false)
        }
    }

    fun convertBitmap(contentResolver: ContentResolver, uri: Uri): Bitmap? {
        var bitmap: Bitmap? = null
        bitmap = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(contentResolver, uri))
        } else {
            MediaStore.Images.Media.getBitmap(contentResolver, uri)
        }
        return bitmap
    }

    //searchScreenshotInMedia
    fun loadScreenShots(context: Context): List<String> {
        var list = mutableListOf<String>()
        val cursor = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )
        cursor?.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dateTakenColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
            val displayNameColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val dateTaken = Date(cursor.getLong(dateTakenColumn))
                val displayName = cursor.getString(displayNameColumn)
                val contentUri = Uri.withAppendedPath(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id.toString()
                )
                list.add(contentUri.toString())
                Timber.tag("accessMediaStore()").d(
                    "id: $id, display_name: $displayName, date_taken: " +
                            "$dateTaken, content_uri: $contentUri"
                )
            }
        }
        return list
    }

}
