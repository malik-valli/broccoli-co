package com.example.broccoli.model.repository

import com.example.broccoli.model.api.RetrofitInstance
import com.example.broccoli.model.User
import retrofit2.Response

object Repository {

    suspend fun sendUser(user: User): Response<String> {
        return RetrofitInstance.api.sendUser(user)
    }
}