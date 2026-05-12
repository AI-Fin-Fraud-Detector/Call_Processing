package tw.futuremedialab.mycall.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.messaging.FirebaseMessaging
import tw.futuremedialab.mycall.data.local.UserPreferences
import tw.futuremedialab.mycall.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "AppViewModel"

sealed class AppStartupState {
    object Loading : AppStartupState()
    data class NeedsAuth(val sessionExpired: Boolean = false) : AppStartupState()
    object Authenticated : AppStartupState()
}

@HiltViewModel
class AppViewModel @Inject constructor(
    private val userPreferences: UserPreferences,
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _startupState = MutableStateFlow<AppStartupState>(AppStartupState.Loading)
    val startupState: StateFlow<AppStartupState> = _startupState.asStateFlow()

    init {
        viewModelScope.launch { checkAuthStatus() }
    }

    private suspend fun checkAuthStatus() {
        val token = userPreferences.getAccessToken()
        if (token == null) {
            _startupState.value = AppStartupState.NeedsAuth(sessionExpired = false)
            return
        }
        authRepository.getAuthStatus(token)
            .onSuccess {
                _startupState.value = AppStartupState.Authenticated
                subscribeFcm(token)
            }
            .onFailure {
                Log.w(TAG, "Token invalid: ${it.message}")
                userPreferences.clearAccessToken()
                _startupState.value = AppStartupState.NeedsAuth(sessionExpired = true)
            }
    }

    fun onLoginSuccess(token: String) {
        viewModelScope.launch {
            userPreferences.saveAccessToken(token)
            _startupState.value = AppStartupState.Authenticated
            subscribeFcm(token)
        }
    }

    private suspend fun subscribeFcm(token: String) {
        try {
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            authRepository.subscribePush(token, fcmToken)
                .onFailure { Log.w(TAG, "FCM subscribe failed: ${it.message}") }
        } catch (e: Exception) {
            Log.w(TAG, "Could not get FCM token: ${e.message}")
        }
    }
}
