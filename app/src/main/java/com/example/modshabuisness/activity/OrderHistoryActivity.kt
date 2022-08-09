package com.example.modshabuisness.activity

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.modshabuisness.Adapter.OrderHistoryAdapter
import com.example.modshabuisness.Constants
import com.example.modshabuisness.R
import com.example.modshabuisness.model.UserProductModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_order_history.*

class OrderHistoryActivity : RootActivity() {

    lateinit var orderHistoryAdapter :OrderHistoryAdapter
    var orderHistory = mutableListOf<UserProductModel>()
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        if (!isNetworkActive(this)) {
            showSnackbar(getString(R.string.network), order_history_view)
        }
        else{
            showCircleProgress()
            val fireStoreList = findViewById<RecyclerView>(R.id.recycle_product_user_order_history)
            val db = FirebaseFirestore.getInstance().collection(Constants.USER).document(getCurrentUserID()!!).collection(Constants.HISTORY)

            db.addSnapshotListener { snapshot, e->
                if(e!=null){
                    return@addSnapshotListener
                }
                orderHistory = snapshot!!.toObjects(UserProductModel::class.java)
                if(orderHistory.isEmpty()){
                    empty_order_history.visibility = View.VISIBLE
                    recycle_product_user_order_history.visibility = View.GONE
                }
                else{
                    orderHistoryAdapter = OrderHistoryAdapter(orderHistory,this@OrderHistoryActivity,ProductInterface::class.java)
                    fireStoreList.adapter = orderHistoryAdapter
                    fireStoreList.setHasFixedSize(true)
                    fireStoreList.layoutManager = LinearLayoutManager(this@OrderHistoryActivity)
                }
                hideProgress()
            }
        }

        history_back.setOnClickListener {
            onBackPressed()
        }


    }


    class OrderHistoryViewHolder(view: View) : RecyclerView.ViewHolder(view){

    }
}
