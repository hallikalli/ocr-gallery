package com.hklee.ocrgallery.viewmodels

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.hklee.musicplayer.base.BaseViewModel
import com.hklee.ocrgallery.data.OcrPhoto
import com.hklee.ocrgallery.data.OcrPhotoRepository
import com.hklee.ocrgallery.utilites.TesseractOcr
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.sql.Date


class TessViewModel @ViewModelInject constructor(
    private val photoRepository: OcrPhotoRepository,
    private val tesseract: TesseractOcr
) : BaseViewModel() {

    private val _syncProgress = MutableLiveData<Pair<Int, Int>>()
    val syncProgress: LiveData<Pair<Int, Int>> get() = _syncProgress
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATE_TAKEN,
    )
    private val selection =
        "UPPER(${MediaStore.Images.Media.DISPLAY_NAME}) LIKE UPPER('%Screenshot%')"
    private val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"


    fun searchPhoto(word: String) =  photoRepository.search(word)


    fun sync(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            var list = loadScreenShots(context)
            //스크린샷 추가
            for (uri in list) {
                if (photoRepository.isUriExist(uri.toString()))
                    continue
                val text = convertBitmap(context.contentResolver, Uri.parse(uri))?.let { tesseract.toOcrText(it) }
                var ocrPhoto = OcrPhoto(uri, text ?: "")
                photoRepository.insert(ocrPhoto)
            }
            //스크린샷 삭제
            for (ocrPhoto in photoRepository.loadAll()) {
                if (!list.contains(ocrPhoto.uri)){
                    photoRepository.remove(ocrPhoto)
                }
            }
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
