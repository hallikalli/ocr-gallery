package com.hklee.ocrgallery.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.hklee.ocrgallery.R
import com.hklee.ocrgallery.data.OcrPhoto
import com.hklee.ocrgallery.databinding.ListItemFullImageBinding
import timber.log.Timber


open class ImagePagerAdapter(private val onImageReadyListener: OnImageReadyListener)  :
    PagingDataAdapter<OcrPhoto, ImagePagerAdapter.ImagePagerViewHolder>(ImagePagerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePagerViewHolder {
        val binding = ListItemFullImageBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ImagePagerViewHolder(binding, onImageReadyListener)
    }

    override fun onBindViewHolder(holder: ImagePagerViewHolder, position: Int) {
        var photo = getItem(position)
        photo?.let {
            holder.bind(it)
        }
    }

    class ImagePagerViewHolder(
        private val binding: ListItemFullImageBinding,
        private val onImageReadyListener: OnImageReadyListener
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OcrPhoto) {
            binding.position = bindingAdapterPosition
            binding.photo = item
            binding.requestListener = object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Timber.d("onLoadFailed")
                    onImageReadyListener.onImageReady(bindingAdapterPosition)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Timber.d("onResourceReady")
                    onImageReadyListener.onImageReady(bindingAdapterPosition)
                    return false
                }
            }
            binding.executePendingBindings()
        }
    }
    interface OnImageReadyListener {

        fun onImageReady(position: Int)
    }
}


private class ImagePagerDiffCallback : DiffUtil.ItemCallback<OcrPhoto>() {
    override fun areItemsTheSame(oldItem: OcrPhoto, newItem: OcrPhoto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OcrPhoto, newItem: OcrPhoto): Boolean {
        return oldItem == newItem
    }
}