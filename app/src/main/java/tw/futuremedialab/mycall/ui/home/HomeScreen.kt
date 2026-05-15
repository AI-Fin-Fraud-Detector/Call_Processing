package tw.futuremedialab.mycall.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import tw.futuremedialab.mycall.core.dial
import tw.futuremedialab.mycall.core.hasDefaultCallingSimConfigured
import tw.futuremedialab.mycall.domain.entity.SimInfo
import tw.futuremedialab.mycall.ui.callLogHistory.CallLogHistoryScreen
import tw.futuremedialab.mycall.ui.components.AnimatedBottomBar
import tw.futuremedialab.mycall.ui.components.DialFab
import tw.futuremedialab.mycall.ui.components.SimSelectionDialog
import tw.futuremedialab.mycall.ui.extensions.openContactAddScreen
import tw.futuremedialab.mycall.ui.extensions.openContactDetailScreen
import tw.futuremedialab.mycall.ui.home.calllogs.CallLogsScreen
import tw.futuremedialab.mycall.ui.home.contacts.ContactsScreen
import tw.futuremedialab.mycall.ui.home.favourite.FavouritesScreen
import tw.futuremedialab.mycall.ui.home.searchbar.HomeSearchBar
import tw.futuremedialab.mycall.ui.home.searchbar.SearchBarState
import tw.futuremedialab.mycall.ui.debug.DebugLogsScreen
import tw.futuremedialab.mycall.ui.devicePairing.DevicePairScreen
import tw.futuremedialab.mycall.ui.settings.BlockedNumbersScreen
import tw.futuremedialab.mycall.ui.settings.SettingsScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    deepLinkPairingCode: String? = null,
    onDeepLinkConsumed: () -> Unit = {}
) {
    val backStack = rememberNavBackStack(RecentsKey)
    val context = LocalContext.current
    val vm: HomeViewModel = hiltViewModel()

    LaunchedEffect(deepLinkPairingCode) {
        if (!deepLinkPairingCode.isNullOrBlank()) {
            backStack.add(DevicePairKey)
            onDeepLinkConsumed()
        }
    }
    var searchBarState by rememberSaveable { mutableStateOf(SearchBarState.COLLAPSED) }
    var pendingDialPhone by remember { mutableStateOf<String?>(null) }
    var simOptions by remember { mutableStateOf<List<SimInfo>>(emptyList()) }
    val currentDestination: NavKey? = backStack.lastOrNull()
    val isHomeTab =
        currentDestination == FavouritesKey ||
                currentDestination == RecentsKey ||
                currentDestination == ContactsKey

    fun requestDial(phone: String) {
        if (phone.isBlank()) return
        val sims = vm.getSimsInfo()
        if (sims.size > 1 && !context.hasDefaultCallingSimConfigured()) {
            pendingDialPhone = phone
            simOptions = sims.sortedBy { it.simSlotIndex }
        } else {
            context.dial(phone)
        }
    }

    Scaffold(
        floatingActionButton = {
            if (isHomeTab) {
                DialFab(
                    visible = !searchBarState.isActive(),
                    onClick = {
                        searchBarState = SearchBarState.EXPANDED_WITH_DIAL_PAD
                    }
                )
            }
        },
        topBar = {
            if (isHomeTab) {
                HomeSearchBar(
                    searchBarState = searchBarState,
                    onSearchBarClick = {
                        searchBarState = SearchBarState.EXPANDED
                    },
                    onSearchCloseClick = {
                        searchBarState = SearchBarState.COLLAPSED
                    },
                    onCallClick = ::requestDial,
                    onSettingsClick = { backStack.add(SettingsKey) }
                )
            }
        },
        bottomBar = {
            if (isHomeTab) {
                AnimatedBottomBar(
                    visible = !searchBarState.isActive(),
                    backStack = backStack
                )
            }
        }
    ) { paddingValues ->

        NavDisplay(
            backStack = backStack,
            modifier = if (isHomeTab) {
                Modifier.padding(paddingValues)
            } else {
                Modifier
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                entry(FavouritesKey) {
                    FavouritesScreen(
                        onCallClick = { requestDial(it.phone) },
                        onContactDetailCLick = { context.openContactDetailScreen(it.id) }
                    )
                }
                entry(RecentsKey) {
                    CallLogsScreen(
                        onContactDetailClick = {
                            it.contactId?.let(context::openContactDetailScreen)
                        },
                        onCallClick = {
                            requestDial(it)
                        },
                        onCallLogClick = {
                            backStack.add(
                                CallLogHistoryKey(
                                    phone = it.phone,
                                    contactName = it.name,
                                    contactId = it.contactId
                                )
                            )
                        }
                    )
                }
                entry(ContactsKey) {
                    ContactsScreen(
                        onContactDetailClick = { context.openContactDetailScreen(it.id) },
                        onCallClick = { requestDial(it.phone) }
                    )
                }
                entry<CallLogHistoryKey> {
                    CallLogHistoryScreen(
                        phone = it.phone,
                        contactName = it.contactName,
                        onBackClick = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        },
                        onCallClick = { phone ->
                            requestDial(phone)
                        },
                        onAddContactClick = { phone ->
                            context.openContactAddScreen(phone)
                        },
                    )
                }
                entry(SettingsKey) {
                    SettingsScreen(
                        onBackClick = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        },
                        onBlockedNumbersClick = {
                            backStack.add(BlockedNumbersKey)
                        },
                        onPairDeviceClick = {
                            backStack.add(DevicePairKey)
                        },
                        onViewDebugLogsClick = {
                            backStack.add(DebugLogsKey)
                        }
                    )
                }
                entry(BlockedNumbersKey) {
                    BlockedNumbersScreen(
                        onBackClick = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        }
                    )
                }
                entry(DevicePairKey) {
                    DevicePairScreen(
                        onBackClick = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        },
                        pairingCode = deepLinkPairingCode
                    )
                }
                entry(DebugLogsKey) {
                    DebugLogsScreen(
                        onBackClick = {
                            if (backStack.size > 1) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        }
                    )
                }
            }
        )

    }

    if (pendingDialPhone != null && simOptions.isNotEmpty()) {
        SimSelectionDialog(
            sims = simOptions,
            onSimSelected = { sim ->
                val phone = pendingDialPhone ?: return@SimSelectionDialog
                context.dial(phone = phone, accountId = sim.accountId)
                pendingDialPhone = null
                simOptions = emptyList()
            },
            onDismiss = {
                pendingDialPhone = null
                simOptions = emptyList()
            }
        )
    }
}
