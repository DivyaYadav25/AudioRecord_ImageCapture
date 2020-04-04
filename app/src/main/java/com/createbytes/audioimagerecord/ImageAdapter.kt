package com.createbytes.audioimagerecord

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_image.view.*

class ImageAdapter (private var clickedImage: List<ClickedImage?>?) :
    RecyclerView.Adapter<ImageAdapter.ImagesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagesViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImagesViewHolder(itemView)
    }

    fun setImageData(clickedImageList: List<ClickedImage?>?) {
        clickedImage = clickedImageList
        notifyItemInserted(clickedImage!!.size-1)
    }

    override fun getItemCount(): Int {
        return clickedImage?.size ?: 0
    }

    override fun onBindViewHolder(_holder: ImagesViewHolder, position: Int) {
        _holder.image.setImageURI(clickedImage?.get(position)?.uri)
    }

    class ImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image : ImageView = itemView.image
    }
}


class ClickedImage(
    var uri:Uri? = null
)
