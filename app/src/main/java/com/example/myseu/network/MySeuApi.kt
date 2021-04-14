package com.example.myseu.network

import com.example.myseu.model.FirebaseTokenBody
import com.example.myseu.model.StudentAuth
import com.example.myseu.model.Result
import io.reactivex.Single
import retrofit2.http.*

interface MySeuApi {

    @POST("/api/studentAuth")
    fun studentAuth(@Query("username") userName: String, @Query("password") password: String ) : Single<StudentAuth>

    @POST("/api/updateFireToken")
    fun updateFirebaseToken(@Header("Authorization") authToken: String, @Body token: FirebaseTokenBody) : Single<Result>
}