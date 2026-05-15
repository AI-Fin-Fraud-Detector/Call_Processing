package tw.futuremedialab.mycall.ui.debug

import android.content.ClipData
import android.content.ClipboardManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import tw.futuremedialab.mycall.util.LogCollector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DebugLogsScreen(onBackClick: () -> Unit) {
    val logs by LogCollector.logsFlow.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCopyMessage by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Debug Logs") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Button(
                    onClick = { LogCollector.clearLogs() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        scope.launch {
                            val logsText = logs.joinToString("\n") { log ->
                                "${log.timestamp} ${log.level}/${log.tag}: ${log.message}"
                            }
                            val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Debug Logs", logsText)
                            clipboard.setPrimaryClip(clip)
                            showCopyMessage = true
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy")
                }
            }
            if (showCopyMessage) {
                Text(
                    "Copied to clipboard",
                    modifier = Modifier.padding(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 12.sp
                )
                LaunchedEffect(Unit) {
                    kotlinx.coroutines.delay(2000)
                    showCopyMessage = false
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF1E1E1E))
                    .padding(8.dp)
            ) {
                items(logs) { log ->
                    LogLineItem(log)
                }
            }
        }
    }
}

@Composable
private fun LogLineItem(log: tw.futuremedialab.mycall.util.LogEntry) {
    val textColor = when (log.level) {
        "E" -> Color(0xFFFF6B6B)  // Red for errors
        "W" -> Color(0xFFFFD93D)  // Yellow for warnings
        "D" -> Color(0xFF6BCB77)  // Green for debug
        "I" -> Color(0xFF4D96FF)  // Blue for info
        else -> Color.Gray
    }

    Text(
        text = "${log.timestamp} ${log.level}/${log.tag}: ${log.message}",
        color = textColor,
        fontSize = 10.sp,
        fontFamily = FontFamily.Monospace,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    )
}
