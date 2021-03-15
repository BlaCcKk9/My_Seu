package com.example.myseu

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_auth.*

class AuthActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)

        icPassword.setOnClickListener {
            Toast.makeText(applicationContext, "Password icon clicked", Toast.LENGTH_LONG).show()
        }
    }

    companion object{
        fun start(context: Context){
            context.startActivity(Intent(context, AuthActivity::class.java))
        }
    }
}