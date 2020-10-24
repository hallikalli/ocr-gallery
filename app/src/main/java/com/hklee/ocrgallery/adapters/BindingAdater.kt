package com.hklee.ocrgallery.adapters

import android.net.Uri
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("imageFromUri")
fun ImageView.bindImageFromUri(uri: String?) {
    if (!uri.isNullOrBlank()) {
        Glide.with(context)
            .load(Uri.parse(uri))
            .centerCrop()
            .into(this);
    } else {
        Glide.with(context).clear(this);
        this.setImageDrawable(null);
    }
}