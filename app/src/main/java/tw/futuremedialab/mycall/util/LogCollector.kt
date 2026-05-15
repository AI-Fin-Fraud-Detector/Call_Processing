package tw.futuremedialab.mycall.util

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class LogEntry(
    val timestamp: String,
    val level: String,
    val tag: String,
    val message: String
)

object LogCollector {
    private val maxLogs = 500
    private val logs = mutableListOf<LogEntry>()
    private val _logsFlow = MutableStateFlow<List<LogEntry>>(emptyList())
    val logsFlow: StateFlow<List<LogEntry>> = _logsFlow.asStateFlow()

    private val dateFormat = SimpleDateFormat("HH:mm:ss.SSS", Locale.US)

    fun addLog(level: String, tag: String, message: String) {
        val timestamp = dateFormat.format(Date())
        val entry = LogEntry(timestamp, level, tag, message)

        logs.add(entry)
        if (logs.size > maxLogs) {
            logs.removeAt(0)
        }

        _logsFlow.value = logs.toList()
    }

    fun clearLogs() {
        logs.clear()
        _logsFlow.value = emptyList()
    }

    fun getLogs(): List<LogEntry> = logs.toList()
}
