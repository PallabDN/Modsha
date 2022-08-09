package com.example.modshabuisness.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.annotation.RequiresApi
import com.example.modshabuisness.R
import com.example.modshabuisness.model.Product
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_user_profile.*

class UserProfile : RootActivity() {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        if(!isNetworkActive(this@UserProfile)){
            showSnackbar(getString(R.string.network),userProfile)
        }
        else{
            getUser(this,object : MyCallbacks{
                override fun onCallbackUser(user: com.example.modshabuisness.model.User) {
                    userName.text = user.name.toString()
                    userMobile.text = user.mobile.toString()
                }

                override fun onCallbackProduct(context: Context, product: Product) {

                }

            })





            order_history.setOnClickListener{
                val intent = Intent(this@UserProfile,OrderHistoryActivity::class.java)
                startActivity(intent)
            }

            user_cart.setOnClickListener {
                startActivity(Intent(this@UserProfile,CartActivity::class.java))
            }

            user_logout.setOnClickListener{
                showCircleProgress()
                Firebase.auth.signOut()
                Handler().postDelayed({
                    hideProgress()
                    finish()
                },1500)

            }
            order_whishlist.setOnClickListener {
                startActivity((Intent(this@UserProfile,StaredActivity::class.java)))
            }
            profile_back.setOnClickListener {
                onBackPressed()
            }
        }



    }
}

