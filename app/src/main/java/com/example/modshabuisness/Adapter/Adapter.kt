package com.example.modshabuisness.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modshabuisness.Constants
import com.example.modshabuisness.R
import com.example.modshabuisness.activity.HomeActivity
import com.example.modshabuisness.activity.ProductInterface
import com.example.modshabuisness.model.Product
import kotlinx.android.synthetic.main.single.view.*
import java.util.*

class Adapter (private val mutableList: MutableList<Product> = mutableListOf(),
               private val context: Context,
               private val clazz : Class<ProductInterface>
               ): RecyclerView.Adapter<HomeActivity.ProductViewHolder>(),Filterable {
    var filteredProduct : MutableList<Product> = mutableList

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeActivity.ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single,parent,false)
        return HomeActivity.ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: HomeActivity.ProductViewHolder, position: Int) {
        val p = filteredProduct[position]
        holder.itemView.product_name.text = p.name.toString()
        holder.itemView.priceDiscount.text = "₹" + p.priceD!![0]
        if(p.priceA==null){

            holder.itemView.priceActual.visibility = View.GONE
        }else{

            holder.itemView.priceActual.text = "₹" + p.priceA!![0]
        }

        if(p.quantity!![0]=="0"){
            holder.itemView.outOfStock_single.visibility = View.VISIBLE
        }
        else{
            holder.itemView.outOfStock_single.visibility = View.GONE
        }
        val image = p.image as MutableList
        Glide
            .with(context)
            .load(image[0])
            .centerCrop()
            .into(holder.itemView.product_image)

        holder.itemView.setOnClickListener {
            val intent = Intent(context,clazz)
            intent.putExtra(Constants.PRODUCT_ID,filteredProduct[position].id)
            ContextCompat.startActivity(context,intent,null)
        }


    }

    override fun getItemCount(): Int {
        return filteredProduct.size
    }


    override fun getFilter(): Filter {
        return object :Filter(){
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                var filterList : MutableList<Product> = mutableListOf()
                if(constraint.toString().isEmpty() || constraint!!.isEmpty()){
                    filterList = mutableList
                }
                else {
                    filterList.clear()
                    for(i in mutableList){
                        if (i.name!!.toString().toLowerCase(Locale.ROOT).contains(constraint.toString().toLowerCase(Locale.ROOT))) {
                            filterList.add(i)
                        }
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = filterList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

                filteredProduct = results!!.values as MutableList<Product>
                notifyDataSetChanged()
            }

        }
    }


}