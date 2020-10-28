package com.hklee.ocrgallery.data

import androidx.paging.DataSource
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.liveData
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class OcrPhotoRepository @Inject constructor(
    private val dao: OcrPhotoDao
) {
    fun search(word: String) = Pager(
        PagingConfig(
            pageSize = 60,
            enablePlaceholders = true
        )
    ) {
        dao.search(word)
    }.flow

    suspend fun loadAll(): Array<OcrPhoto> {
        return dao.loadAll()
    }

    suspend fun isUriExist(uri: String): Boolean {
        return dao.isUriExist(uri)
    }

    suspend fun insert(ocrPhoto: OcrPhoto) {
        dao.insert(ocrPhoto)
    }

    suspend fun remove(ocrPhoto: OcrPhoto) {
        dao.delete(ocrPhoto)
    }


}
