package com.example.myseu

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.myseu.model.FirebaseTokenBody
import com.example.myseu.model.Result
import com.example.myseu.network.MySeuServiceApi
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_auth.*
import kotlinx.android.synthetic.main.activity_web_view.*


@Suppress("DEPRECATION")
class WebViewActivity : AppCompatActivity() {

    private val disposable = CompositeDisposable()
    private val mySeuServiceApi = MySeuServiceApi()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        var token = intent.extras!!.getString("token")

        ivLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setMessage(R.string.logout_message)

//            builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(R.string.answer_yes) { dialog, which ->
                AuthActivity.start(this@WebViewActivity, true)
                finish()
            }

            builder.setNegativeButton(R.string.answer_no) { dialog, which ->
                dialog.dismiss()
            }

            builder.show()
        }

        ivEnterPin.setOnClickListener {
            startActivityForResult(Intent(this, EnterPinActivity::class.java), 0)
        }

        ivCancelPin.setOnClickListener {
            val builder = AlertDialog.Builder(this)

            builder.setMessage(R.string.cancel_pin_message)

//            builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))

            builder.setPositiveButton(R.string.answer_yes) { dialog, which ->
                ivEnterPin.visibility = View.VISIBLE
                ivCancelPin.visibility = View.GONE
                val editor = getSharedPreferences("PIN", Context.MODE_PRIVATE).edit()
                editor.putString("pin", "")
                editor.commit()
            }

            builder.setNegativeButton(R.string.answer_no) { dialog, which ->
                dialog.dismiss()
            }

            builder.show()

        }

        val sharedPreferences = getSharedPreferences("PIN", Activity.MODE_PRIVATE)
        val pin = sharedPreferences.getString("pin", "")

        if (pin == "") {
            ivEnterPin.visibility = View.VISIBLE
            ivCancelPin.visibility = View.GONE
        } else {
            ivEnterPin.visibility = View.GONE
            ivCancelPin.visibility = View.VISIBLE
        }



        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val fireToken = task.result?.token
                    Log.d("TAG", "onComplete: Token: $fireToken")
                    var fireTokenBody = FirebaseTokenBody(fireToken.toString())
                    disposable.addAll(
                        mySeuServiceApi.updateFireBaseToken(token.toString(), fireTokenBody)
                            .subscribeOn(Schedulers.newThread())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribeWith(object : DisposableSingleObserver<Result>() {

                                override fun onSuccess(result: Result) {

                                    val editor = getSharedPreferences("TOKEN", Context.MODE_PRIVATE).edit()
                                    editor.putString("token", token.toString())
                                    editor.commit()

                                    webView.apply {
                                        settings.javaScriptEnabled = true
                                        settings.safeBrowsingEnabled = true
                                        settings.domStorageEnabled = true
                                        webViewClient = WebViewClient()
                                        loadUrl("http://emis1.seu.edu.ge/")
                                        webViewClient = object : WebViewClient() {
                                            override fun onPageFinished(
                                                view: WebView?,
                                                url: String?
                                            ) {
                                                super.onPageFinished(view, url)
                                                val key = "Student-Token"
                                                val `val` = token.toString()
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                                    webView.evaluateJavascript(
                                                        "localStorage.setItem('$key','$`val`');",
                                                        null
                                                    )
                                                } else {
                                                    webView.loadUrl("javascript:localStorage.setItem('$key','$`val`');")
                                                }
                                            }
                                        }
                                    }

                                }

                                override fun onError(e: Throwable) {

                                }

                            })
                    )
                } else {

                }
            }
    }

    override fun onResume() {
        super.onResume()

        val sharedPreferences = getSharedPreferences("PIN", Activity.MODE_PRIVATE)
        val pin = sharedPreferences.getString("pin", "")
        if (pin == "") {
            ivEnterPin.visibility = View.VISIBLE
            ivCancelPin.visibility = View.GONE
        } else {
            ivEnterPin.visibility = View.GONE
            ivCancelPin.visibility = View.VISIBLE
        }

    }


    companion object {
        fun start(context: Context, token: String) {
            context.startActivity(
                Intent(context, WebViewActivity::class.java)
                    .putExtra("token", token)
            )
        }
    }

}