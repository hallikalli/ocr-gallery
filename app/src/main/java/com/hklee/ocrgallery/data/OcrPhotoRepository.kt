package com.hklee.ocrgallery.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.sqlite.db.SimpleSQLiteQuery
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
        var query = makeQuery(splitWord(word))
        var simpleQuery = SimpleSQLiteQuery(query)
        dao.search(simpleQuery)
    }.flow

    fun splitWord(text: String): List<String> {
        var words = mutableListOf<String>()
        if (text.isBlank()) {
            words.add("")
        } else {
            words.addAll(text.split("\\s+".toRegex()).map { word ->
                word.replace("""^[,\.]|[,\.]$""".toRegex(), "")
            }.distinct())
            words.remove("")
        }
        return words
    }

    fun makeQuery(words: List<String>): String {
        var query = "SELECT * FROM OcrPhoto WHERE ocr LIKE "
        return query + words.joinToString(separator = " AND ocr LIKE ") { "'%$it%'" }
    }

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

    suspend fun update(ocrPhoto: OcrPhoto) {
        dao.update(ocrPhoto)
    }

    fun loadOldVersion(version: Int): Array<OcrPhoto> {
        return dao.getOldVersion(version)
    }
}
