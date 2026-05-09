package tw.futuremedialab.mycall.ui.onboarding

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import tw.futuremedialab.mycall.MainActivity
import tw.futuremedialab.mycall.ui.theme.AmadzTheme
import tw.futuremedialab.mycall.util.PermissionChecker
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OnboardingActivity : ComponentActivity() {

    private val dialerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                navigateToMain()
            }
        }

    private val dialerRequestIntent: Intent
        get() = PermissionChecker.changeDialogRequestUiIntent(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmadzTheme {
                OnboardingScreen(
                    onRequestDialerPermission = {
                        dialerLauncher.launch(dialerRequestIntent)
                    },
                    onOpenAppSettings = {
                        startActivity(
                            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                data = Uri.fromParts("package", packageName, null)
                            }
                        )
                    }
                )
            }
        }
    }

    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
