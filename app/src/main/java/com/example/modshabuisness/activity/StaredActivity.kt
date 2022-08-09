package com.example.modshabuisness.activity

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modshabuisness.Adapter.CartAdapter
import com.example.modshabuisness.Adapter.StaredAdapter
import com.example.modshabuisness.R
import com.example.modshabuisness.model.Product
import com.example.modshabuisness.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.activity_stared.*

class StaredActivity : RootActivity() {

    lateinit var wish_listAdapter:StaredAdapter

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stared)



        if(!isNetworkActive(this@StaredActivity)){
            showSnackbar(getString(R.string.network),stared)
        }
        else{
            showCircleProgress()
            val fireStoreList = findViewById<RecyclerView>(R.id.wishlist_user_recyclerView)

            var wishlist = mutableListOf<String>()
            getUser(
                this@StaredActivity,
                object : MyCallbacks{
                    override fun onCallbackUser(user: User) {
                        wishlist = user.wishlist as MutableList<String>
                        if(wishlist.isNotEmpty()){
                            wish_listAdapter = StaredAdapter(wishlist,this@StaredActivity,ProductInterface::class.java)
                            fireStoreList.adapter = wish_listAdapter
                            fireStoreList.setHasFixedSize(true)
                            fireStoreList.layoutManager = GridLayoutManager(this@StaredActivity, 2)
                        }
                        else{
                            fireStoreList.visibility = View.GONE
                            wishlist_image.visibility = View.VISIBLE
                        }
                        hideProgress()
                    }

                    override fun onCallbackProduct(context: Context, product: Product) {
                        TODO("Not yet implemented")
                    }

                }
            )

            wishlist_back.setOnClickListener {
                onBackPressed()
            }


        }


    }

    class ProductViewHolderStared(view: View):RecyclerView.ViewHolder(view){

    }

}
