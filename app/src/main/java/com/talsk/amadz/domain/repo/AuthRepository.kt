package com.talsk.amadz.domain.repo

import com.talsk.amadz.domain.entity.UserProfile

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(email: String, phoneNumber: String, name: String, password: String): Result<UserProfile>
    suspend fun getAuthStatus(token: String): Result<UserProfile>
    suspend fun subscribePush(token: String, fcmToken: String): Result<Unit>
    suspend fun reportIncomingCall(token: String, phoneNumber: String): Result<Unit>
}
