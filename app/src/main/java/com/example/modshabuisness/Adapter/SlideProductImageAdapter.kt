package com.example.modshabuisness.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.example.modshabuisness.R
import com.example.modshabuisness.activity.ProductInterface
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.activity_product_interface.view.*
import kotlinx.android.synthetic.main.slider_product_image_layout.view.*

class SlideProductImageAdapter(
    private val list: MutableList<String> = mutableListOf<String>(),
    private val context: Context
    ) : SliderViewAdapter<ProductInterface.ProductImageViewHolder>() {
    override fun getCount(): Int {
        return list.size
    }

    override fun onCreateViewHolder(parent: ViewGroup): ProductInterface.ProductImageViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.slider_product_image_layout,null)
        return ProductInterface.ProductImageViewHolder(view)
    }

    override fun onBindViewHolder(
        viewHolder: ProductInterface.ProductImageViewHolder,
        position: Int
    ) {
        val p = list[position]
        Glide
            .with(context)
            .load(p)
            .centerCrop()
            .into(viewHolder.itemView.product_image_interface)
    }
}