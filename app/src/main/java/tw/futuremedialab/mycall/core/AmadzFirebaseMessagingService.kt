package tw.futuremedialab.mycall.core

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import tw.futuremedialab.mycall.data.local.UserPreferences
import tw.futuremedialab.mycall.domain.CallAction
import tw.futuremedialab.mycall.domain.CallOrchestrator
import tw.futuremedialab.mycall.domain.repo.AuthRepository
import tw.futuremedialab.mycall.util.LoggingUtil
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

    @Inject
    lateinit var callOrchestrator: CallOrchestrator

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        LoggingUtil.d(TAG, "FCM token refreshed")
        serviceScope.launch {
            val accessToken = userPreferences.getAccessToken() ?: return@launch
            authRepository.subscribePush(accessToken, token)
                .onFailure { LoggingUtil.w(TAG, "Re-subscribe failed: ${it.message}") }
        }
    }

    override fun onMessageReceived(message: RemoteMessage) {
        LoggingUtil.d(TAG, "FCM message received: ${message.data}")
        when (message.data["action"]) {
            "hangup" -> {
                LoggingUtil.d(TAG, "Hangup command received, disconnecting active call")
                callOrchestrator.onAction(CallAction.Hangup)
            }
        }
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }
}
