package com.hklee.ocrgallery.data

import androidx.paging.PagingSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface OcrPhotoDao {
    @Query("SELECT * FROM OcrPhoto")
    fun loadAll(): Array<OcrPhoto>

    @Query("SELECT * FROM OcrPhoto WHERE ocr LIKE '%'||:word||'%'")
    fun search(word: String): PagingSource<Int, OcrPhoto>

    @RawQuery(observedEntities = [OcrPhoto::class])
    fun search(query: SupportSQLiteQuery): PagingSource<Int, OcrPhoto>

    @Query("SELECT EXISTS(SELECT * FROM OcrPhoto WHERE uri = :uri)")
    fun isUriExist(uri: String): Boolean

    @Insert
    suspend fun insert(ocrPhoto: List<OcrPhoto>)

    @Insert
    suspend fun insert(ocrPhoto: OcrPhoto)

    @Delete
    suspend fun delete(ocrPhoto: OcrPhoto)

    @Update
    suspend fun update(ocrPhoto: OcrPhoto)

    @Query("SELECT * FROM OcrPhoto WHERE version < :version")
    fun getOldVersion(version: Int): Array<OcrPhoto>
}