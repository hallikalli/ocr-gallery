package com.hklee.ocrgallery.binding

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.squareup.picasso.Picasso
import timber.log.Timber

/*fun ImageView.bindImageFromUri(uri: String?) {
    if (!uri.isNullOrBlank()) {
        Picasso.get()
            .load(Uri.parse(uri))
            .into(this)
    } else {

        this.setImageDrawable(null);
    }
}*/

@BindingAdapter("imageFromUri", "requestListener", requireAll = false)
fun ImageView.bindImageFromUri(
    uri: String?,
    requestListener: RequestListener<Drawable>?
) {
    Timber.d("requestListener : $requestListener")
    if (!uri.isNullOrEmpty()) {
//        val transition = Glide.with(context)
//            .load(Uri.parse(uri))
//            .into(this)
//            .transition(DrawableTransitionOptions())
//            .dontAnimate()
//            .dontTransform()
//        requestListener?.let { transition.listener(it) }
//        transition.into(this)
        val transition = Glide.with(context)
            .load(Uri.parse(uri))
            .centerCrop()
        requestListener?.let { transition.listener(it) }

        transition.into(this)
        Timber.d("Glide start")
    }
}
