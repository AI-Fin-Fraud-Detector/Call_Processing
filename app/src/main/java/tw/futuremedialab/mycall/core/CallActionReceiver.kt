package tw.futuremedialab.mycall.core

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import tw.futuremedialab.mycall.domain.CallAction
import tw.futuremedialab.mycall.domain.CallOrchestrator
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CallActionReceiver : BroadcastReceiver() {

    @Inject
    lateinit var callOrchestrator: CallOrchestrator

    override fun onReceive(context: Context, intent: Intent?) {
        when (intent?.action) {
            ACTION_ACCEPT -> callOrchestrator.onAction(CallAction.Answer)
            ACTION_DECLINE -> callOrchestrator.onAction(CallAction.Hangup)
        }
    }

    companion object {
        const val ACTION_ACCEPT = "tw.futuremedialab.mycall.call.ACTION_ACCEPT"
        const val ACTION_DECLINE = "tw.futuremedialab.mycall.call.ACTION_DECLINE"
    }
}
