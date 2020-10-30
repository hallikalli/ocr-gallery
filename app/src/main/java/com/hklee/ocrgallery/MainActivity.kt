package com.hklee.ocrgallery

import android.Manifest
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.hklee.ocrgallery.viewmodels.TessViewModel
import dagger.hilt.android.AndroidEntryPoint
import permissions.dispatcher.*
import permissions.dispatcher.ktx.PermissionsRequester
import permissions.dispatcher.ktx.constructPermissionsRequest

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val tessViewModel by viewModels<TessViewModel>()
    private val permissionsRequester: PermissionsRequester = constructPermissionsRequest(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_MEDIA_LOCATION,
    ) {
        tessViewModel.sync(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()
        askPermissionAndSync()
    }

    private fun askPermissionAndSync() {
        permissionsRequester.launch()
    }

}