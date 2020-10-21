package com.hklee.ocrgallery.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [OcrPhoto::class], version = 1)
abstract class OcrPhotoDb : RoomDatabase() {
    abstract fun ocrPhotoDao(): OcrPhotoDao

    companion object {
        @Volatile private var INSTANCE: OcrPhotoDb? = null

        fun getInstance(context: Context): OcrPhotoDb =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context)
                    .also { INSTANCE = it }
            }

        //https://medium.com/androiddevelopers/7-pro-tips-for-room-fbadea4bfbd1#4785
        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext,
                OcrPhotoDb::class.java, DB_NAME)
                .build()
    }
}
const val DB_NAME = "OcrPhoto.db"
