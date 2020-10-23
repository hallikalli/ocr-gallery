package com.hklee.ocrgallery.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.hklee.ocrgallery.R
import com.hklee.ocrgallery.data.OcrPhoto


class GalleryAdapter :
    PagingDataAdapter<OcrPhoto, GalleryAdapter.GalleryViewHolder>(GalleryDiffCallback()) {

    lateinit var context: Context
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {
        context = parent.context
        var view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_photo, parent, false)
        return GalleryViewHolder(view);
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        if (getItem(position) != null) {
            Glide.with(context)
                .load(Uri.parse(getItem(position)!!.uri))
                .centerCrop()
                .into(holder.imageView);
        } else {
            Glide.with(context).clear(holder.imageView);
            holder.imageView.setImageDrawable(null);
        }
    }

    class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var imageView: ImageView = view.findViewById(R.id.imageView_photo)
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