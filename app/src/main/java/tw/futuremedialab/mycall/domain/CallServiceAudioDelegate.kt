package tw.futuremedialab.mycall.domain

interface CallServiceAudioDelegate {
    fun setMicMuted(muted: Boolean)
    fun setSpeaker(enabled: Boolean)
}