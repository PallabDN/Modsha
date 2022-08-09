package com.example.modshabuisness.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.SearchView
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modshabuisness.Adapter.Adapter
import com.example.modshabuisness.Adapter.SlideImageAdapter
import com.example.modshabuisness.Adapter.StaredAdapter
import com.example.modshabuisness.Constants
import com.example.modshabuisness.R
import com.example.modshabuisness.model.*
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.smarteist.autoimageslider.SliderView
import com.smarteist.autoimageslider.SliderViewAdapter
import kotlinx.android.synthetic.main.activity_home.*
import java.util.*


class HomeActivity : RootActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var adapter:Adapter
    var mHandler = Handler()

    var productList = mutableListOf<Product>()
    var imageList = mutableListOf<String>()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        setSupportActionBar(home_toolbar)



        if(!isNetworkActive(this)){
            showSnackbar(getString(R.string.network), home_view)
        }

        else{
            home_toolbar.setNavigationOnClickListener {
                home_drawer.openDrawer(GravityCompat.START)
            }
            navigation_view.setNavigationItemSelectedListener(this)



            showCircleProgress()
            header()


            val trendingList = findViewById<RecyclerView>(R.id.trending_product)
            FirebaseFirestore.getInstance().collection(Constants.OTHER).document(Constants.TRENDING).get()
                .addOnSuccessListener {
                    val q = it.toObject(TrendingProduct::class.java)!!
                    val list = q.trending as MutableList
                    if(list.isNotEmpty()){
                        val adapter = StaredAdapter(list,this@HomeActivity,ProductInterface::class.java)
                        trendingList.adapter = adapter
                        trendingList.setHasFixedSize(true)
                        trendingList.layoutManager = LinearLayoutManager(this@HomeActivity,LinearLayoutManager.HORIZONTAL,false)

                    } else{
                        other_divider.visibility = View.GONE
                        trending.visibility = View.GONE
                        trendingList.visibility = View.GONE
                    }
                }


            val sliderView = findViewById<SliderView>(R.id.slider_image)
            val fireStoreList = findViewById<RecyclerView>(R.id.recycle_product)
            val db = FirebaseFirestore.getInstance().collection(Constants.PRODUCT)
            db.addSnapshotListener { snapshot, e->
                if(e!=null){
                    return@addSnapshotListener
                }
                productList = snapshot!!.toObjects(Product::class.java)
                adapter = Adapter(productList, this@HomeActivity, ProductInterface::class.java)
                fireStoreList.adapter = adapter
                fireStoreList.setHasFixedSize(true)
                fireStoreList.layoutManager = GridLayoutManager(this@HomeActivity, 2)
                hideProgress()
            }

            FirebaseFirestore.getInstance().collection(Constants.OTHER).document(Constants.ADS).get()
                .addOnSuccessListener {
                    imageList = it.toObject(ImageModel::class.java)!!.image_list as MutableList<String>

                    if(imageList.isEmpty()){
                        sliderView.visibility = View.GONE
                    }else{
                        val slide_adapter  = SlideImageAdapter(imageList, this@HomeActivity)
                        sliderView.setSliderAdapter(slide_adapter)
                        sliderView.scrollTimeInSec = 5
                        sliderView.isAutoCycle = true
                        sliderView.startAutoCycle()
                    }
                }

            cart_home.setOnClickListener {
                if(FirebaseAuth.getInstance().currentUser!=null){
                    startActivity(Intent(this@HomeActivity, CartActivity::class.java))

                }
                else{
                    Login(this@HomeActivity)
                }
            }
        }



        home_view.setOnRefreshListener {
            startActivity(intent)
            finish()
            home_view.isRefreshing = false
        }



    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflate: MenuInflater = menuInflater
        inflate.inflate(R.menu.app_bar, menu)
        val menuItem = menu!!.findItem(R.id.search_item)
        val searchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                slider_image.visibility = View.GONE
                trending_product.visibility = View.GONE
                trending.visibility = View.GONE
                adapter.filter.filter(newText.toString())
                return false
            }
        })
        return true
    }



    override fun onBackPressed() {
        if(home_drawer.isDrawerOpen(GravityCompat.START)){
            home_drawer.closeDrawer(GravityCompat.START)
        }
    }











    class ProductViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }

    class ImageViewHolder(view: View): SliderViewAdapter.ViewHolder(view){

    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onNavigationItemSelected(p0: MenuItem): Boolean {
        when(p0.itemId){
            R.id.drawer_account -> {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    Login(this@HomeActivity)
                } else {
                    startActivity(Intent(this@HomeActivity, UserProfile::class.java))
                }
                home_drawer.closeDrawer(GravityCompat.START)
            }
            R.id.drawer_cart -> {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    Login(this@HomeActivity)
                } else {
                    startActivity(Intent(this@HomeActivity, CartActivity::class.java))
                }
                home_drawer.closeDrawer(GravityCompat.START)
            }
            R.id.drawer_history ->{
                if (FirebaseAuth.getInstance().currentUser == null) {
                    Login(this@HomeActivity)
                } else {
                    startActivity(Intent(this@HomeActivity, OrderHistoryActivity::class.java))
                }
                home_drawer.closeDrawer(GravityCompat.START)
            }
            R.id.drawer_star_item -> {
                if (FirebaseAuth.getInstance().currentUser == null) {
                    Login(this@HomeActivity)
                } else {
                    startActivity(Intent(this@HomeActivity, StaredActivity::class.java))
                }
                home_drawer.closeDrawer(GravityCompat.START)
            }

            R.id.drawer_log_in -> {
                Login(this@HomeActivity)
            }

            R.id.drawer_log_out -> {
                if (FirebaseAuth.getInstance().currentUser != null) {
                    showCircleProgress()
                    Handler().postDelayed({
                        FirebaseAuth.getInstance().signOut()
                        showToast("Successfully Logged Out",this@HomeActivity)
                        startActivity(intent)
                        finish()
                    },1500)
                }

            }
        }
        return true
    }



    fun header(){
        val header = navigation_view.getHeaderView(0)
        val name = header.findViewById<TextView>(R.id.user_navigation_name)
        val mobile = header.findViewById<TextView>(R.id.user_navigation_mobile)
        if(FirebaseAuth.getInstance().currentUser==null){
            name.visibility = View.GONE
            mobile.visibility = View.GONE
        }
        else{
            FirebaseFirestore.getInstance().collection(Constants.USER).document(getCurrentUserID()!!).get()
                .addOnSuccessListener {
                    val user = it.toObject(User::class.java)!!
                    val pattern = Regex("\\s+")  // separate for white-spaces
                    val ans : List<String> = pattern.split(user.name!!)
                    name.text = ans[0].toLowerCase(Locale.ROOT)
                    mobile.text = user.mobile
                }
        }


    }


}
