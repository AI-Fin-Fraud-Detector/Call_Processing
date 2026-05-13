package tw.futuremedialab.mycall.domain

import android.util.Log
import tw.futuremedialab.mycall.data.local.UserPreferences
import tw.futuremedialab.mycall.domain.repo.AuthRepository
import tw.futuremedialab.mycall.domain.repo.ContactDetailProvider
import javax.inject.Inject
import javax.inject.Singleton

private const val TAG = "FraudReporter"

@Singleton
class FraudReporter @Inject constructor(
    private val authRepository: AuthRepository,
    private val userPreferences: UserPreferences,
    private val contactDetailProvider: ContactDetailProvider
) {
    suspend fun reportIncomingCall(phoneNumber: String) {
        val token = userPreferences.getAccessToken() ?: run {
            Log.d(TAG, "No token — skipping fraud report")
            return
        }
        val callerName = if (phoneNumber.isNotEmpty()) {
            contactDetailProvider.getContactByPhone(phoneNumber)?.name
        } else null
        authRepository.reportIncomingCall(token, phoneNumber, callerName)
            .onFailure { Log.w(TAG, "Failed to report incoming call: ${it.message}") }
    }
}
