package com.desmond.codelab.data

import com.desmond.codelab.domain.body.LoginBody
import com.desmond.codelab.domain.response.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun authenticateUser(@Body loginBody: LoginBody): Response<LoginResponse>
}