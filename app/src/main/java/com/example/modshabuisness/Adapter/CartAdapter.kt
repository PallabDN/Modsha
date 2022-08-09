package com.example.modshabuisness.Adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.modshabuisness.Constants
import com.example.modshabuisness.R
import com.example.modshabuisness.activity.CartActivity
import com.example.modshabuisness.activity.ProductInterface
import com.example.modshabuisness.model.Product
import com.example.modshabuisness.model.UserProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.singe_items_user.view.*
import kotlinx.android.synthetic.main.single.view.*
import kotlin.properties.Delegates

class CartAdapter(
    private val mutableList: MutableList<UserProductModel>,
    private val context: Context,
    private val view:TextView,
    private val clazz : Class<ProductInterface>
): RecyclerView.Adapter<CartActivity.CartProductViewHolder>() {
    var w = 0
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartActivity.CartProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.singe_items_user,parent,false)
        return CartActivity.CartProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartActivity.CartProductViewHolder, position: Int) {
        holder.itemView.status_of_order.visibility = View.GONE
        val x = mutableList[position]
        val q = x.quantity
        if(x.size==null || x.size.isEmpty()){
            holder.itemView.user_item_size.visibility = View.GONE
        }else{
            holder.itemView.user_item_size.text = x.size.toString()
        }
        var y=0

        FirebaseFirestore.getInstance().collection(Constants.PRODUCT).document(x.productId!!).get()
            .addOnSuccessListener {
                Log.d(Constants.EG,"Fetching Successful")
                val item = it.toObject(Product::class.java)!!
                holder.itemView.user_item_quantity.text = "Quantity: "+item.quantity!![q]
                holder.itemView.user_item_name.text = item.name.toString()
                holder.itemView.user_item_price.text = "₹"+item.priceD!![q]
                y= item.priceD!![q].toInt() + item.charge!![q].toInt()
                holder.itemView.shipping_charge.text = "Charge: ₹"+item.charge!![q]
                Glide
                    .with(context)
                    .load(item.image!![0])
                    .centerCrop()
                    .into(holder.itemView.user_item_image)
                w += y
                view.text = "Total: "+w.toString()
            }
            .addOnFailureListener {
                Log.d(Constants.EG,"Fetching Failed")
            }

        holder.itemView.remove_user_item.setOnClickListener {
            FirebaseFirestore.getInstance().collection(Constants.USER)
                .document(FirebaseAuth.getInstance().currentUser!!.uid)
                .collection(Constants.CART).document(x.productId)
                .delete()
                .addOnSuccessListener {
                    Log.d(Constants.EG,"Successfully Removed")
                }
        }
        holder.itemView.setOnClickListener {
            val intent = Intent(context,clazz)
            intent.putExtra(Constants.PRODUCT_ID,x.productId)
            ContextCompat.startActivity(context,intent,null)

        }

    }

    override fun getItemCount(): Int {
        return mutableList.size
    }

    fun getTotal():Int{
        return w
    }


}