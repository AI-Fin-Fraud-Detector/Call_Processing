package com.talsk.amadz.core

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.talsk.amadz.data.local.UserPreferences
import com.talsk.amadz.domain.repo.AuthRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "AmadzFCMService"

@AndroidEntryPoint
class AmadzFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var authRepository: AuthRepository

    @Inject
    lateinit var userPreferences: UserPreferences

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        Log.d(TAG, "FCM token refreshed")
        serviceScope.launch {
            val accessToken = userPreferences.getAccessToken() ?: return@launch
            authRepository.subscribePush(accessToken, token)
                .onFailure { Log.w(TAG, "Re-subscribe failed: ${it.message}") }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        Log.d(TAG, "FCM message received: ${message.data}")
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
