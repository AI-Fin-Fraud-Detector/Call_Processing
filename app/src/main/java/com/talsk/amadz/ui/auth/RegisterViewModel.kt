package com.talsk.amadz.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.talsk.amadz.domain.repo.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var email by mutableStateOf("")
    var phoneNumber by mutableStateOf("")
    var name by mutableStateOf("")
    var password by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var isSuccess by mutableStateOf(false)

    fun register(onSuccess: () -> Unit) {
        if (email.isBlank() || phoneNumber.isBlank() || name.isBlank() || password.isBlank()) {
            errorMessage = "請填寫所有欄位"
            return
        }
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            authRepository.register(email.trim(), phoneNumber.trim(), name.trim(), password)
                .onSuccess {
                    isSuccess = true
                    onSuccess()
                }
                .onFailure { errorMessage = it.message }
            isLoading = false
        }
    }
}
