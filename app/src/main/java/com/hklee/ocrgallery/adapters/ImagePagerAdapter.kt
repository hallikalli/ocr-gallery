package com.hklee.ocrgallery.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hklee.ocrgallery.R
import com.hklee.ocrgallery.data.OcrPhoto
import com.hklee.ocrgallery.databinding.ListItemFullImageBinding
import com.hklee.ocrgallery.databinding.ListItemPhotoBinding


class ImagePagerAdapter :
    PagingDataAdapter<OcrPhoto, ImagePagerAdapter.ImagePagerViewHolder>(ImagePagerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagePagerViewHolder {
        return ImagePagerViewHolder(
            ListItemFullImageBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImagePagerViewHolder, position: Int) {
        var photo = getItem(position)
        photo?.let {
            holder.bind(it)
        }
    }

    class ImagePagerViewHolder(
        private val binding: ListItemFullImageBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: OcrPhoto) {
            binding.photo = item
            binding.executePendingBindings()
        }
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