package com.hklee.ocrgallery.data

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OcrPhotoDao{
    @Query("SELECT * FROM OcrPhoto")
    fun getAll(): PagingSource<Int, OcrPhoto>

    @Query("SELECT * FROM OcrPhoto WHERE ocr LIKE :word")
    fun search(word:String): PagingSource<Int, OcrPhoto>

    @Insert
    suspend fun insert(ocrPhoto: List<OcrPhoto>)

    @Insert
    suspend fun insert(ocrPhoto: OcrPhoto)

    @Delete
    suspend fun delete(ocrPhoto: OcrPhoto)
}