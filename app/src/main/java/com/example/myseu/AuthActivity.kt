package com.example.myseu


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.alimuzaffar.lib.pin.PinEntryEditText
import com.example.myseu.model.StudentAuth
import com.example.myseu.network.MySeuServiceApi
import com.example.myseu.utils.AsteriskPasswordTransformationMethod
import com.example.myseu.utils.language
import com.example.myseu.utils.slideDown
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_auth.*
import java.util.*


class AuthActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    private val mySeuServiceApi = MySeuServiceApi()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        setLanguageBtnVisibility()
        val pinEntry = findViewById<View>(R.id.txt_pin_entry) as PinEntryEditText
        pinEntry.transformationMethod = AsteriskPasswordTransformationMethod()
        var isLogout = intent.extras!!.getBoolean("isLogout")

        val sharedPreferences = getSharedPreferences("PIN", Activity.MODE_PRIVATE)
        val pin = sharedPreferences.getString("pin", "")


        constraintMain.setOnClickListener {
            backGround.visibility = View.GONE
            constraintEnterPin.visibility = View.GONE
        }

        if (pin == "") {
            backGround.visibility = View.GONE
            constraintEnterPin.visibility = View.GONE
        }
        else{
            if (isLogout){
                backGround.visibility = View.GONE
                constraintEnterPin.visibility = View.GONE
            } else {
                backGround.visibility = View.VISIBLE
                constraintEnterPin.visibility = View.VISIBLE
            }
        }


//        if (isLogout){
//            backGround.visibility = View.GONE
//            constraintEnterPin.visibility = View.GONE
//        } else {
//
//        }

        pinEntry?.setOnPinEnteredListener { str ->

            val sharedPreferences = getSharedPreferences("PIN", Activity.MODE_PRIVATE)
            val pin = sharedPreferences.getString("pin", "")

            if (pin != "") {
                if (pin == str.toString()) {
                    val sharedPreferences = getSharedPreferences("TOKEN", Activity.MODE_PRIVATE)
                    val token = sharedPreferences.getString("token", "")
                    WebViewActivity.start(this@AuthActivity, token!!)
                } else Toast.makeText(
                    this@AuthActivity,
                    applicationContext.getString(R.string.wrong_pin),
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        languageEnglish.setOnClickListener {
            language = "en"
            setLanguageBtnVisibility()
            setLocale("ka")
            start(this@AuthActivity)
            finish()
        }

        languageGeorgia.setOnClickListener {
            language = "ge"
            setLanguageBtnVisibility()
            setLocale("en")
            start(this@AuthActivity)
            finish()
        }
        btnSignIn.setOnClickListener {
            if (isEmpty(etUser.text.toString()) || isEmpty(etPassword.text.toString())) {
                linePassword.setBackgroundColor(Color.RED)
                lineUser.setBackgroundColor(Color.RED)
                tvError.slideDown()
            } else {
                startAuth(etUser.text.toString(), etPassword.text.toString())
            }
        }
    }


    private fun startAuth(userName: String, password: String) {

        disposable.addAll(
            mySeuServiceApi.getStudentAuth(userName, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<StudentAuth>() {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onSuccess(studentAuth: StudentAuth) {
                        when (studentAuth.result) {
                            "yes" -> {
                                startWebView(studentAuth.token)
                            }
                            "no" -> {
                                studentNotExist()
                            }
                        }
                    }

                    override fun onError(e: Throwable) {
                        Log.e("Error----->", e.message.toString())
                    }

                })
        )

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startWebView(token: String) {
        linePassword.setBackgroundColor(Color.parseColor("#EEEEEE"))
        lineUser.setBackgroundColor(Color.parseColor("#EEEEEE"))
        tvError.visibility = View.GONE
        WebViewActivity.start(this@AuthActivity, token)
        finish()
    }

    private fun studentNotExist() {
        linePassword.setBackgroundColor(Color.RED)
        lineUser.setBackgroundColor(Color.RED)
        tvError.slideDown()
    }

    private fun isEmpty(text: String): Boolean = text.isEmpty()

    private fun setLocale(lang: String) {
        val locale = Locale(lang)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
        val editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit()
        editor.putString("My_Lang", lang)
        editor.apply()
    }

    private fun loadLocate() {
        val sharedPreferences = getSharedPreferences("Settings", Activity.MODE_PRIVATE)
        val language = sharedPreferences.getString("My_Lang", "en")
        setLocale(language!!)
    }

    private fun setLanguageBtnVisibility() {

        if (language == "ge") {
            languageEnglish.visibility = View.VISIBLE
            languageGeorgia.visibility = View.GONE
        } else {
            languageEnglish.visibility = View.GONE
            languageGeorgia.visibility = View.VISIBLE
        }
    }


    companion object {
        fun start(context: Context, isLogout: Boolean = false) {
            context.startActivity(Intent(context, AuthActivity::class.java).putExtra("isLogout", isLogout))
        }
    }
}