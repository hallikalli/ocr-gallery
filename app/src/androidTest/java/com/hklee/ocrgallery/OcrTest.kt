package com.hklee.ocrgallery

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import com.hklee.ocrgallery.data.OcrPhotoDao
import com.hklee.ocrgallery.data.OcrPhotoRepository
import com.hklee.ocrgallery.utils.TesseractOcr
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class OcrTest {

    @Mock
    lateinit var repo: OcrPhotoRepository // 유즈케이스@Mock
    @Mock
    lateinit var ocrPhotoDao: OcrPhotoDao // 유즈케이스
    @Mock lateinit var appContext: Context
    @Mock lateinit var tesseract: TesseractOcr

    @Before
    fun initContxt() {
        appContext = InstrumentationRegistry.getInstrumentation().targetContext
//        tesseract = TesseractOcr(appContext)
    }

    @Test
    fun imageToText() {
//      assertTrue(repo!=null)
//        var repo = mock(OcrPhotoRepository::class.java)

//        val viewModel = TessViewModel(OcrPhotoRepository(ocrPhotoDao), tesseract)
//
//        for (uri in viewModel.getScreenshotList(appContext)) {
//            val text = viewModel.toBitmap(appContext.contentResolver, uri)?.let {
//                tesseract.toOcrText(it)
//            }
//            Timber.tag("timage to text").d(text?:"text is null")
//
//        }
//        viewModel.sync(appContext)
//        TessViewModel.sync(appContext)
    }
}