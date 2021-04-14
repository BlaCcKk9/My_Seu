package com.example.myseu

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        val sharedPreferences = getSharedPreferences("TOKEN", Activity.MODE_PRIVATE)
//        val token = sharedPreferences.getString("token", "")

//        if (token != ""){
//            WebViewActivity.start(this@MainActivity, token!!)
//            finish()
//        } else {
            AuthActivity.start(this, false)
            finish()

    }
}