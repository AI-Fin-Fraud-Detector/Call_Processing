package com.talsk.amadz.ui.auth

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import kotlinx.serialization.Serializable

@Serializable
data class LoginKey(val sessionExpired: Boolean = false) : NavKey

@Serializable
data object RegisterKey : NavKey

@Composable
fun AuthNavGraph(
    sessionExpired: Boolean,
    onLoginSuccess: (String) -> Unit
) {
    val backStack = rememberNavBackStack(LoginKey(sessionExpired))

    NavDisplay(
        backStack = backStack,
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<LoginKey> { key ->
                LoginScreen(
                    sessionExpired = key.sessionExpired,
                    onLoginSuccess = onLoginSuccess,
                    onRegisterClick = { backStack.add(RegisterKey) }
                )
            }
            entry<RegisterKey> {
                RegisterScreen(
                    onSuccess = {
                        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    },
                    onBackClick = {
                        if (backStack.size > 1) backStack.removeAt(backStack.lastIndex)
                    }
                )
            }
        }
    )
}
