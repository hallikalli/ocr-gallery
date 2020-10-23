package com.hklee.ocrgallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.provider.MediaStore
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hklee.ocrgallery.data.OcrPhotoDao
import com.hklee.ocrgallery.data.OcrPhotoDao_Impl
import com.hklee.ocrgallery.data.OcrPhotoRepository
import com.hklee.ocrgallery.utilites.TesseractOcr
import com.hklee.ocrgallery.viewmodels.TessViewModel
import junit.framework.TestCase.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.mock
import org.mockito.junit.MockitoJUnitRunner
import timber.log.Timber
import java.sql.Date

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.hklee.OcrGallery", appContext.packageName)
    }

    lateinit var appContext: Context
    lateinit var tesseract: TesseractOcr

    @Before
    fun initContxt() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
        tesseract = TesseractOcr(appContext)
    }


    @Test
    fun tesseract() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        TesseractOcr(appContext)
    }

    @Test
    fun accessMediaStore() {
        var list = mutableListOf<Uri>()

        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.DATE_TAKEN,
        )
        val selection = "UPPER(${MediaStore.Images.Media.DISPLAY_NAME}) LIKE UPPER('%Screenshot%')"
        val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
        val cursor = appContext.contentResolver.query(
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
                list.add(contentUri)

                Timber.tag("accessMediaStore()").d(
                    "id: $id, display_name: $displayName, date_taken: " +
                            "$dateTaken, content_uri: $contentUri"
                )
            }
        }
    }

}