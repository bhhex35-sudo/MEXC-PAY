package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AdminOrderEntity
import com.example.ui.components.SampleDataBanner
import com.example.ui.components.SampleOriginPill
import com.example.ui.components.formatInrDetailed
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.AmberPending
import com.example.ui.theme.AmberSurface
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CoralSurface
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.PurpleSandbox
import com.example.ui.theme.PurpleSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.UtrFormStatus

@Composable
fun PaymentInstructionsScreen(
    order: AdminOrderEntity?,
    onBack: () -> Unit,
    onNavigateToUtrSubmission: (String) -> Unit,
    onOpenQrScanner: (String) -> Unit = {}
) {
    val clipboardManager = LocalClipboardManager.current
    var copiedFeedback by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("payment_instructions_screen")
    ) {
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
                    modifier = Modifier.testTag("payment_instructions_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Payment Instructions",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Placeholder UPI ID & Sample QR Code",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanAccent
                    )
                }
                SampleOriginPill(isSampleData = true)
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Order not found.", color = TextSecondary)
            }
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Prominent Sandbox Placeholder Warning
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = AmberSurface,
                border = BorderStroke(1.dp, AmberPending)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.WarningAmber,
                        contentDescription = null,
                        tint = AmberPending,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "PLACEHOLDER UPI & QR (SANDBOX ONLY)",
                            style = MaterialTheme.typography.labelMedium,
                            color = AmberPending,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Do not transfer real funds. The UPI handle and QR graphic below are non-routable placeholders until an authorized backend is connected.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }

            // QR Code Showcase Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = PurpleSurface,
                        border = BorderStroke(1.dp, PurpleSandbox)
                    ) {
                        Text(
                            text = "SAMPLE PLACEHOLDER QR • ORDER ${order.orderId}",
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanAccent,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(220.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(NavySurfaceElevated)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_sample_qr_1791624443612),
                            contentDescription = "Sample Placeholder UPI QR Code",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(14.dp))
                                .testTag("sample_qr_image"),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = order.beneficiaryDisplayName,
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Payable Amount: ${formatInrDetailed(order.inrAmount)}",
                        style = MaterialTheme.typography.titleSmall,
                        color = CyanAccent,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Placeholder UPI ID Copy Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = NavySurfaceElevated,
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Placeholder Beneficiary UPI ID",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                                Text(
                                    text = order.assignedPlaceholderUpiId,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontFamily = JetBrainsMonoFamily,
                                    color = TextPrimary
                                )
                            }
                            IconButton(
                                onClick = {
                                    clipboardManager.setText(AnnotatedString(order.assignedPlaceholderUpiId))
                                    copiedFeedback = "Placeholder UPI ID copied to clipboard"
                                },
                                modifier = Modifier.testTag("copy_upi_id_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy Placeholder UPI ID",
                                    tint = CyanAccent
                                )
                            }
                        }
                    }

                    if (copiedFeedback != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = copiedFeedback!!,
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldVerified
                        )
                    }
                }
            }

            // Step-by-Step Settlement Workflow Card
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
                        text = "Verification & Settlement Protocol",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    HorizontalDivider(color = NavyBorder)
                    InstructionStepRow(
                        stepNum = "1",
                        title = "Include Batch Code in Remarks",
                        desc = "Reference ${order.adminBatchCode} (${order.bankReferenceNote})."
                    )
                    InstructionStepRow(
                        stepNum = "2",
                        title = "Obtain 12-Digit NPCI UTR Number",
                        desc = "Copy the 12-digit Unique Transaction Reference generated by your banking app."
                    )
                    InstructionStepRow(
                        stepNum = "3",
                        title = "Submit UTR for Backend Audit",
                        desc = "Submitting a UTR places the claim in PENDING BACKEND VERIFICATION. Balances are never credited until bank records are confirmed."
                    )
                }
            }

            OutlinedButton(
                onClick = { onOpenQrScanner(order.orderId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("payment_instructions_open_scanner_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, ElectricBlue)
            ) {
                Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Scan Payment / UTR QR with CameraX",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            Button(
                onClick = { onNavigateToUtrSubmission(order.orderId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("proceed_to_utr_submission_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue,
                    contentColor = NavyBackground
                )
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Proceed to UTR Submission",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun InstructionStepRow(
    stepNum: String,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(ElectricBlue.copy(alpha = 0.25f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNum,
                style = MaterialTheme.typography.labelSmall,
                color = CyanAccent,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = desc,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
        }
    }
}

@Composable
fun UtrSubmissionScreen(
    order: AdminOrderEntity?,
    utrFormStatus: UtrFormStatus,
    scannedUtrPrefill: String? = null,
    scannedUpiPrefill: String? = null,
    onConsumeScannedPrefill: () -> Unit = {},
    onOpenQrScanner: (String) -> Unit = {},
    onBack: () -> Unit,
    onSubmitUtr: (String, String, String, String) -> Unit,
    onResetStatus: () -> Unit,
    onNavigateToWallet: () -> Unit
) {
    var utrInput by rememberSaveable { mutableStateOf("") }
    var payerUpiInput by rememberSaveable { mutableStateOf("arjun.mehta.sample@okicici") }
    var remarksInput by rememberSaveable { mutableStateOf("") }
    var localValidationError by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(scannedUtrPrefill, scannedUpiPrefill) {
        if (!scannedUtrPrefill.isNullOrBlank()) {
            utrInput = scannedUtrPrefill.filter { it.isDigit() }.take(12)
            localValidationError = null
        }
        if (!scannedUpiPrefill.isNullOrBlank()) {
            payerUpiInput = scannedUpiPrefill
        }
        if (!scannedUtrPrefill.isNullOrBlank() || !scannedUpiPrefill.isNullOrBlank()) {
            onConsumeScannedPrefill()
        }
    }

    val digitsOnlyCount = utrInput.count { it.isDigit() }
    val isTwelveDigits = utrInput.trim().matches(Regex("^\\d{12}$"))

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("utr_submission_screen")
    ) {
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
                    modifier = Modifier.testTag("utr_submission_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UTR Submission & Verification Queue",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Order ${order?.orderId ?: ""}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = JetBrainsMonoFamily,
                        color = CyanAccent
                    )
                }
            }
        }

        if (order == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Order not found.", color = TextSecondary)
            }
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Security Guardrail Notice
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = NavySurfaceElevated,
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LockClock,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "ZERO AUTO-CREDIT SECURITY POLICY",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Submitting a 12-digit UTR logs an unverified payment claim with status PENDING BACKEND VERIFICATION. Your wallet balance will NOT increase from this submission until an authorized backend confirms bank settlement.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }

            // Order Summary Strip
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Claimed Order Amount",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = formatInrDetailed(order.inrAmount),
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Placeholder UPI: ${order.assignedPlaceholderUpiId}",
                            style = MaterialTheme.typography.labelSmall,
                            fontFamily = JetBrainsMonoFamily,
                            color = TextMuted
                        )
                    }
                    SampleOriginPill(isSampleData = order.isSampleData)
                }
            }

            // If a UTR was just recorded as Pending Verification, show the Pending Verification Status Card
            when (utrFormStatus) {
                is UtrFormStatus.RecordedPending -> {
                    val sub = utrFormStatus.submission
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("utr_pending_verification_card"),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = AmberSurface.copy(alpha = 0.65f)),
                        border = BorderStroke(1.5.dp, AmberPending)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = AmberPending,
                                    modifier = Modifier.size(26.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "PENDING BACKEND VERIFICATION",
                                        style = MaterialTheme.typography.titleMedium,
                                        color = AmberPending,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Payment NOT Verified Yet • Wallet NOT Credited",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextPrimary
                                    )
                                }
                            }

                            HorizontalDivider(color = AmberPending.copy(alpha = 0.35f))

                            DetailRow(label = "Submission ID", value = sub.submissionId, isMono = true)
                            DetailRow(label = "Order Reference", value = sub.orderId, isMono = true)
                            DetailRow(label = "Submitted 12-Digit UTR", value = sub.submittedUtrNumber, isMono = true)
                            DetailRow(
                                label = "Claimed Principal",
                                value = formatInrDetailed(sub.claimedInrAmount),
                                valueColor = AmberPending
                            )
                            DetailRow(
                                label = "Balance Credit Effect",
                                value = "₹0.00 (Awaiting Backend Bank Record)",
                                valueColor = CyanAccent
                            )
                            DetailRow(
                                label = "Submitted At",
                                value = formatTimestamp(sub.submittedAtEpochMs)
                            )

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NavySurface
                            ) {
                                Text(
                                    text = sub.adminAuditNote,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Button(
                                onClick = onNavigateToWallet,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .testTag("view_pending_in_wallet_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricBlue,
                                    contentColor = NavyBackground
                                )
                            ) {
                                Text(
                                    text = "Inspect Unverified Claim in Wallet Ledger",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    utrInput = ""
                                    onResetStatus()
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(1.dp, NavyBorder)
                            ) {
                                Text("Submit Another UTR Reference", color = TextSecondary)
                            }
                        }
                    }
                }

                else -> {
                    // Input Form Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Text(
                                text = "Enter 12-Digit Bank UTR Reference",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Strict NPCI format validation requires exactly 12 numeric digits.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            val activeError = localValidationError
                                ?: (utrFormStatus as? UtrFormStatus.Error)?.message

                            if (activeError != null) {
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("utr_validation_error_banner"),
                                    shape = RoundedCornerShape(12.dp),
                                    color = CoralSurface,
                                    border = BorderStroke(1.dp, CoralAlert)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.GppMaybe,
                                            contentDescription = null,
                                            tint = CoralAlert,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = activeError,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = CoralAlert
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }

                            OutlinedTextField(
                                value = utrInput,
                                onValueChange = { newValue ->
                                    if (newValue.length <= 12 && newValue.all { it.isDigit() }) {
                                        utrInput = newValue
                                        localValidationError = null
                                        onResetStatus()
                                    }
                                },
                                label = { Text("12-Digit UTR Number ($digitsOnlyCount/12)") },
                                placeholder = { Text("e.g. 428190384712") },
                                leadingIcon = {
                                    Icon(Icons.Default.Pin, contentDescription = null, tint = CyanAccent)
                                },
                                trailingIcon = {
                                    if (isTwelveDigits) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircleOutline,
                                            contentDescription = "Format valid",
                                            tint = EmeraldVerified
                                        )
                                    }
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                                colors = mexcpayTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("utr_number_input")
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Quick fill sample valid 12-digit UTR helper for smooth testing
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedButton(
                                    onClick = { onOpenQrScanner(order.orderId) },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        horizontal = 10.dp,
                                        vertical = 4.dp
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .testTag("utr_open_qr_scanner_button"),
                                    border = BorderStroke(1.dp, ElectricBlue)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.QrCodeScanner,
                                        contentDescription = null,
                                        tint = CyanAccent,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "Scan QR Code",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = CyanAccent
                                    )
                                }

                                OutlinedButton(
                                    onClick = {
                                        val randomUtr = (100_000_000_000L..999_999_999_999L).random().toString()
                                        utrInput = randomUtr
                                        localValidationError = null
                                        onResetStatus()
                                    },
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                                        horizontal = 10.dp,
                                        vertical = 4.dp
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(36.dp)
                                        .testTag("fill_sample_utr_button"),
                                    border = BorderStroke(1.dp, NavyBorder)
                                ) {
                                    Text(
                                        text = "Fill Sample 12-Digit UTR",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = TextSecondary
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            OutlinedTextField(
                                value = payerUpiInput,
                                onValueChange = {
                                    payerUpiInput = it
                                    localValidationError = null
                                },
                                label = { Text("Sender UPI Handle (Sample)") },
                                leadingIcon = {
                                    Icon(Icons.Default.AlternateEmail, contentDescription = null, tint = CyanAccent)
                                },
                                singleLine = true,
                                colors = mexcpayTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("payer_upi_input")
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = remarksInput,
                                onValueChange = { remarksInput = it },
                                label = { Text("Audit Remarks (Optional)") },
                                placeholder = { Text("e.g. Paid from sandbox account") },
                                leadingIcon = {
                                    Icon(Icons.Default.Info, contentDescription = null, tint = CyanAccent)
                                },
                                singleLine = true,
                                colors = mexcpayTextFieldColors(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("utr_remarks_input")
                            )

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    if (!utrInput.trim().matches(Regex("^\\d{12}$"))) {
                                        localValidationError =
                                            "Invalid UTR format: Please enter an exact 12-digit numeric UTR reference."
                                    } else {
                                        localValidationError = null
                                        onSubmitUtr(
                                            order.orderId,
                                            utrInput.trim(),
                                            payerUpiInput.trim(),
                                            remarksInput.trim()
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(54.dp)
                                    .testTag("submit_utr_claim_button"),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ElectricBlue,
                                    contentColor = NavyBackground
                                )
                            ) {
                                Icon(Icons.Default.HourglassTop, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Record UTR Claim (Pending Verification)",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            SampleDataBanner(compact = true)
        }
    }
}
