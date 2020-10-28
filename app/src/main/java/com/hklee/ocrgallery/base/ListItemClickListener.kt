package com.hklee.ocrgallery.base

import android.view.View

interface ListItemClickListener {
    fun onListItemClick(position: Int, view: View? = null)
}