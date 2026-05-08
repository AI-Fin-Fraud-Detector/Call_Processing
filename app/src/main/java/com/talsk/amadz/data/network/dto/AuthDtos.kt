package com.talsk.amadz.data.network.dto

import com.google.gson.annotations.SerializedName

data class LoginRequestDto(
    val email: String,
    val password: String
)

data class RegisterRequestDto(
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val name: String,
    val password: String
)

data class AuthTokenDto(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("token_type") val tokenType: String
)

data class UserProfileDto(
    val uuid: String,
    val email: String,
    @SerializedName("phone_number") val phoneNumber: String,
    val name: String
)

data class FraudReportRequestDto(
    @SerializedName("phone_number") val phoneNumber: String
)

data class PushSubscribeRequestDto(
    val platform: String,
    @SerializedName("fcm_token") val fcmToken: String? = null
)

data class PushSubscribeResponseDto(
    val message: String,
    @SerializedName("user_id") val userId: String
)

data class ApiErrorDto(
    val detail: String?
)
