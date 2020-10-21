package com.hklee.ocrgallery.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OcrPhotoRepository @Inject constructor(
    private val dao: OcrPhotoDao
) {
    fun search(word: String) = Pager(
        PagingConfig(
            pageSize = 60,
            enablePlaceholders = true,
            maxSize = 200
        )
    ) {
        dao.search(word)
    }.flow

    suspend fun insert(ocrPhoto: OcrPhoto) {
        dao.insert(ocrPhoto)
    }

    suspend fun remove(ocrPhoto: OcrPhoto) {
        dao.delete(ocrPhoto)
    }


}
