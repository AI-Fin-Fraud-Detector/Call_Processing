package tw.futuremedialab.mycall.ui

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import tw.futuremedialab.mycall.ui.auth.AuthNavGraph
import tw.futuremedialab.mycall.ui.home.HomeScreen
import kotlinx.serialization.Serializable

@Serializable
data object HomeKey : NavKey

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainNavGraph(appViewModel: AppViewModel = hiltViewModel()) {
    val startupState by appViewModel.startupState.collectAsStateWithLifecycle()

    when (val state = startupState) {
        is AppStartupState.Loading -> {
            // Keep Splash Screen until state is determined, no extra UI needed
        }

        is AppStartupState.NeedsAuth -> {
            AuthNavGraph(
                sessionExpired = state.sessionExpired,
                onLoginSuccess = { token -> appViewModel.onLoginSuccess(token) }
            )
        }

        is AppStartupState.Authenticated -> {
            val backStack = rememberNavBackStack(HomeKey)
            NavDisplay(
                backStack = backStack,
                entryProvider = entryProvider {
                    entry<HomeKey> {
                        HomeScreen()
                    }
                }
            )
        }
    }
}
