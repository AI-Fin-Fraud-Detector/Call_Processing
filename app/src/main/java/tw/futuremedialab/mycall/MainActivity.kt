package tw.futuremedialab.mycall

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import tw.futuremedialab.mycall.core.dial
import tw.futuremedialab.mycall.core.hasDefaultCallingSimConfigured
import tw.futuremedialab.mycall.domain.repo.SimInfoProvider
import tw.futuremedialab.mycall.ui.AppStartupState
import tw.futuremedialab.mycall.ui.AppViewModel
import tw.futuremedialab.mycall.ui.MainNavGraph
import tw.futuremedialab.mycall.ui.onboarding.OnboardingActivity
import tw.futuremedialab.mycall.ui.theme.AmadzTheme
import tw.futuremedialab.mycall.util.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var simInfoProvider: SimInfoProvider

    private val appViewModel: AppViewModel by viewModels()

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        if (savedInstanceState == null) {
            handleDialIntent(intent)
            handleDeepLink(intent)
        }

        // Not set as default dialer app → go to Onboarding first
        if (!PermissionChecker.isDefaultPhoneApp(this)) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        // Request phone-related permissions
        if (!PermissionChecker.hasAllPermissions(this)) {
            permissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.CALL_PHONE,
                    android.Manifest.permission.READ_CALL_LOG,
                    android.Manifest.permission.READ_CONTACTS,
                    android.Manifest.permission.WRITE_CONTACTS,
                    android.Manifest.permission.POST_NOTIFICATIONS,
                )
            )
        }

        // Keep SplashScreen until auth state is determined
        splashScreen.setKeepOnScreenCondition {
            appViewModel.startupState.value == AppStartupState.Loading
        }

        setContent {
            AmadzTheme {
                MainNavGraph(appViewModel = appViewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleDialIntent(intent)
        handleDeepLink(intent)
    }

    private fun handleDialIntent(intent: Intent) {
        // Skip if this is a deep link (handled separately via handleDeepLink)
        val data = intent.data
        if (data?.scheme == "safecall") return

        val phoneNumber = extractPhoneNumber(intent) ?: return
        when (intent.action) {
            Intent.ACTION_CALL,
            Intent.ACTION_VOICE_COMMAND,
            Intent.ACTION_CALL_BUTTON,
            Intent.ACTION_DIAL,
            Intent.ACTION_VIEW -> requestDial(phoneNumber)
        }
    }

    private fun requestDial(phone: String) {
        val sims = simInfoProvider.getSimsInfo().sortedBy { it.simSlotIndex }
        if (sims.size > 1 && !hasDefaultCallingSimConfigured()) {
            val items = sims.map {
                it.displayName?.takeIf(String::isNotBlank) ?: "SIM ${it.simSlotIndex + 1}"
            }.toTypedArray()
            AlertDialog.Builder(this)
                .setTitle("Select SIM card")
                .setItems(items) { dialog, which ->
                    sims.getOrNull(which)?.let { dial(phone = phone, accountId = it.accountId) }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        } else {
            dial(phone)
        }
    }

    private fun extractPhoneNumber(intent: Intent): String? {
        val data: Uri? = intent.data
        val fromData = data?.schemeSpecificPart?.takeIf { it.isNotBlank() }
        if (fromData != null) return fromData
        return intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER)?.takeIf { it.isNotBlank() }
    }

    private fun handleDeepLink(intent: Intent) {
        val data: Uri? = intent.data
        if (data?.scheme == "safecall" && data.host == "pair_device") {
            val pairingCode = data.lastPathSegment?.takeIf { it.isNotBlank() }
            if (pairingCode != null) {
                appViewModel.setDeepLinkPairingCode(pairingCode)
            }
        }
    }
}
