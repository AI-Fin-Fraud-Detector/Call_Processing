package tw.futuremedialab.mycall.data.network

import tw.futuremedialab.mycall.data.network.dto.AuthTokenDto
import tw.futuremedialab.mycall.data.network.dto.DeviceApproveRequestDto
import tw.futuremedialab.mycall.data.network.dto.DeviceApproveResponseDto
import tw.futuremedialab.mycall.data.network.dto.LoginRequestDto
import tw.futuremedialab.mycall.data.network.dto.RegisterRequestDto
import tw.futuremedialab.mycall.data.network.dto.UserProfileDto
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

    /** Approve a QR device-login pairing started by a device. */
    @POST("api/auth/device/approve")
    suspend fun approveDevice(
        @Header("Authorization") authorization: String,
        @Body request: DeviceApproveRequestDto
    ): Response<DeviceApproveResponseDto>
}
