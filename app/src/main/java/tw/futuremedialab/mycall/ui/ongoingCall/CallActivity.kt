package tw.futuremedialab.mycall.ui.ongoingCall

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tw.futuremedialab.mycall.App
import tw.futuremedialab.mycall.core.LockScreenController
import tw.futuremedialab.mycall.core.ProxyController
import tw.futuremedialab.mycall.ui.theme.AmadzTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CallActivity : ComponentActivity() {
    private val vm: CallViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProxyController(this, lifecycle)
        LockScreenController.enable(this)
        setContent {
            val uiState by vm.callState.collectAsStateWithLifecycle()
            val contactDetail by vm.contact.collectAsStateWithLifecycle()
            AmadzTheme {
                CallScreen(
                    contact = contactDetail,
                    uiState = uiState,
                    onAction = { vm.onAction(it) },
                    onFinish = { finishAndRemoveTask() }
                )
            }
        }
    }

    companion object {

        fun start(context: Context, phone: String) {
            val intent = Intent(context, CallActivity::class.java)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.putExtra("phone", phone)
            context.startActivity(intent)

        }
    }
}



