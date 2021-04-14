package com.example.myseu.network

import com.example.myseu.model.FirebaseTokenBody
import com.example.myseu.model.StudentAuth
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class MySeuServiceApi {

    private val BASE_URL = "https://reg.seu.edu.ge/"


    private var api = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(MySeuApi::class.java)

    fun getStudentAuth(userName: String, password: String): Single<StudentAuth> = api.studentAuth(userName, password)
    fun updateFireBaseToken(authToken: String, token: FirebaseTokenBody) = api.updateFirebaseToken(authToken, token)

}