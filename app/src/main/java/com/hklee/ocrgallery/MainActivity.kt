package com.hklee.ocrgallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.OnLifecycleEvent
import com.googlecode.tesseract.android.TessBaseAPI
import com.hklee.ocrgallery.viewmodels.TessViewModel
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val tessViewModel by viewModels<TessViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tessViewModel.sync(this)
    }
}