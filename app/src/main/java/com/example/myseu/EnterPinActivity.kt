package com.example.myseu

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alimuzaffar.lib.pin.PinEntryEditText
import com.example.myseu.utils.AsteriskPasswordTransformationMethod
import kotlinx.android.synthetic.main.activity_enter_pin.*


class EnterPinActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enter_pin)

        val pinEntry = findViewById<View>(R.id.txt_pin_entry) as PinEntryEditText
        pinEntry.transformationMethod = AsteriskPasswordTransformationMethod()
        pinEntry?.setOnPinEnteredListener { str ->

            val sharedPreferences = getSharedPreferences("PIN", Activity.MODE_PRIVATE)
            val pin = sharedPreferences.getString("pin", "")

            if (pin != ""){
                if (pin == str.toString()) {
                    val sharedPreferences = getSharedPreferences("TOKEN", Activity.MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", "")
                    WebViewActivity.start(this@EnterPinActivity, token!!)
                } else Toast.makeText(this@EnterPinActivity, applicationContext.getString(R.string.wrong_pin), Toast.LENGTH_LONG).show()
            } else {
                Log.e("strstr->>>", str.toString())
                val editor = getSharedPreferences("PIN", Context.MODE_PRIVATE).edit()
                editor.putString("pin", str.toString())
                editor.commit()

                val returnIntent = Intent()
                setResult(Activity.RESULT_OK, returnIntent)
                finish()
            }

//            val editor = getSharedPreferences("PIN", Context.MODE_PRIVATE).edit()
//            editor.putString("pin", str.toString())
//            editor.apply()
//
//            val returnIntent = Intent()
//            setResult(Activity.RESULT_OK, returnIntent)
//            finish()

        }
    }
}