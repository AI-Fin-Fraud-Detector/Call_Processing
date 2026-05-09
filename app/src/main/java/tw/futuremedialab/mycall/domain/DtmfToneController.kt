package tw.futuremedialab.mycall.domain

interface DtmfToneGenerator {
    fun startTone(digit: Char)
    fun stopTone()
}