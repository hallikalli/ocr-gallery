package com.hklee.ocrgallery.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hklee.ocrgallery.base.ListItemClickListener
import com.hklee.ocrgallery.data.OcrPhoto
import com.hklee.ocrgallery.databinding.ListItemPhotoBinding
import kotlinx.android.synthetic.main.list_item_photo.view.*
import timber.log.Timber


open class GalleryAdapter(private val itemClickListener: ListItemClickListener?) :
    PagingDataAdapter<OcrPhoto, GalleryAdapter.GalleryViewHolder>(GalleryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        return GalleryViewHolder(
            ListItemPhotoBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
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
        private val binding: ListItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OcrPhoto) {
            binding.photo = item
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