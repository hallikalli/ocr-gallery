package com.hklee.ocrgallery.viewmodels

import androidx.hilt.lifecycle.ViewModelInject
import com.hklee.ocrgallery.data.OcrPhotoRepository
import com.hklee.musicplayer.base.BaseViewModel

class GalleryViewModel @ViewModelInject constructor(
    private val photoRepository: OcrPhotoRepository
) : BaseViewModel() {
    fun searchPhoto(word: String) =  photoRepository.search(word)
}
