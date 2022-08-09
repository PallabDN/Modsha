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
import com.example.modshabuisness.activity.HomeActivity
import com.example.modshabuisness.activity.OrderHistoryActivity
import com.example.modshabuisness.activity.ProductInterface
import com.example.modshabuisness.model.Product
import com.example.modshabuisness.model.User
import com.example.modshabuisness.model.UserProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.type.Color
import kotlinx.android.synthetic.main.activity_user_profile.*
import kotlinx.android.synthetic.main.singe_items_user.view.*
import kotlinx.android.synthetic.main.single.view.*
import java.util.*

class OrderHistoryAdapter(
    private val mutableList: MutableList<UserProductModel>,
    private val context: Context,
    private val clazz : Class<ProductInterface>
): RecyclerView.Adapter<OrderHistoryActivity.OrderHistoryViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderHistoryActivity.OrderHistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.singe_items_user,parent,false)
        return OrderHistoryActivity.OrderHistoryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: OrderHistoryActivity.OrderHistoryViewHolder,
        position: Int
    ) {

        val x = mutableList[position]
        val q = x.quantity

        if(x.status==0){
            holder.itemView.status_of_order.text = "Status: Precessing"
            holder.itemView.status_of_order.setTextColor(android.graphics.Color.parseColor("#984063"))
        }
        else if(x.status==1){
            holder.itemView.status_of_order.text = "Status: Succeed"
        }
        else{
            holder.itemView.status_of_order.text = "Status: Failed"
            holder.itemView.status_of_order.setTextColor(android.graphics.Color.parseColor("#DD4470"))
        }

        if(x.size==null || x.size.isEmpty()){
            holder.itemView.user_item_size.visibility = View.GONE
        }else{
            holder.itemView.user_item_size.text = x.size.toString()
        }

        FirebaseFirestore.getInstance().collection(Constants.PRODUCT).document(x.productId!!).get()
            .addOnSuccessListener {
                Log.d(Constants.EG,"Fetching Successful")
                val item = it.toObject(Product::class.java)!!
                holder.itemView.user_item_quantity.text = "Quantity: "+item.quantity!![q]
                holder.itemView.user_item_name.text = item.name.toString()
                holder.itemView.user_item_price.text = "₹"+item.priceD!![q]

                holder.itemView.shipping_charge.text = "Charge: ₹"+item.charge!![q]
                Glide
                    .with(context)
                    .load(item.image!![0])
                    .centerCrop()
                    .into(holder.itemView.user_item_image)
            }
            .addOnFailureListener {
                Log.d(Constants.EG,"Fetching Failed")
            }

        holder.itemView.remove_user_item.visibility = View.GONE
        holder.itemView.setOnClickListener {
            val intent = Intent(context,clazz)
            intent.putExtra(Constants.PRODUCT_ID,x.productId)
            ContextCompat.startActivity(context,intent,null)
        }
    }

    override fun getItemCount(): Int {
        return mutableList.size
    }


}









