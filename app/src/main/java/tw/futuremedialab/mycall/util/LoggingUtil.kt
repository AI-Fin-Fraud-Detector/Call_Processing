package tw.futuremedialab.mycall.util

import android.util.Log

object LoggingUtil {
    fun d(tag: String, message: String, throwable: Throwable? = null) {
        Log.d(tag, message, throwable)
        LogCollector.addLog("D", tag, if (throwable != null) "$message\n${Log.getStackTraceString(throwable)}" else message)
    }

    fun e(tag: String, message: String, throwable: Throwable? = null) {
        Log.e(tag, message, throwable)
        LogCollector.addLog("E", tag, if (throwable != null) "$message\n${Log.getStackTraceString(throwable)}" else message)
    }

    fun w(tag: String, message: String, throwable: Throwable? = null) {
        Log.w(tag, message, throwable)
        LogCollector.addLog("W", tag, if (throwable != null) "$message\n${Log.getStackTraceString(throwable)}" else message)
    }

    fun i(tag: String, message: String, throwable: Throwable? = null) {
        Log.i(tag, message, throwable)
        LogCollector.addLog("I", tag, if (throwable != null) "$message\n${Log.getStackTraceString(throwable)}" else message)
    }
}
