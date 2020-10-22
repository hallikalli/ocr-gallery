package com.hklee.ocrgallery.utilites

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import com.hklee.ocrgallery.R
import timber.log.Timber
import java.io.*


class TesseractOcr(val context: Context) {

    private val ASSETS_TESS = "tessdata"
    private val LANG = "kor+eng"
    private var trainedDataPath = context.cacheDir.absolutePath + "/tessdata/"

    private val tess = TessBaseAPI().apply {
        updateTrainedData()
        init(trainedDataPath, LANG)
    }

    // 문자 인식 및 결과 출력
    fun toOcrText(bitmap: Bitmap?): String? {
        tess.setImage(bitmap)
        return tess.utF8Text
    }

    fun destroy() {
        tess.end()
    }


    // tessdata에 파일이 없으면 복제
    private fun updateTrainedData() {
        if(prepareDirectory())
            updateTrainedDataFile()
        else
            Timber.e("Directory not created")
    }

    private fun prepareDirectory(): Boolean {
        val dir = File(trainedDataPath)
        if(dir.exists()) return true
        return dir.mkdir()
    }

    private fun updateTrainedDataFile() {
        var trainedDataList = context.assets.list(ASSETS_TESS)
        if (trainedDataList == null) {
            Timber.e("No data in assets/tessdata")
            return
        }
        for (fileName in trainedDataList) {
            val path = trainedDataPath+fileName
            if (!File(path).exists()) continue
            copyFile("$ASSETS_TESS/$fileName", path)
        }
    }

    private fun copyFile(src: String, dest: String) {
        try {
            context.assets.open(src).use { input ->
                FileOutputStream(dest).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}