package com.hklee.ocrgallery.di


import android.content.Context
import com.hklee.ocrgallery.utilites.TesseractOcr
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Singleton

@InstallIn(ApplicationComponent::class)
@Module
class UtilModule {
    @Singleton
    @Provides
    fun provideTesseract(@ApplicationContext context: Context): TesseractOcr {
        return TesseractOcr(context)
    }
}
