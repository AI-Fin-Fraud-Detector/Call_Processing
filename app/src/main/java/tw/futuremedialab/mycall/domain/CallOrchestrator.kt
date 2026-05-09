package tw.futuremedialab.mycall.domain

import android.telecom.Call
import tw.futuremedialab.mycall.domain.entity.CallState
import kotlinx.coroutines.flow.StateFlow

interface CallOrchestrator {
    val callState: StateFlow<CallState>

    fun onAction(callAction: CallAction)
    fun setCallServiceAudioDelegate(audioController: CallServiceAudioDelegate)
    fun onCallAdded(call: Call)
    fun onCallRemoved(call: Call)
    fun onDestroy()
}
