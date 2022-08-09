package com.example.modshabuisness.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modshabuisness.Adapter.CartAdapter
import com.example.modshabuisness.Constants
import com.example.modshabuisness.R
import com.example.modshabuisness.model.UserProductModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_stared.*

class CartActivity : RootActivity() {

    var cartProductList = mutableListOf<UserProductModel>()
    lateinit var cartAdapter:CartAdapter
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)


        if (!isNetworkActive(this)) {
            showSnackbar(getString(R.string.network), cart_view)
        }

        else {
            showCircleProgress()
            val fireStoreList = findViewById<RecyclerView>(R.id.recycle_product_user_cart)
            val db = FirebaseFirestore.getInstance().collection(Constants.USER).document(getCurrentUserID()!!).collection(Constants.CART)

            db.addSnapshotListener { snapshot, e->
                if(e!=null){
                    return@addSnapshotListener
                }
                cartProductList = snapshot!!.toObjects(UserProductModel::class.java)
                if(cartProductList.isEmpty()){
                    user_empty_cart.visibility = View.VISIBLE
                    user_cart_bottom.visibility =View.GONE
                    recycle_product_user_cart.visibility = View.GONE
                }
                else{
                    cartAdapter = CartAdapter(cartProductList,this@CartActivity,total_product_price,ProductInterface::class.java)
                    fireStoreList.adapter = cartAdapter
                    fireStoreList.setHasFixedSize(true)
                    fireStoreList.layoutManager = LinearLayoutManager(this@CartActivity)
                }
                hideProgress()
            }


            user_place_order.setOnClickListener {
                val intent = Intent(this@CartActivity,FinalActivity::class.java)

                startActivity(intent)

            }
            cart_back.setOnClickListener {
                onBackPressed()
            }
        }




    }

    class CartProductViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
}