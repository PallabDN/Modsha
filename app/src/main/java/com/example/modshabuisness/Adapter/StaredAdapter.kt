package com.example.modshabuisness.Adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modshabuisness.Constants
import com.example.modshabuisness.R
import com.example.modshabuisness.activity.ProductInterface
import com.example.modshabuisness.activity.StaredActivity
import com.example.modshabuisness.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.singe_items_user.view.*
import kotlinx.android.synthetic.main.single.view.*

class StaredAdapter(
    private val mutableList: MutableList<String>,
    private val context: Context,
    private val clazz : Class<ProductInterface>
): RecyclerView.Adapter<StaredActivity.ProductViewHolderStared>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StaredActivity.ProductViewHolderStared {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single,parent,false)
        return StaredActivity.ProductViewHolderStared(view)
    }

    override fun onBindViewHolder(holder: StaredActivity.ProductViewHolderStared, position: Int) {

        FirebaseFirestore.getInstance().collection(Constants.PRODUCT).document(mutableList[position]).get()
            .addOnSuccessListener {
                val p = it.toObject(Product::class.java)!!
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

                Glide
                    .with(context)
                    .load(p.image!![0])
                    .centerCrop()
                    .into(holder.itemView.product_image)
                holder.itemView.setOnClickListener {
                    val intent = Intent(context,clazz)
                    intent.putExtra(Constants.PRODUCT_ID,p.id)
                    ContextCompat.startActivity(context,intent,null)

                }
            }
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }


}