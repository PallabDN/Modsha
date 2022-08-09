package com.example.modshabuisness.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.example.modshabuisness.Adapter.Adapter
import com.example.modshabuisness.Adapter.SlideProductImageAdapter
import com.example.modshabuisness.Constants
import com.example.modshabuisness.R
import com.example.modshabuisness.model.Product
import com.example.modshabuisness.model.User
import com.example.modshabuisness.model.UserProductModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.activity_product_interface.*
import java.util.*

class ProductInterface : RootActivity() {
    var pos = 0
    var quantity_product: String? = null
    var size_product: String? = null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_interface)

        val string = intent.getStringExtra(Constants.PRODUCT_ID)
        var p:Boolean = false

        if(!isNetworkActive(this)){
            showSnackbar(getString(R.string.network),item_view)
        }
        else{
            showCircleProgress()

            if(FirebaseAuth.getInstance().currentUser!=null){
                getUser(this@ProductInterface,object :MyCallbacks{
                    override fun onCallbackUser(user: User) {
                        val x:MutableList<String>? = user.wishlist as MutableList<String>?
                        if (x != null) {
                            for(i in x){
                                if(i==string){
                                    wish.setImageResource(R.drawable.star_selected)
                                    p=true
                                }
                            }
                        }
                    }

                    override fun onCallbackProduct(context: Context, product: Product) {
                        TODO("Not yet implemented")
                    }

                })
            }


            getProduct(this@ProductInterface,string!!,object : MyCallbacks{
                override fun onCallbackUser(user: User) {

                }

                override fun onCallbackProduct(context: Context, product: Product) {
                    val slider_adapter = SlideProductImageAdapter(product.image as MutableList<String>,context)
                    slider_product_image.setSliderAdapter(slider_adapter)

                    name.text = product.name.toString()

                    description.text = product.description.toString()



                    if(product.quantity!![0].toInt()==0){
                        productInterface_quantity.text = getString(R.string.out_of_stock)
                        productInterface_quantity.setTextColor(getColor(R.color.snackbar))
                        spinner_product_quantity.visibility = View.GONE

                    }
                    else{
                        val quantity = product.quantity as MutableList
                        val spinner = findViewById<Spinner>(R.id.spinner_product_quantity)
                        val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, quantity)
                        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter

                        spinner.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                pos = position
                                quantity_product = parent.getItemAtPosition(position).toString()
                                price.text = "₹"+ product.priceD!![position]
                                if(product.priceA==null){
                                    price1.visibility = View.GONE
                                }else{
                                    price1.text = "₹" + product.priceA!![position]
                                }
                                charges.text = "Shipping Charge: ₹"+product.charge!![position]
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                pos = 0
                                quantity_product = parent!!.getItemAtPosition(0).toString()
                                price.text = "₹"+ product.priceD!![pos]
                                if(product.priceA==null){
                                    price1.visibility = View.GONE
                                }else{
                                    price1.text = "₹" + product.priceA!![pos]
                                }
                                charges.text = "Shipping Charge: ₹"+product.charge!![0]
                            }
                        }
                    }



                    if(product.size==null){
                        size.visibility = View.GONE
                    }
                    else{
                        val sizz = product.size as MutableList
                        val spinner = findViewById<Spinner>(R.id.spinner_product_size)
                        val adapter = ArrayAdapter<String>(context, R.layout.spinner_item, sizz)
                        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
                        spinner.adapter = adapter

                        spinner.onItemSelectedListener = object :
                            AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>,
                                view: View,
                                position: Int,
                                id: Long
                            ) {
                                size_product = parent.getItemAtPosition(position).toString()
                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {}
                        }
                    }


                    if(product.tag != null && product.tag!!.isNotEmpty()){
                        val tag_list = product.tag as MutableList
                        val relatedProductList = mutableListOf<Product>()
                        for(j in tag_list){
                            FirebaseFirestore.getInstance().collection(Constants.PRODUCT)
                                .addSnapshotListener { value, error ->
                                    if(error!=null){
                                        return@addSnapshotListener
                                    }
                                    val p_list = value!!.toObjects(Product::class.java) as MutableList
                                    for(i in p_list){
                                        if (i.name!!.toString().toLowerCase(Locale.ROOT).contains(j.toLowerCase(Locale.ROOT))){
                                            relatedProductList.add(i)
                                        }
                                    }
                                    if(relatedProductList.isEmpty()){
                                        related_products.visibility = View.GONE
                                        related.visibility = View.GONE
                                    }else{
                                        related_products.adapter = Adapter(relatedProductList,this@ProductInterface,ProductInterface::class.java)
                                        related_products.setHasFixedSize(true)
                                        related_products.layoutManager = GridLayoutManager(this@ProductInterface,2)
                                    }

                                }
                        }

                    }
                    else{
                        related_products.visibility = View.GONE
                        related.visibility = View.GONE
                    }



                }


            })
            hideProgress()




            cart.setOnClickListener {
                if(FirebaseAuth.getInstance().currentUser!=null){
                    startActivity(Intent(this@ProductInterface,CartActivity::class.java))
                }
                else{
                    Login(this@ProductInterface)
                }
            }

            order_now.setOnClickListener{
                if(FirebaseAuth.getInstance().currentUser!=null){
                    if(quantity_product!=null){
                        val model = UserProductModel(string,pos,size_product)
                        FirebaseFirestore.getInstance().collection(Constants.USER).document(getCurrentUserID()!!).collection(Constants.CART).document(string!!).set(model)
                            .addOnSuccessListener {
                                Log.d(Constants.EG,"Add to Cart Successful")
                            }
                            .addOnFailureListener {
                                Log.d(Constants.EG,"Add to Cart Failed")
                            }
                        startActivity(Intent(this@ProductInterface,CartActivity::class.java))
                    }
                    else{
                        showToast(getString(R.string.out_of_stock),this@ProductInterface)
                    }
                }
                else{
                    Login(this@ProductInterface)
                }
            }

            order_more.setOnClickListener {
                if(FirebaseAuth.getInstance().currentUser!=null){
                    if(quantity_product!=null){
                        val model = UserProductModel(string,pos,size_product)
                        FirebaseFirestore.getInstance().collection(Constants.USER).document(getCurrentUserID()!!)
                            .collection(Constants.CART).document(string).set(model)
                            .addOnSuccessListener {
                                Log.d(Constants.EG,"Add to Cart Successful")
                            }
                            .addOnFailureListener {
                                Log.d(Constants.EG,"Add to Cart Failed")
                            }
                        showToast("Added to you Cart",this@ProductInterface)
                    }
                    else{
                        showToast(getString(R.string.out_of_stock),this@ProductInterface)
                    }
                }
                else{
                    Login(this@ProductInterface)
                }
            }
            wish.setOnClickListener {

                if(FirebaseAuth.getInstance().currentUser!=null){
                    showCircleProgress()
                        getUser(this@ProductInterface,object :MyCallbacks{
                            override fun onCallbackUser(user: User) {

                                val wishlist = user.wishlist
                                val list = wishlist as MutableList<String>?
                                if(p){
                                    if(list!=null){
                                        for(i in wishlist!!){
                                            if(i==string){
                                                list.remove(i)
                                                wish.setImageResource(R.drawable.star_unselected)
                                                p=false
                                            }
                                        }
                                    }
                                }
                                else{
                                    if(list!=null){
                                        list.add(string)
                                        wish.setImageResource(R.drawable.star_selected)
                                        p=true
                                    }
                                }
                                FirebaseFirestore.getInstance().collection(Constants.USER).document(getCurrentUserID()!!).update(
                                    "wishlist",
                                    list,
                                )


                            }

                            override fun onCallbackProduct(context: Context, product: Product) {
                                TODO("Not yet implemented")
                            }

                        })
                    Handler().postDelayed({hideProgress()},1000)

                }
                else{
                    Login(this@ProductInterface)
                }

            }

            product_interface_back.setOnClickListener {
                onBackPressed()
            }


        }
    }

    class ProductImageViewHolder(view: View): SliderViewAdapter.ViewHolder(view){

    }
}
