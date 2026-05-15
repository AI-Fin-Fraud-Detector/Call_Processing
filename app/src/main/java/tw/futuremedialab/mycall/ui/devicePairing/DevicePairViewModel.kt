package tw.futuremedialab.mycall.ui.devicePairing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tw.futuremedialab.mycall.data.local.UserPreferences
import tw.futuremedialab.mycall.domain.repo.AuthRepository
import tw.futuremedialab.mycall.util.LoggingUtil
import javax.inject.Inject

@HiltViewModel
class DevicePairViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
) : ViewModel() {

    sealed interface State {
        data object Scanning : State
        data object Approving : State
        data object Success : State
        data class Error(val message: String) : State
    }

    private val _state = MutableStateFlow<State>(State.Scanning)
    val state: StateFlow<State> = _state.asStateFlow()

    fun onPairingCodeScanned(rawValue: String) {
        LoggingUtil.d("DevicePairVM", "onPairingCodeScanned called with: $rawValue")
        if (_state.value is State.Approving) {
            LoggingUtil.d("DevicePairVM", "Already approving, returning")
            return
        }
        viewModelScope.launch {
            _state.value = State.Approving
            val token = userPreferences.getAccessToken()
            if (token.isNullOrBlank()) {
                LoggingUtil.w("DevicePairVM", "No token, user not logged in")
                _state.value = State.Error("You need to be logged in to pair a device.")
                return@launch
            }
            val pairingCode = extractPairingCode(rawValue)
            LoggingUtil.d("DevicePairVM", "Extracted pairing code: $pairingCode")
            if (pairingCode.isBlank()) {
                LoggingUtil.w("DevicePairVM", "Invalid pairing code")
                _state.value = State.Error("That QR code is not a SafeCall pairing code.")
                return@launch
            }
            LoggingUtil.d("DevicePairVM", "Approving device with code: $pairingCode")
            authRepository.approveDevicePairing(token, pairingCode)
                .onSuccess {
                    LoggingUtil.d("DevicePairVM", "Pairing approved successfully")
                    _state.value = State.Success
                }
                .onFailure {
                    LoggingUtil.e("DevicePairVM", "Pairing failed: ${it.message}")
                    _state.value = State.Error(it.message ?: "Pairing failed.")
                }
        }
    }

    fun resetToScanning() {
        _state.value = State.Scanning
    }

    private fun extractPairingCode(raw: String): String {
        val trimmed = raw.trim()
        return if (trimmed.contains("code=")) {
            trimmed.substringAfter("code=").substringBefore('&').trim()
        } else {
            trimmed
        }
    }
}
