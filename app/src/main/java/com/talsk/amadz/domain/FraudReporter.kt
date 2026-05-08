package com.talsk.amadz.domain

import android.util.Log
import com.talsk.amadz.data.local.UserPreferences
import com.talsk.amadz.domain.repo.AuthRepository
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FraudReporter"

@Singleton
class FraudReporter @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences
) {
    suspend fun reportIncomingCall(phoneNumber: String) {
        val token = userPreferences.getAccessToken() ?: run {
            Log.d(TAG, "No token — skipping fraud report")
            return
        }
        authRepository.reportIncomingCall(token, phoneNumber)
            .onFailure { Log.w(TAG, "Failed to report incoming call: ${it.message}") }
    }
}
