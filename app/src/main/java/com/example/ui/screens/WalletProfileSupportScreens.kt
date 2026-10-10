package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.example.ui.security.BiometricAuthHelper
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.BonusSettingEntity
import com.example.data.model.PaymentSubmissionEntity
import com.example.data.model.UserProfileAccount
import com.example.data.model.WalletTransactionEntity
import com.example.ui.components.SampleDataBanner
import com.example.ui.components.SampleOriginPill
import com.example.ui.components.formatInr
import com.example.ui.components.formatInrDetailed
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.AmberPending
import com.example.ui.theme.AmberSurface
import com.example.ui.theme.CoralAlert
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueDark
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NavyBackground
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.WalletSummaryState

@Composable
fun WalletScreen(
    walletSummary: WalletSummaryState,
    transactions: List<WalletTransactionEntity>,
    submissions: List<PaymentSubmissionEntity>,
    onBrowseOrders: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) } // 0 = All, 1 = Sample Verified, 2 = Unverified Claims

    val filteredTransactions = when (selectedTab) {
        1 -> transactions.filter { it.affectsVerifiedBalance }
        2 -> transactions.filter { !it.affectsVerifiedBalance }
        else -> transactions
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("wallet_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "OTC Settlement Wallet",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Separated Sample Verified Balance & Unverified UTR Queue",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                SampleOriginPill(isSampleData = true)
            }
        }

        item {
            SampleDataBanner(compact = true)
        }

        // Balance Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    ElectricBlueDark.copy(alpha = 0.65f),
                                    NavySurface
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Available Verified Balance (Sample Record)",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formatInrDetailed(walletSummary.sampleVerifiedAvailableInr),
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("wallet_available_balance_text")
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = EmeraldSurface.copy(alpha = 0.6f),
                            border = BorderStroke(1.dp, EmeraldVerified.copy(alpha = 0.45f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Cumulative Bonus (Sample)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = EmeraldVerified
                                )
                                Text(
                                    text = "+${formatInrDetailed(walletSummary.sampleVerifiedBonusEarnedInr)}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            color = AmberSurface.copy(alpha = 0.65f),
                            border = BorderStroke(1.dp, AmberPending.copy(alpha = 0.55f))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Unverified Claims (${walletSummary.unverifiedClaimsCount})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = AmberPending
                                )
                                Text(
                                    text = formatInrDetailed(walletSummary.unverifiedPendingClaimsInr),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.testTag("wallet_unverified_claims_text")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onBrowseOrders,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("wallet_browse_orders_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = NavyBackground
                        )
                    ) {
                        Text(
                            text = "Browse Active BUY / SELL INR Orders",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Unverified UTR Submissions Queue Section
        if (submissions.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
                    border = BorderStroke(1.dp, NavyBorder)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HourglassTop,
                                    contentDescription = null,
                                    tint = AmberPending,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "UTR Verification Queue (${submissions.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        submissions.take(3).forEach { sub ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = NavySurface,
                                border = BorderStroke(1.dp, NavyBorder)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "Order ${sub.orderId} • UTR ${sub.submittedUtrNumber}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontFamily = JetBrainsMonoFamily,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = sub.parsedVerificationState.displayLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = if (sub.isSampleData && sub.verificationState == "BACKEND_VERIFIED") EmeraldVerified else AmberPending
                                        )
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = formatInr(sub.claimedInrAmount),
                                            style = MaterialTheme.typography.titleSmall,
                                            color = TextPrimary,
                                            fontWeight = FontWeight.Bold
                                        )
                                        SampleOriginPill(isSampleData = sub.isSampleData)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Filter Chips for Ledger
        item {
            Column {
                Text(
                    text = "Wallet Ledger & Audit Trail",
                    style = MaterialTheme.typography.titleLarge,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        label = { Text("All Entries (${transactions.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = NavyBackground,
                            containerColor = NavySurfaceElevated,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("wallet_tab_all")
                    )
                    FilterChip(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        label = { Text("Sample Verified") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = NavyBackground,
                            containerColor = NavySurfaceElevated,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("wallet_tab_verified")
                    )
                    FilterChip(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        label = { Text("Unverified UTR") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricBlue,
                            selectedLabelColor = NavyBackground,
                            containerColor = NavySurfaceElevated,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.testTag("wallet_tab_unverified")
                    )
                }
            }
        }

        items(
            items = filteredTransactions,
            key = { it.transactionId }
        ) { tx ->
            TransactionLedgerRowCard(transaction = tx)
        }
    }
}

@Composable
fun ProfileScreen(
    userProfile: UserProfileAccount,
    bonusSettings: List<BonusSettingEntity>,
    biometricAppLockEnabled: Boolean,
    onToggleBiometricAppLock: (Boolean) -> Unit,
    onLockSessionNow: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onResetSandboxData: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val hardwareStatus = remember(context) { BiometricAuthHelper.checkBiometricSupport(context) }
    var resetFeedback by rememberSaveable { mutableStateOf(false) }
    var biometricTestResult by rememberSaveable { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("profile_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "OTC Participant Profile",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Account Configuration & Admin Workflow Readiness",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
            SampleOriginPill(isSampleData = true)
        }

        // User Identity Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.45f))
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(ElectricBlue.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = userProfile.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = userProfile.kycTierLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldVerified
                        )
                    }
                }

                HorizontalDivider(color = NavyBorder)

                DetailRow(label = "Participant ID", value = userProfile.userId, isMono = true)
                DetailRow(label = "Registered Email", value = userProfile.email)
                DetailRow(label = "Mobile Number", value = userProfile.phoneNumber)
                DetailRow(
                    label = "Placeholder Settlement UPI",
                    value = userProfile.linkedSettlementUpi,
                    isMono = true
                )
                DetailRow(
                    label = "Environment Mode",
                    value = userProfile.accountEnvironment,
                    valueColor = CyanAccent,
                    isMono = true
                )
            }
        }

        // Biometric Authentication (Fingerprint / Face Unlock) Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("profile_biometric_security_card"),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, ElectricBlue.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Biometric App Lock",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Fingerprint & Face Unlock Session Guard",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                    Switch(
                        checked = biometricAppLockEnabled,
                        onCheckedChange = onToggleBiometricAppLock,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NavyBackground,
                            checkedTrackColor = ElectricBlue,
                            uncheckedThumbColor = TextSecondary,
                            uncheckedTrackColor = NavySurfaceElevated
                        ),
                        modifier = Modifier.testTag("profile_biometric_switch")
                    )
                }

                HorizontalDivider(color = NavyBorder)

                DetailRow(
                    label = "Device Sensor Status",
                    value = if (hardwareStatus.isReady) "Active & Enrolled" else "Not Enrolled / Sandbox Mode",
                    valueColor = if (hardwareStatus.isReady) EmeraldVerified else AmberPending
                )

                if (biometricTestResult != null) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurfaceElevated,
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Text(
                            text = biometricTestResult!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = CyanAccent,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            BiometricAuthHelper.authenticate(
                                context = context,
                                title = "Verify Biometric Sensor",
                                subtitle = "Test Fingerprint or Face Unlock on your device",
                                onSuccess = {
                                    biometricTestResult = "✓ Biometric verification succeeded!"
                                },
                                onError = { err, _ ->
                                    biometricTestResult = "Biometric prompt result: $err"
                                }
                            )
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("profile_test_biometric_button"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, ElectricBlue)
                    ) {
                        Text(
                            text = "Test Prompt",
                            style = MaterialTheme.typography.labelLarge,
                            color = CyanAccent
                        )
                    }

                    Button(
                        onClick = onLockSessionNow,
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("profile_lock_now_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = NavyBackground
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Lock Desk Now",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Admin Workflow & Bonus Settings Table
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Admin Bonus Settings Model (Sample)",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Prepared for administrator configuration of BUY/SELL INR bonus tiers.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                HorizontalDivider(color = NavyBorder)
                bonusSettings.forEach { tier ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurfaceElevated,
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${tier.tierName} (${tier.orderType.replace("_", " ")})",
                                    style = MaterialTheme.typography.titleSmall,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "Range: ${formatInr(tier.minAmountInr)} – ${formatInr(tier.maxAmountInr)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            Text(
                                text = "+${tier.bonusPercentage}%",
                                style = MaterialTheme.typography.titleMedium,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Security Architecture Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = NavySurface),
            border = BorderStroke(1.dp, NavyBorder)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = EmeraldVerified,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Security & Verification Compliance",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "• Zero Fake Verification: UTR submissions are never auto-verified on device.\n" +
                        "• Zero Auto-Credit: Wallet balances cannot be incremented by UTR input or button clicks.\n" +
                        "• Zero Hardcoded Secrets: No live API keys or real merchant UPI credentials are stored in the APK.",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }
        }

        // Actions: Customer Support, Reset Sandbox Data, Logout
        Button(
            onClick = onNavigateToSupport,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("profile_open_support_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ElectricBlue,
                contentColor = NavyBackground
            )
        ) {
            Icon(Icons.Default.SupportAgent, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Open Customer Support & UTR Audit Help",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedButton(
            onClick = {
                onResetSandboxData()
                resetFeedback = true
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("reset_sandbox_data_button"),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.6f))
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, tint = CyanAccent)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Reset Sample Sandbox Orders & Claims",
                color = CyanAccent,
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (resetFeedback) {
            Text(
                text = "✓ Sandbox sample orders and ledger reset to initial state.",
                style = MaterialTheme.typography.labelSmall,
                color = EmeraldVerified
            )
        }

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("profile_logout_button"),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, CoralAlert.copy(alpha = 0.6f))
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = CoralAlert)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign Out of Sandbox Desk",
                color = CoralAlert,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun CustomerSupportScreen(
    onBack: () -> Unit
) {
    var orderIdQuery by rememberSaveable { mutableStateOf("") }
    var issueSummary by rememberSaveable { mutableStateOf("") }
    var ticketRecorded by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("customer_support_screen")
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
                    modifier = Modifier.testTag("support_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Customer Support & Reconciliation Desk",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "UTR Verification & OTC Order Escalation",
                        style = MaterialTheme.typography.bodySmall,
                        color = CyanAccent
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SampleDataBanner(compact = true)

            // Support Channels Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.45f))
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "OTC Settlement Support Channels (Sample)",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(color = NavyBorder)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Email, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Reconciliation Desk Email (Sample)", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("otc-audit.sample@mexcpay.in", style = MaterialTheme.typography.labelMedium, fontFamily = JetBrainsMonoFamily, color = TextPrimary)
                        }
                    }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = CyanAccent, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Operating Hours", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Text("24x7 Automated Webhook + Admin Audit Desk (09:00–23:00 IST)", style = MaterialTheme.typography.bodySmall, color = TextPrimary)
                        }
                    }
                }
            }

            // Frequently Asked Questions on UTR Verification
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "UTR Verification & Security FAQ",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(color = NavyBorder)
                    FaqItem(
                        question = "Why doesn't my wallet balance increase immediately after submitting a UTR?",
                        answer = "For strict financial security, MEXC PAY never credits wallet balances based on client-side UTR input alone. Every UTR remains in PENDING BACKEND VERIFICATION until an authorized backend reconciles the bank statement."
                    )
                    FaqItem(
                        question = "What format is required for UTR submission?",
                        answer = "NPCI UPI transactions generate an exact 12-digit numeric Unique Transaction Reference (UTR). Submissions shorter or longer than 12 digits or containing letters are rejected by input validation."
                    )
                    FaqItem(
                        question = "Are the UPI IDs and QR codes shown in this app real?",
                        answer = "No. Until an authorized production backend is connected, all UPI handles, QR graphics, and orders are clearly labeled sample placeholders."
                    )
                }
            }

            // Log Local Support Inquiry
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, NavyBorder)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Log Sample Reconciliation Inquiry",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    OutlinedTextField(
                        value = orderIdQuery,
                        onValueChange = {
                            orderIdQuery = it
                            ticketRecorded = false
                        },
                        label = { Text("Order ID or 12-Digit UTR") },
                        placeholder = { Text("e.g. MXP-ORD-9041") },
                        singleLine = true,
                        colors = mexcpayTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("support_order_id_input")
                    )

                    OutlinedTextField(
                        value = issueSummary,
                        onValueChange = {
                            issueSummary = it
                            ticketRecorded = false
                        },
                        label = { Text("Inquiry Details") },
                        placeholder = { Text("Describe your UTR audit question...") },
                        minLines = 3,
                        colors = mexcpayTextFieldColors(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("support_issue_input")
                    )

                    Button(
                        onClick = {
                            if (orderIdQuery.isNotBlank() || issueSummary.isNotBlank()) {
                                ticketRecorded = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("support_submit_inquiry_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ElectricBlue,
                            contentColor = NavyBackground
                        )
                    ) {
                        Text(
                            text = "Record Sample Support Inquiry",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (ticketRecorded) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = EmeraldSurface,
                            border = BorderStroke(1.dp, EmeraldVerified)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = EmeraldVerified,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Sample inquiry logged locally for sandbox demonstration.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FaqItem(
    question: String,
    answer: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = question,
            style = MaterialTheme.typography.titleSmall,
            color = CyanAccent,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = answer,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
    }
}
