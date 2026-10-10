package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.data.model.AdminOrderEntity
import com.example.ui.components.SampleOriginPill
import com.example.ui.components.formatInrDetailed
import com.example.ui.scanner.ParsedPaymentQrPayload
import com.example.ui.scanner.ZxingQrCodeAnalyzer
import com.example.ui.theme.AmberPending
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.concurrent.Executors

@Composable
fun QrScannerScreen(
    preselectedOrder: AdminOrderEntity?,
    allOrders: List<AdminOrderEntity>,
    onBack: () -> Unit,
    onProceedToUtrWithScan: (orderId: String, scannedUtr: String?, scannedUpi: String?) -> Unit,
    onInspectOrderDetails: (orderId: String) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var scannedResult by remember { mutableStateOf<ParsedPaymentQrPayload?>(null) }
    var torchEnabled by rememberSaveable { mutableStateOf(false) }
    var boundCamera by remember { mutableStateOf<Camera?>(null) }

    val targetOrder = remember(scannedResult, preselectedOrder, allOrders) {
        val matchedId = scannedResult?.matchedOrderId
        when {
            matchedId != null -> allOrders.find { it.orderId.equals(matchedId, ignoreCase = true) } ?: preselectedOrder
            preselectedOrder != null -> preselectedOrder
            else -> allOrders.firstOrNull()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("qr_scanner_screen")
    ) {
        // Top App Bar
        Surface(
            color = NavySurface,
            border = BorderStroke(1.dp, NavyBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("qr_scanner_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "CameraX Payment & UTR Scanner",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (preselectedOrder != null) {
                            "Scanning for Order ${preselectedOrder.orderId}"
                        } else {
                            "Scan UPI QR or 12-Digit UTR Receipt QR"
                        },
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = JetBrainsMonoFamily,
                        color = CyanAccent
                    )
                }
                if (hasCameraPermission && boundCamera?.cameraInfo?.hasFlashUnit() == true) {
                    IconButton(
                        onClick = {
                            val nextState = !torchEnabled
                            torchEnabled = nextState
                            boundCamera?.cameraControl?.enableTorch(nextState)
                        },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NavySurfaceElevated)
                            .testTag("qr_scanner_torch_button")
                    ) {
                        Icon(
                            imageVector = if (torchEnabled) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Toggle Torch",
                            tint = if (torchEnabled) AmberPending else CyanAccent
                        )
                    }
                }
                Spacer(modifier = Modifier.width(6.dp))
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // CameraX Live Viewfinder Box or Permission Gate
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(310.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.5.dp, ElectricBlue.copy(alpha = 0.65f))
            ) {
                if (hasCameraPermission) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        CameraXQrPreview(
                            onQrDetected = { payload ->
                                if (scannedResult == null) {
                                    scannedResult = payload
                                }
                            },
                            onCameraBound = { camera ->
                                boundCamera = camera
                            }
                        )

                        // Animated Viewfinder Reticle Overlay
                        ScannerReticleOverlay(
                            isLocked = scannedResult != null,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Bottom Overlay Status Pill
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(12.dp),
                            shape = RoundedCornerShape(50),
                            color = NavyBackground.copy(alpha = 0.88f),
                            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCodeScanner,
                                    contentDescription = null,
                                    tint = if (scannedResult != null) EmeraldVerified else CyanAccent,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (scannedResult != null) {
                                        "QR Code Captured • Ready for Verification"
                                    } else {
                                        "Align Payment QR or UTR Receipt inside frame"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                } else {
                    // Runtime Camera Permission Request Card
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(ElectricBlue.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(30.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Camera Access Required for QR Scanning",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Grant camera permission to scan UPI payment QR codes and 12-digit UTR receipt barcodes with CameraX.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                            modifier = Modifier.testTag("grant_camera_permission_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ElectricBlue,
                                contentColor = NavyBackground
                            )
                        ) {
                            Text(
                                text = "Grant Camera Permission",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Detected QR Result Card (if a QR code was decoded)
            val currentScan = scannedResult
            if (currentScan != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("scanned_qr_result_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldSurface.copy(alpha = 0.7f)),
                    border = BorderStroke(1.5.dp, EmeraldVerified)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldVerified,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Decoded QR Payload",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = EmeraldVerified,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            SampleOriginPill(isSampleData = true)
                        }

                        HorizontalDivider(color = EmeraldVerified.copy(alpha = 0.3f))

                        if (currentScan.matchedOrderId != null) {
                            DetailRow(
                                label = "Matched Order ID",
                                value = currentScan.matchedOrderId,
                                valueColor = CyanAccent,
                                isMono = true
                            )
                        }
                        if (currentScan.payeeUpiId != null) {
                            DetailRow(
                                label = "Parsed UPI ID",
                                value = currentScan.payeeUpiId,
                                isMono = true
                            )
                        }
                        if (currentScan.amountInr != null) {
                            DetailRow(
                                label = "QR Amount",
                                value = formatInrDetailed(currentScan.amountInr),
                                valueColor = TextPrimary,
                                isBold = true
                            )
                        }
                        if (currentScan.extractedUtrCandidate != null) {
                            DetailRow(
                                label = "Extracted 12-Digit UTR",
                                value = currentScan.extractedUtrCandidate,
                                valueColor = EmeraldVerified,
                                isMono = true,
                                isBold = true
                            )
                        }
                        if (currentScan.transactionNote != null) {
                            DetailRow(
                                label = "Reference Note",
                                value = currentScan.transactionNote
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        val orderForNavigation = targetOrder
                        if (orderForNavigation != null) {
                            Button(
                                onClick = {
                                    onProceedToUtrWithScan(
                                        orderForNavigation.orderId,
                                        currentScan.extractedUtrCandidate,
                                        currentScan.payeeUpiId
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("qr_proceed_to_utr_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricBlue,
                                    contentColor = NavyBackground
                                )
                            ) {
                                Icon(Icons.Default.ReceiptLong, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Continue to UTR Submission (${orderForNavigation.orderId})",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = { onInspectOrderDetails(orderForNavigation.orderId) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, CyanAccent)
                            ) {
                                Text(
                                    text = "View Order ${orderForNavigation.orderId} Details",
                                    color = CyanAccent,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = { scannedResult = null },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("qr_rescan_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, NavyBorder)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = TextSecondary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Another QR Code", color = TextSecondary)
                        }
                    }
                }
            }

            // Sandbox Sample QR Payload Presets (for instant testing on streaming emulators)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Sample QR Payloads (Emulator Test Presets)",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "On a cloud emulator without a physical QR sheet in front of the camera, you can load a sample UPI/UTR QR payload into the parser below:",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )

                    val activeOrder = preselectedOrder ?: allOrders.firstOrNull()
                    val sampleOrderId = activeOrder?.orderId ?: "MXP-ORD-9041"
                    val sampleUpi = activeOrder?.assignedPlaceholderUpiId ?: "mexcpay.sandbox.desk01@okaxis"
                    val sampleAmount = activeOrder?.inrAmount ?: 25000.0

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val randomUtr = (100_000_000_000L..999_999_999_999L).random().toString()
                                val rawUpiUri =
                                    "upi://pay?pa=$sampleUpi&pn=MEXC%20PAY%20SANDBOX&am=$sampleAmount&tn=$sampleOrderId&tr=$randomUtr"
                                scannedResult = com.example.ui.scanner.PaymentQrParser.parse(rawUpiUri)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("scan_sample_upi_utr_qr_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, ElectricBlue)
                        ) {
                            Text(
                                text = "Load Sample UPI + UTR QR",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanAccent,
                                textAlign = TextAlign.Center
                            )
                        }

                        OutlinedButton(
                            onClick = {
                                val randomUtr = (100_000_000_000L..999_999_999_999L).random().toString()
                                val rawReceipt = "NPCI_UTR_RECEIPT:$randomUtr|ORDER:$sampleOrderId|SANDBOX_SAMPLE"
                                scannedResult = com.example.ui.scanner.PaymentQrParser.parse(rawReceipt)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("scan_sample_receipt_qr_button"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, NavyBorder)
                        ) {
                            Text(
                                text = "Load Sample 12-Digit UTR QR",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Security Footnote
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = NavySurfaceElevated,
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Scanning a QR code only pre-fills checkout/UTR fields for convenience. It never performs fake verification or credits wallet balances automatically.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun CameraXQrPreview(
    onQrDetected: (ParsedPaymentQrPayload) -> Unit,
    onCameraBound: (Camera?) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    val previewView = remember {
        PreviewView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }

    DisposableEffect(lifecycleOwner) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val listener = Runnable {
            val cameraProvider = runCatching { cameraProviderFuture.get() }.getOrNull() ?: return@Runnable
            val preview = Preview.Builder().build().also {
                it.surfaceProvider = previewView.surfaceProvider
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(
                        cameraExecutor,
                        ZxingQrCodeAnalyzer { parsed ->
                            ContextCompat.getMainExecutor(context).execute {
                                onQrDetected(parsed)
                            }
                        }
                    )
                }

            val selector = when {
                runCatching { cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA) }.getOrDefault(false) ->
                    CameraSelector.DEFAULT_BACK_CAMERA
                runCatching { cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA) }.getOrDefault(false) ->
                    CameraSelector.DEFAULT_FRONT_CAMERA
                else -> CameraSelector.DEFAULT_BACK_CAMERA
            }

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )
                onCameraBound(camera)
            } catch (_: Exception) {
                onCameraBound(null)
            }
        }

        cameraProviderFuture.addListener(listener, ContextCompat.getMainExecutor(context))

        onDispose {
            runCatching {
                cameraProviderFuture.get().unbindAll()
            }
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier
            .fillMaxSize()
            .testTag("camerax_preview_view")
    )
}

@Composable
private fun ScannerReticleOverlay(
    isLocked: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "laser_scan")
    val laserProgress by infiniteTransition.animateFloat(
        initialValue = 0.15f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_line"
    )

    val frameColor = if (isLocked) EmeraldVerified else ElectricBlue

    Canvas(modifier = modifier) {
        val boxSize = size.minDimension * 0.66f
        val left = (size.width - boxSize) / 2f
        val top = (size.height - boxSize) / 2f

        // Outer rounded reticle frame
        drawRoundRect(
            color = frameColor,
            topLeft = Offset(left, top),
            size = Size(boxSize, boxSize),
            cornerRadius = CornerRadius(28f, 28f),
            style = Stroke(width = 6f)
        )

        // Animated horizontal laser line when actively scanning
        if (!isLocked) {
            val laserY = top + (boxSize * laserProgress)
            drawLine(
                color = CyanAccent,
                start = Offset(left + 16f, laserY),
                end = Offset(left + boxSize - 16f, laserY),
                strokeWidth = 4f
            )
        }
    }
}
