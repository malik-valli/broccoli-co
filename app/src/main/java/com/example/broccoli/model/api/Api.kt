package com.example.broccoli.model.api

import com.example.broccoli.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface Api {

    @POST("/fakeAuth")
    suspend fun sendUser(
        @Body user: User
    ): Response<String>
}