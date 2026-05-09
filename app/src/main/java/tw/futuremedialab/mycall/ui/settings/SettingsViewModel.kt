package tw.futuremedialab.mycall.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tw.futuremedialab.mycall.App
import tw.futuremedialab.mycall.domain.repo.CallLogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val callLogRepository: CallLogRepository
) : ViewModel() {

    fun clearAllCallLogs() {
        viewModelScope.launch {
            callLogRepository.deleteAllCallLogs()
            App.needCallLogRefresh = true
        }
    }
}
