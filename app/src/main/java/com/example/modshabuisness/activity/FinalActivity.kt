package com.example.modshabuisness.activity

import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.modshabuisness.Constants
import com.example.modshabuisness.R
import com.example.modshabuisness.model.Product
import com.example.modshabuisness.model.User
import com.example.modshabuisness.model.UserProductModel
import com.example.modshabuisness.model.pin
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_cart.*
import kotlinx.android.synthetic.main.activity_final.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

class FinalActivity : RootActivity() {
    lateinit var storedVerificationId:String
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_final)
        val user = getCurrentUserID()

        if (!isNetworkActive(this)) {
            showSnackbar(getString(R.string.network), cart_view)
        }
        else{
            var p = false
            confirm_order.setOnClickListener {
                if(address_location.text.isNullOrEmpty() || final_location.text!!.length!=6){
                    showToast("Please Enter the details properly",this@FinalActivity)
                }else{

                    val auth = FirebaseAuth.getInstance()
                    if(final_otp_layout.visibility==View.GONE){
                        showCircleProgress()
                        FirebaseFirestore.getInstance().collection(Constants.OTHER).document(Constants.PIN).get()
                            .addOnSuccessListener {
                                val x = it.toObject(pin::class.java)!!.pin
                                if (x != null) {
                                    for(i in x){
                                        if(i==final_location.text.toString()){
                                            p = true

                                        }
                                    }
                                }

                                if(!p){
                                    hideProgress()
                                    message.visibility = View.VISIBLE
                                }
                                else{
                                    message.visibility = View.GONE
                                    val position = final_location.text.toString() +"+" +address_location.text.toString()
                                    FirebaseFirestore.getInstance().collection(Constants.USER).document(
                                        getCurrentUserID()!!).update("location", position)


                                    val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                                        }

                                        override fun onVerificationFailed(e: FirebaseException) {
                                            showToast("Too many requests. Please try after some time.", this@FinalActivity)

                                        }

                                        override fun onCodeSent(
                                            verificationId: String,
                                            token: PhoneAuthProvider.ForceResendingToken
                                        ) { storedVerificationId = verificationId
                                        }
                                    }
                                    getUser(this@FinalActivity,object : MyCallbacks{
                                        override fun onCallbackUser(user: User) {
                                            val mobile = user.mobile!!

                                            val options = PhoneAuthOptions.newBuilder(auth)
                                                .setPhoneNumber("+91" + mobile.toString())       // Phone number to verify
                                                .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                                                .setActivity(this@FinalActivity)                 // Activity (for callback binding)
                                                .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                                                .build()
                                            PhoneAuthProvider.verifyPhoneNumber(options)
                                        }

                                        override fun onCallbackProduct(
                                            context: Context,
                                            product: Product
                                        ) {
                                            TODO("Not yet implemented")
                                        }
                                    })


                                    Handler().postDelayed({
                                        hideProgress()
                                        confirm_order.text = "Submit"
                                        final_otp_layout.visibility=View.VISIBLE
                                    },2000)




                                }
                            }
                    }else{
                        if(final_otp.text!!.isEmpty() || final_otp.text!!.length!=6){
                            showToast("Enter OTP Correctly", this@FinalActivity)
                        }
                        else{
                            showCircleProgress()
                            val credential = PhoneAuthProvider.getCredential(
                                storedVerificationId,
                                final_otp.text.toString()
                            )
                            signInWithPhoneAuthCredential(credential, auth,this@FinalActivity)
                            Handler().postDelayed({
                                FirebaseFirestore.getInstance().collection(Constants.USER).document(getCurrentUserID()!!).collection(Constants.HISTORY)
                                    .get().addOnSuccessListener {
                                        val x = it.toObjects(UserProductModel::class.java)
                                        var i = x.size
                                        FirebaseFirestore.getInstance().collection(Constants.USER)
                                            .document(getCurrentUserID()!!)
                                            .collection(Constants.CART)
                                            .get().addOnSuccessListener {
                                                val list =
                                                    it.toObjects(UserProductModel::class.java)
                                                for (k in list) {
                                                    if (i < (x.size + list.size)) {
                                                        FirebaseFirestore.getInstance()
                                                            .collection(Constants.USER)
                                                            .document(getCurrentUserID()!!)
                                                            .collection(Constants.HISTORY)
                                                            .document(i.toString()).set(k)
                                                        i += 1
                                                    } else {
                                                        break
                                                    }
                                                }
                                            }
                                    }
                                showToast("Successfully Ordered",this@FinalActivity)
                                startActivity(Intent(this@FinalActivity,HomeActivity::class.java))
                                hideProgress()
                                finish()
                            }, 2000)
                        }
                    }
                }
            }
        }
    }



    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, auth: FirebaseAuth, context: Context) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    hideProgress()
                    showToast("Incorrect OTP",context)
                }
            }
    }




}
