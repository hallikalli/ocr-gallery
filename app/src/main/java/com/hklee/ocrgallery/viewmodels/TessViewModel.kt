package com.hklee.ocrgallery.viewmodels

import android.content.Intent
import android.provider.MediaStore
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.hklee.musicplayer.base.BaseViewModel
import com.hklee.ocrgallery.data.OcrPhotoRepository
import com.hklee.ocrgallery.utilites.TesseractOcr
import java.io.File
import java.io.IOException


class TessViewModel @ViewModelInject constructor(
    private val photoRepository: OcrPhotoRepository,
    private val tesseract: TesseractOcr
) : BaseViewModel(){

    private val _syncProgress = MutableLiveData<Pair<Int, Int>>()
    val syncProgress: LiveData<Pair<Int, Int>> get() = _syncProgress

    fun sync(){
        //todo sync with db
    }

}
