package com.hklee.ocrgallery

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.Images.Media.query
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.hklee.ocrgallery.utilites.TesseractOcr

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import timber.log.Timber
import java.sql.Date
import java.util.concurrent.TimeUnit

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

    @Test
    fun tesseract() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        TesseractOcr(appContext)
    }

    @Test
    fun accessMediaStore() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
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
                Timber.tag("accessMediaStore()").d(
                    "id: $id, display_name: $displayName, date_taken: " +
                            "$dateTaken, content_uri: $contentUri"
                )
            }
        }
    }

}