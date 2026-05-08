package com.talsk.amadz.data.network

import com.talsk.amadz.data.network.dto.AuthTokenDto
import com.talsk.amadz.data.network.dto.LoginRequestDto
import com.talsk.amadz.data.network.dto.RegisterRequestDto
import com.talsk.amadz.data.network.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

interface AuthApiService {

    @GET("api/auth/status")
    suspend fun getStatus(
        @Header("Authorization") authorization: String
    ): Response<UserProfileDto>

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequestDto
    ): Response<AuthTokenDto>

    @POST("api/auth/register")
    suspend fun register(
        @Body request: RegisterRequestDto
    ): Response<UserProfileDto>
}
