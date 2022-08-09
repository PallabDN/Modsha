package com.example.modshabuisness.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.modshabuisness.R

class MainActivity : RootActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        Handler().postDelayed({
            startActivity(Intent(this@MainActivity, HomeActivity::class.java))
            finish()
        },1500)

    }
}
