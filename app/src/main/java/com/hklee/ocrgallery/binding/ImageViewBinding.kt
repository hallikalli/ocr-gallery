package com.hklee.ocrgallery.binding

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener

@BindingAdapter("imageFromUri", "requestListener", requireAll = false)
fun ImageView.bindImageFromUri(
    uri: String?,
    requestListener: RequestListener<Drawable>?
) {
    if (!uri.isNullOrEmpty()) {
        val transition = Glide.with(context)
            .load(Uri.parse(uri))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .dontAnimate()
            .dontTransform()
        requestListener?.let { transition.listener(it) }
        transition.into(this)
    }
}
