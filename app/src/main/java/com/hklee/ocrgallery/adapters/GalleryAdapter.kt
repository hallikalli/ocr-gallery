package com.hklee.ocrgallery.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.hklee.ocrgallery.R
import com.hklee.ocrgallery.data.OcrPhoto

class GalleryAdapter : PagingDataAdapter<OcrPhoto, GalleryAdapter.GalleryViewHolder>(GalleryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        val imageView = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_photo, parent, false) as ImageView
        return GalleryViewHolder(imageView);
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
      //   val photo = getItem(position)
        //        if (photo != null) {
        //            holder.bind(photo)
        //        }
        //Glide

    }
    class GalleryViewHolder(val imageView: ImageView) : RecyclerView.ViewHolder(imageView)
/*
    class GalleryViewHolder(
        private val binding: ListItemPhotoBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.setClickListener { view ->
                binding.photo?.let { photo ->
                    val uri = Uri.parse(photo.user.attributionUrl)
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    view.context.startActivity(intent)
                }
            }
        }

        fun bind(item: UnsplashPhoto) {
            binding.apply {
                photo = item
                executePendingBindings()
            }
        }
    }*/
} 
private class GalleryDiffCallback : DiffUtil.ItemCallback<OcrPhoto>() {
    override fun areItemsTheSame(oldItem: OcrPhoto, newItem: OcrPhoto): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: OcrPhoto, newItem: OcrPhoto): Boolean {
        return oldItem == newItem
    }
}