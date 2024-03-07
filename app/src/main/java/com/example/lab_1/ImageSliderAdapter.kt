package com.example.lab_1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ImageSliderAdapter : RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>() {

    private val images = mutableListOf<String>()

    fun addImage(imageUrl: String) {
        images.add(imageUrl)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageSliderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.image_slider_item, parent, false)
        return ImageSliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageSliderViewHolder, position: Int) {
        Glide.with(holder.itemView)
            .load(images[position])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return images.size
    }

    inner class ImageSliderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
    }
}