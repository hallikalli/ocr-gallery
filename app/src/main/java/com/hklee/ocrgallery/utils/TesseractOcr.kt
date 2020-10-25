package com.hklee.ocrgallery.utils

import android.content.Context
import android.graphics.Bitmap
import com.googlecode.tesseract.android.TessBaseAPI
import timber.log.Timber
import java.io.*


class TesseractOcr(val context: Context) {

    private val TRANINEDDATA_FOLDER = "tessdata"
    private val LANG = "kor+eng"
    private var TESSERACT_PATH = context.cacheDir.absolutePath + "/tesseract/"

    //파일 구조
    // /data/user/0/~/cache/tesseract/tessdata/eng.traineddata

    private val tess = TessBaseAPI().apply {
        updateTrainedData()
        init(TESSERACT_PATH, LANG) //init시 tessdata 라는 하위폴더가 가 있어야함
    }

    // 문자 인식 및 결과 출력
    fun toOcrText(bitmap: Bitmap): String {
        tess.setImage(bitmap)
        var result = tess.utF8Text.replace("\n", " ").replace("\t"," ");
        Timber.tag("result ocr ").d(result)
        return result
    }

    fun destroy() {
        tess.end()
    }


    // tessdata에 파일이 없으면 복제
    private fun updateTrainedData() {
        if(prepareDirectory())
            updateTrainedDataFile()
        else
            Timber.e("Directory($TESSERACT_PATH) not created ")
    }

    private fun prepareDirectory(): Boolean {
        val dir = File( TESSERACT_PATH+TRANINEDDATA_FOLDER)
        if(dir.exists()) return true
        return dir.mkdirs()
    }

    private fun updateTrainedDataFile() {
        var assetsList = context.assets.list(TRANINEDDATA_FOLDER)
        if (assetsList == null) {
            Timber.e("No data in assets/tessdata")
            return
        }
        for (fileName in assetsList) {
            val tessFileName = TESSERACT_PATH+TRANINEDDATA_FOLDER+"/"+fileName
            if (!File(tessFileName).exists())
                copyFile("$TRANINEDDATA_FOLDER/$fileName", tessFileName)
        }
    }

    private fun copyFile(src: String, dest: String) {
        try {
            context.assets.open(src).use { input ->
                FileOutputStream(dest,false).use { output ->
                    input.copyTo(output)
                }
            }
        } catch (e: IOException) {
            Timber.e(e.printStackTrace().toString())
        }
    }
}