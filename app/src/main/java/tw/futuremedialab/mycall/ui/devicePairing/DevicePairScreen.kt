package tw.futuremedialab.mycall.ui.devicePairing

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import boofcv.android.ConvertBitmap
import boofcv.factory.fiducial.FactoryFiducial
import boofcv.struct.image.GrayU8
import org.ddogleg.struct.DogArray_I8

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicePairScreen(
    onBackClick: () -> Unit,
    pairingCode: String? = null,
    vm: DevicePairViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsStateWithLifecycle()

    LaunchedEffect(pairingCode) {
        if (!pairingCode.isNullOrBlank()) {
            vm.onPairingCodeScanned(pairingCode)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Pair device") },
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is DevicePairViewModel.State.Scanning -> {
                    ScanningView(
                        onCodeScanned = { vm.onPairingCodeScanned(it) },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                is DevicePairViewModel.State.Approving -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(24.dp))
                        Text("Linking device…", style = MaterialTheme.typography.bodyLarge)
                    }
                }

                is DevicePairViewModel.State.Success -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            modifier = Modifier.size(96.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = "Device linked successfully.",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(32.dp))
                        Button(onClick = onBackClick) { Text("Done") }
                    }
                }

                is DevicePairViewModel.State.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(96.dp),
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(Modifier.height(24.dp))
                        Text(
                            text = (state as DevicePairViewModel.State.Error).message,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(32.dp))
                        Button(onClick = { vm.resetToScanning() }) {
                            Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Try again")
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedButton(onClick = onBackClick) { Text("Cancel") }
                    }
                }
            }
        }
    }
}

@Composable
private fun ScanningView(onCodeScanned: (String) -> Unit, modifier: Modifier = Modifier) {
    var manualCode by remember { mutableStateOf("") }

    val extractPairingCode = { qrContent: String ->
        val code = if (qrContent.startsWith("safecall://pair_device/")) {
            qrContent.removePrefix("safecall://pair_device/")
        } else {
            qrContent
        }
        onCodeScanned(code)
    }

    Box(modifier = modifier) {
        Column(modifier = Modifier.fillMaxSize()) {
            QrScannerView(
                onQrDetected = extractPairingCode,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(0.4f)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Or enter code manually",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                OutlinedTextField(
                    value = manualCode,
                    onValueChange = { manualCode = it },
                    placeholder = { Text("Pairing code") },
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    singleLine = true
                )
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = {
                        if (manualCode.isNotBlank()) {
                            extractPairingCode(manualCode)
                            manualCode = ""
                        }
                    },
                    enabled = manualCode.isNotBlank()
                ) {
                    Text("Submit")
                }
            }
        }
    }
}

@Composable
private fun QrScannerView(onQrDetected: (String) -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(Unit) {
        onDispose {
            ProcessCameraProvider.getInstance(context).get().unbindAll()
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraFuture = ProcessCameraProvider.getInstance(ctx)
            cameraFuture.addListener({
                val cameraProvider = cameraFuture.get()

                val preview = Preview.Builder().build()
                    .also { it.surfaceProvider = previewView.surfaceProvider }

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(
                            ContextCompat.getMainExecutor(ctx),
                            BoofCvQrAnalyzer(onQrDetected)
                        )
                    }

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
}

private class BoofCvQrAnalyzer(private val onQrDetected: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val detector = FactoryFiducial.qrcode(null, GrayU8::class.java)
    private val gray = GrayU8()

    override fun analyze(imageProxy: ImageProxy) {
        try {
            val bmp = imageProxy.toBitmap()
            ConvertBitmap.bitmapToGray(bmp, gray, null as DogArray_I8?)
            detector.process(gray)
            val detections = detector.getDetections()
            if (detections.isNotEmpty()) {
                onQrDetected(detections[0].message)
            }
        } finally {
            imageProxy.close()
        }
    }
}
