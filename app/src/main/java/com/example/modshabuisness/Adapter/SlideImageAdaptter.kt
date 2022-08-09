package com.example.modshabuisness.Adapter

import com.example.modshabuisness.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.modshabuisness.activity.HomeActivity
import com.example.modshabuisness.model.ImageModel
import com.example.modshabuisness.model.Product
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.single.view.*
import kotlinx.android.synthetic.main.slider_image_layout.view.*


class SlideImageAdapter(
    private val mutableList: MutableList<String> = mutableListOf(),
    private val context: Context
)
    : SliderViewAdapter<HomeActivity.ImageViewHolder>() {
    override fun getCount(): Int {
        return mutableList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup): HomeActivity.ImageViewHolder? {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.slider_image_layout,null)
        return HomeActivity.ImageViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: HomeActivity.ImageViewHolder, position: Int) {
        val p = mutableList[position]
        Glide
            .with(context)
            .load(p)
            .centerCrop()
            .into(viewHolder.itemView.myimage)
    }


}