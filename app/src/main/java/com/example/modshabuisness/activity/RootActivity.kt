package com.example.modshabuisness.activity

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.modshabuisness.Constants
import com.example.modshabuisness.R
import com.example.modshabuisness.model.Product
import com.example.modshabuisness.model.User
import com.example.modshabuisness.model.UserMobile
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.muddzdev.styleabletoast.StyleableToast
import kotlinx.android.synthetic.main.sign_up.*
import java.util.concurrent.TimeUnit


open class RootActivity : AppCompatActivity() {
    private lateinit var mProgressDialog: Dialog
    @RequiresApi(Build.VERSION_CODES.M)
    fun isNetworkActive(context: Context): Boolean{
        val connectivityManager:ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if(capabilities!=null){
            if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)){
                return true
            }else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)){
                return true
            }
            else if(capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)){
                return true
            }
        }
        return false
    }



    @RequiresApi(Build.VERSION_CODES.M)
    fun Login(context: Context){
        lateinit var storedVerificationId:String
        val layoutInflater=LayoutInflater.from(context)
        val alertDialogBuilder = AlertDialog.Builder(context).create()
        val promptView = layoutInflater.inflate(R.layout.log_in, null)
        val mobile = promptView.findViewById<EditText>(R.id.user_input_mobile_login)
        val mobile_layout = promptView.findViewById<TextInputLayout>(R.id.user_input_mobile_layout_login)
        val otp = promptView.findViewById<EditText>(R.id.user_otp_login)
        val otp_layout = promptView.findViewById<TextInputLayout>(R.id.user_otp_layout_login)
        val submit = promptView.findViewById<Button>(R.id.submit_login)
        val signup_login = promptView.findViewById<TextView>(R.id.signup_login)
        val hane_not_account = promptView.findViewById<LinearLayout>(R.id.hane_not_account)
        if(!isNetworkActive(context)){
            showSnackbar(getString(R.string.network), user_sign_up)
        }
        else{
            val auth = FirebaseAuth.getInstance()

            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                }

                override fun onVerificationFailed(e: FirebaseException) {
                    showToast("Too many requests. Please try after some time.", context)
                    Handler().postDelayed({
                        hideProgress()
                        alertDialogBuilder.dismiss()
                    },3000)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) { storedVerificationId = verificationId
                    hideProgress()
                }
            }

            signup_login.setOnClickListener{
                Signup(context)
                alertDialogBuilder.dismiss()
            }

            submit.setOnClickListener {
                if(otp_layout.visibility==View.VISIBLE){

                    if(otp.text!!.isEmpty() || otp.text!!.length!=6){
                        showToast("Enter OTP Correctly", context)
                    }
                    else{
                        showProgress("Logging in")
                        val credential = PhoneAuthProvider.getCredential(
                            storedVerificationId,
                            otp.text.toString()
                        )
                        signInWithPhoneAuthCredential(credential, auth,context)
                        Handler().postDelayed({
                            hideProgress()
                            alertDialogBuilder.dismiss()
                        }, 3000)
                    }
                }
                else{
                    if(mobile.text!!.isEmpty() || mobile.text!!.length !=10){
                        showToast("Please Enter Mobile Number Properly", context)
                    }
                    else{
                        showCircleProgress()
                        var x = false
                        var users_mobile = mutableListOf<String>()
                        val db = FirebaseFirestore.getInstance().collection(Constants.USER)
                        FirebaseFirestore.getInstance().collection(Constants.OTHER).document(Constants.USER_IDENTITY).get()
                            .addOnSuccessListener {
                                users_mobile = it.toObject(UserMobile::class.java)!!.user_mobile as MutableList<String>
                                for(i in users_mobile){
                                    if(i==mobile.text.toString()){
                                        x = true
                                    }
                                }
                                if(!x){
                                    hideProgress()
                                    showToast("You don't have an account",context)
                                }
                                else{
                                    val options = PhoneAuthOptions.newBuilder(auth)
                                        .setPhoneNumber("+91" + mobile.text.toString())       // Phone number to verify
                                        .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(this)                 // Activity (for callback binding)
                                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                                        .build()
                                    PhoneAuthProvider.verifyPhoneNumber(options)

                                    hane_not_account.visibility = View.GONE
                                    otp_layout.visibility = View.VISIBLE
                                    mobile_layout.visibility = View.GONE
                                    submit.text = getString(R.string.user_submit)

                                }
                            }



                    }

                }

            }
        }
        alertDialogBuilder.setView(promptView)
        alertDialogBuilder.show()
    }








    @RequiresApi(Build.VERSION_CODES.M)
    fun Signup(context: Context){
        lateinit var storedVerificationId:String
        lateinit var auth:FirebaseAuth
        lateinit var name:String
        lateinit var mobile:String
        lateinit var email:String
        val layoutInflater = LayoutInflater.from(context)
        val alertDialogBuilder = AlertDialog.Builder(context).create()
        val promptView = layoutInflater.inflate(R.layout.sign_up, null)
        val user_input_name = promptView.findViewById<EditText>(R.id.user_input_name)
        val user_input_name_layout = promptView.findViewById<TextInputLayout>(R.id.user_input_name_layout)
        val user_input_mobile = promptView.findViewById<EditText>(R.id.user_input_mobile)
        val user_input_mobile_layout = promptView.findViewById<TextInputLayout>(R.id.user_input_mobile_layout)
        val user_input_email = promptView.findViewById<EditText>(R.id.user_input_email)
        val user_input_email_layout = promptView.findViewById<TextInputLayout>(R.id.user_input_email_layout)
        val user_otp = promptView.findViewById<EditText>(R.id.user_otp)
        val user_otp_layout = promptView.findViewById<TextInputLayout>(R.id.user_otp_layout)
        val submit = promptView.findViewById<Button>(R.id.submit)

        if(!isNetworkActive(context)){
            showSnackbar(getString(R.string.network), user_sign_up)
        }
        else{
            auth = FirebaseAuth.getInstance()



            val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {

                }

                override fun onVerificationFailed(e: FirebaseException) {
                    showToast("Too many requests. Please try after some time.", context)
                    Handler().postDelayed({
                        hideProgress()
                        alertDialogBuilder.dismiss()
                    },3000)
                }

                override fun onCodeSent(
                    verificationId: String,
                    token: PhoneAuthProvider.ForceResendingToken
                ) { storedVerificationId = verificationId
                    hideProgress()
                }
            }




            submit.setOnClickListener {

                if(user_otp_layout.visibility==View.VISIBLE){
                    if(user_otp.text!!.isEmpty() || user_otp.text!!.length!=6){
                        showToast("Enter OTP Correctly", context)
                    }
                    else{
                        showProgress("Signing Up")
                        val credential = PhoneAuthProvider.getCredential(
                            storedVerificationId,
                            user_otp.text.toString()
                        )
                        signInWithPhoneAuthCredential(credential, auth,context)
                        Handler().postDelayed({

                            val currentUser = FirebaseAuth.getInstance().currentUser

                            if (currentUser != null) {
                                val user = User(name = name, mobile = mobile, email = email,uid = currentUser.uid)
                                val db = FirebaseFirestore.getInstance().collection(Constants.USER)
                                var users_mobile = mutableListOf<String>()
                                FirebaseFirestore.getInstance().collection(Constants.OTHER).document(Constants.USER_IDENTITY).get()
                                    .addOnSuccessListener {
                                        users_mobile = it.toObject(UserMobile::class.java)!!.user_mobile as MutableList<String>
                                        users_mobile.add(mobile)
                                        FirebaseFirestore.getInstance().collection(Constants.OTHER).document(Constants.USER_IDENTITY).set(UserMobile(users_mobile))
                                    }
                                db.document(currentUser.uid)
                                    .set(user, SetOptions.merge())
                                    .addOnSuccessListener {
                                        hideProgress()
                                        alertDialogBuilder.dismiss()
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(Constants.EG, e.toString())
                                        hideProgress()
                                        alertDialogBuilder.dismiss()
                                    }

                            }

                        }, 5000)
                    }
                }
                else{
                    if(user_input_name.text!!.isEmpty() || user_input_name.text!!.length < 4){
                        showToast("Please Enter Your Name Properly", context)
                    }
                    else if(user_input_mobile.text!!.isEmpty() || user_input_mobile.text!!.length !=10){
                        showToast("Please Enter Valid Mobile Number", context)
                    }
                    else{

                        var x:Boolean=false
                        name = user_input_name.text.toString()
                        mobile = user_input_mobile.text.toString()
                        email = user_input_email.text.toString()
                        val db = FirebaseFirestore.getInstance().collection(Constants.USER)
                        var users_mobile = mutableListOf<String>()
                        FirebaseFirestore.getInstance().collection(Constants.OTHER).document(Constants.USER_IDENTITY).get()
                            .addOnSuccessListener {
                                users_mobile = it.toObject(UserMobile::class.java)!!.user_mobile as MutableList<String>
                                for(i in users_mobile){
                                    if(i==mobile){
                                        x = true
                                    }
                                }
                                if(x){
                                    showToast("You have an Account",context)
                                }
                                else{
                                    showCircleProgress()
                                    val options = PhoneAuthOptions.newBuilder(auth)
                                        .setPhoneNumber("+91" + user_input_mobile.text.toString())       // Phone number to verify
                                        .setTimeout(10L, TimeUnit.SECONDS) // Timeout and unit
                                        .setActivity(this)                 // Activity (for callback binding)
                                        .setCallbacks(callbacks)          // OnVerificationStateChangedCallbacks
                                        .build()
                                    PhoneAuthProvider.verifyPhoneNumber(options)

                                    user_otp_layout.visibility = View.VISIBLE
                                    user_input_email_layout.visibility = View.GONE
                                    user_input_name_layout.visibility = View.GONE
                                    user_input_mobile_layout.visibility = View.GONE
                                    submit.text = getString(R.string.user_submit)
                                }

                            }


                    }
                }
            }


        }
        alertDialogBuilder.setView(promptView)
        alertDialogBuilder.show()
    }












    @RequiresApi(Build.VERSION_CODES.M)
    fun showSnackbar(s: String, view: View){
        val snackbar = Snackbar.make(view, s, Snackbar.LENGTH_INDEFINITE).setAction("Retry") {
            startActivity(intent)
            finish()
        }
            .setActionTextColor(Color.WHITE)


        val snakbarView = snackbar.view
        snakbarView.setBackgroundColor(Color.RED);

        val textView = snakbarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)

        textView.setTextColor(Color.WHITE)
        textView.textSize = 10f
        snackbar.show()
    }





    fun showToast(s: String, context: Context){
       StyleableToast.makeText(context,s,Toast.LENGTH_LONG,R.style.mytoast).show()
    }






    fun getCurrentUserID(): String? {
        return FirebaseAuth.getInstance().currentUser!!.uid
    }








    fun showProgress(s: String){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.linear_progress)
        mProgressDialog.findViewById<TextView>(R.id.tittle).text = s
        mProgressDialog.show()
    }






    fun showCircleProgress(){
        mProgressDialog = Dialog(this)
        mProgressDialog.setContentView(R.layout.circular_progress)
        mProgressDialog.show()
    }







    fun hideProgress(){
        mProgressDialog.dismiss()
    }






    @RequiresApi(Build.VERSION_CODES.M)
    fun getUser(context: Context, myCallbacks: MyCallbacks){
        if(getCurrentUserID()!=null){
            FirebaseFirestore.getInstance().collection(Constants.USER).document(getCurrentUserID()!!).get()
                .addOnSuccessListener {
                    val user:User = it.toObject(User::class.java)!!
                    myCallbacks.onCallbackUser(user)

                }
                .addOnFailureListener {
                    showToast(it.toString(), context)
                }
        }
        else{
            Signup(context)
        }
    }






    fun getProduct(context: Context, productId: String, myCallbacks: MyCallbacks){
        FirebaseFirestore.getInstance().collection(Constants.PRODUCT).document(productId).get()
            .addOnSuccessListener {
                val product = it.toObject(Product::class.java)!!
                myCallbacks.onCallbackProduct(context, product)
            }
            .addOnFailureListener { e->
                showToast(e.toString(), context)
            }
    }








    interface MyCallbacks {
        fun onCallbackUser(user: User)
        fun onCallbackProduct(context: Context, product: Product)
    }






   private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential, auth: FirebaseAuth,context: Context) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (!task.isSuccessful) {
                    hideProgress()
                    showToast("Incorrect OTP",context)
                }
            }
    }



}
