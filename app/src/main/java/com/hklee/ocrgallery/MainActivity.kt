package com.hklee.ocrgallery

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.hklee.ocrgallery.viewmodels.TessViewModel
import dagger.hilt.android.AndroidEntryPoint
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
        checkPermissionAndSyncDb()
    }

    private fun checkPermissionAndSyncDb() {
        permissionsRequester.launch()
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        val v: View? = currentFocus
        val ret = super.dispatchTouchEvent(event)
        if (v is EditText || v is SearchView) {
            val w: View? = currentFocus
            val scrcoords = IntArray(2)
            w?.let {
                w.getLocationOnScreen(scrcoords)
                val x: Float = event.rawX + w.left - scrcoords[0]
                val y: Float = event.rawY + w.top - scrcoords[1]
                if (event.action == MotionEvent.ACTION_UP && (x < w.left || x >= w.right || y < w.top || y > w.bottom)) {
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(window.currentFocus!!.windowToken, 0)
                }
            }
        }
        return ret
    }
}