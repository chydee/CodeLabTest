package com.desmond.codelab.data.repository

import com.desmond.codelab.data.ApiService
import com.desmond.codelab.domain.body.LoginBody
import com.desmond.codelab.domain.response.LoginResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject

class MainRepository @Inject constructor(private val service: ApiService) {
    suspend fun authenticateUser(body: LoginBody): Flow<Response<LoginResponse>> {
        return flow {
            val response = service.authenticateUser(body)
            emit(response)
        }.flowOn(Dispatchers.IO)
    }
}