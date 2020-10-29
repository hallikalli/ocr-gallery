package com.hklee.ocrgallery.adapters

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hklee.ocrgallery.base.ListItemClickListener
import com.hklee.ocrgallery.base.OnImageReadyListener
import com.hklee.ocrgallery.data.OcrPhoto
import com.hklee.ocrgallery.databinding.ListItemPhotoBinding
import kotlinx.android.synthetic.main.list_item_photo.view.*


open class GalleryAdapter(
    private val itemClickListener: ListItemClickListener?,
    private val onImageReadyListener: OnImageReadyListener?=null
) :
    PagingDataAdapter<OcrPhoto, GalleryAdapter.GalleryViewHolder>(GalleryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val binding = ListItemPhotoBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return GalleryViewHolder(binding, onImageReadyListener)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        var photo = getItem(position)
        photo?.let {
            holder.bind(it)
            holder.itemView.imageView_photo.setOnClickListener {
                itemClickListener?.onListItemClick(position, it)
            }
        }
    }

    class GalleryViewHolder(
        private val binding: ListItemPhotoBinding,
        private val onImageReadyListener:OnImageReadyListener?
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OcrPhoto) {
            binding.photo = item
            binding.requestListener = object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    onImageReadyListener?.onImageReady(bindingAdapterPosition)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    onImageReadyListener?.onImageReady(bindingAdapterPosition)
                    return false
                }
            }
            binding.executePendingBindings()
        }
    }
}

private class GalleryDiffCallback : DiffUtil.ItemCallback<OcrPhoto>() {
    override fun areItemsTheSame(oldItem: OcrPhoto, newItem: OcrPhoto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OcrPhoto, newItem: OcrPhoto): Boolean {
        return oldItem == newItem
    }
}