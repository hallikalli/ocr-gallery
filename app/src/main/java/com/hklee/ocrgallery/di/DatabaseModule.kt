package com.hklee.ocrgallery.di

import android.content.Context
import com.hklee.ocrgallery.data.OcrPhotoDb
import com.hklee.ocrgallery.data.OcrPhotoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
class DatabaseModule {
    @Singleton
    @Provides
    fun provideOcrPhotoDatabase(@ApplicationContext context: Context): OcrPhotoDb {
        return OcrPhotoDb.getInstance(context)
    }

    @Provides
    fun provideOcrPhotoDao(appDatabase: OcrPhotoDb): OcrPhotoDao {
        return appDatabase.ocrPhotoDao()
    }

}
