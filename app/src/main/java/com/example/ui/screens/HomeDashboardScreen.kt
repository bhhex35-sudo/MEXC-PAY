package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.CallMade
import androidx.compose.material.icons.automirrored.filled.CallReceived
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.data.model.AdminOrderEntity
import com.example.data.model.BonusSettingEntity
import com.example.data.model.OrderType
import com.example.data.model.UserProfileAccount
import com.example.data.model.WalletTransactionEntity
import com.example.ui.components.AdminOrderCard
import com.example.ui.components.SampleDataBanner
import com.example.ui.components.SampleOriginPill
import com.example.ui.components.formatInr
import com.example.ui.components.formatInrDetailed
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.AmberPending
import com.example.ui.theme.AmberSurface
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
import com.example.ui.theme.NavySurfaceHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.WalletSummaryState

@Composable
fun HomeDashboardScreen(
    userProfile: UserProfileAccount,
    walletSummary: WalletSummaryState,
    orders: List<AdminOrderEntity>,
    transactions: List<WalletTransactionEntity>,
    bonusSettings: List<BonusSettingEntity>,
    onBuyInrClick: () -> Unit,
    onSellInrClick: () -> Unit,
    onViewAllOrders: () -> Unit,
    onOrderClick: (String) -> Unit,
    onNavigateToWallet: () -> Unit,
    onNavigateToSupport: () -> Unit,
    onLockAppNow: () -> Unit = {},
    onOpenQrScanner: () -> Unit = {}
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("home_dashboard_screen"),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Top Header Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(id = R.drawable.img_launcher_icon_1791624424550),
                        contentDescription = "MEXC PAY Icon",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MEXC PAY",
                                style = MaterialTheme.typography.titleLarge,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = ElectricBlue.copy(alpha = 0.22f),
                                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "OTC DESK",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyanAccent,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = userProfile.fullName,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onOpenQrScanner,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NavySurfaceElevated)
                            .testTag("home_qr_scanner_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = "Scan Payment or UTR QR Code",
                            tint = CyanAccent
                        )
                    }
                    IconButton(
                        onClick = onLockAppNow,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NavySurfaceElevated)
                            .testTag("home_biometric_lock_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fingerprint,
                            contentDescription = "Lock App with Biometrics",
                            tint = CyanAccent
                        )
                    }
                    IconButton(
                        onClick = onNavigateToSupport,
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(NavySurfaceElevated)
                            .testTag("home_support_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SupportAgent,
                            contentDescription = "Customer Support",
                            tint = CyanAccent
                        )
                    }
                }
            }
        }

        // 2. Mandatory Sample & Security Notice Banner
        item {
            SampleDataBanner()
        }

        // 3. Wallet Balance Hero Card
        item {
            WalletHeroBalanceCard(
                walletSummary = walletSummary,
                onOpenWallet = onNavigateToWallet
            )
        }

        // 4. BUY INR & SELL INR Action Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OtcQuickActionCard(
                    title = "BUY INR",
                    subtitle = "Up to +4.6% Bonus",
                    description = "Claim admin OTC buy orders & submit UTR",
                    badgeText = "INSTANT QUOTA",
                    accentColor = ElectricBlue,
                    secondaryColor = CyanAccent,
                    isBuy = true,
                    onClick = onBuyInrClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_buy_inr_button")
                )
                OtcQuickActionCard(
                    title = "SELL INR",
                    subtitle = "Up to +2.2% Bonus",
                    description = "Participate in verified INR payout pools",
                    badgeText = "SETTLEMENT POOL",
                    accentColor = CyanAccent,
                    secondaryColor = EmeraldVerified,
                    isBuy = false,
                    onClick = onSellInrClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("home_sell_inr_button")
                )
            }
        }

        // 5. Admin Bonus Settings Strip (Prepared for Admin Workflow)
        if (bonusSettings.isNotEmpty()) {
            item {
                AdminBonusTiersPreviewCard(bonusSettings = bonusSettings)
            }
        }

        // 6. Active Orders Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Active Admin Orders",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Issued by OTC Desk Administrators (Sample)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                TextButton(
                    onClick = onViewAllOrders,
                    modifier = Modifier.testTag("home_view_all_orders_button")
                ) {
                    Text(
                        text = "View All (${orders.size})",
                        color = CyanAccent,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Top 3 Active Orders
        items(
            items = orders.take(3),
            key = { it.orderId }
        ) { order ->
            AdminOrderCard(
                order = order,
                onClick = { onOrderClick(order.orderId) }
            )
        }

        // 7. Recent Transaction History Section Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "Transaction History",
                            style = MaterialTheme.typography.titleLarge,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sample verified ledger vs unverified UTR claims",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }
                TextButton(
                    onClick = onNavigateToWallet,
                    modifier = Modifier.testTag("home_view_ledger_button")
                ) {
                    Text(
                        text = "Full Ledger",
                        color = CyanAccent,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        items(
            items = transactions.take(4),
            key = { it.transactionId }
        ) { tx ->
            TransactionLedgerRowCard(transaction = tx)
        }
    }
}

@Composable
private fun WalletHeroBalanceCard(
    walletSummary: WalletSummaryState,
    onOpenWallet: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onOpenWallet)
            .testTag("home_wallet_balance_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.45f))
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Image(
                painter = painterResource(id = R.drawable.img_fintech_banner_1791624433626),
                contentDescription = null,
                modifier = Modifier
                    .matchParentSize()
                    .alpha(0.32f),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                ElectricBlue.copy(alpha = 0.38f),
                                ElectricBlueDark.copy(alpha = 0.88f),
                                NavySurface.copy(alpha = 0.96f)
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Verified Available Balance (Sample)",
                            style = MaterialTheme.typography.labelLarge,
                            color = TextSecondary
                        )
                    }
                    SampleOriginPill(isSampleData = true)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = formatInrDetailed(walletSummary.sampleVerifiedAvailableInr),
                    style = MaterialTheme.typography.displayMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = NavySurfaceElevated.copy(alpha = 0.85f),
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Sample Bonus Earned",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "+${formatInr(walletSummary.sampleVerifiedBonusEarnedInr)}",
                                style = MaterialTheme.typography.titleMedium,
                                color = EmeraldVerified,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        color = AmberSurface.copy(alpha = 0.75f),
                        border = BorderStroke(1.dp, AmberPending.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "Unverified UTR Claims (${walletSummary.unverifiedClaimsCount})",
                                style = MaterialTheme.typography.bodySmall,
                                color = AmberPending
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = formatInr(walletSummary.unverifiedPendingClaimsInr),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Strict Security: Submitted UTRs are held in Pending Verification and never auto-credit balance.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun OtcQuickActionCard(
    title: String,
    subtitle: String,
    description: String,
    badgeText: String,
    accentColor: Color,
    secondaryColor: Color,
    isBuy: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.55f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            accentColor.copy(alpha = 0.20f),
                            NavySurface
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(accentColor.copy(alpha = 0.25f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBuy) Icons.AutoMirrored.Filled.CallReceived else Icons.AutoMirrored.Filled.CallMade,
                        contentDescription = title,
                        tint = secondaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = NavySurfaceHigh
                ) {
                    Text(
                        text = badgeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = secondaryColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelMedium,
                color = secondaryColor,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                minLines = 2
            )
        }
    }
}

@Composable
private fun AdminBonusTiersPreviewCard(
    bonusSettings: List<BonusSettingEntity>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurfaceElevated),
        border = BorderStroke(1.dp, NavyBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Admin Bonus Schedule (Sample Config)",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
                SampleOriginPill(isSampleData = true)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                bonusSettings.take(3).forEach { tier ->
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = NavySurface,
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = "+${tier.bonusPercentage}%",
                                style = MaterialTheme.typography.titleMedium,
                                color = CyanAccent,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = tier.orderType.replace("_", " "),
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "Min ${formatInr(tier.minAmountInr)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextMuted,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionLedgerRowCard(
    transaction: WalletTransactionEntity,
    modifier: Modifier = Modifier
) {
    val isVerifiedSample = transaction.affectsVerifiedBalance
    val statusColor = if (isVerifiedSample) EmeraldVerified else AmberPending
    val statusBg = if (isVerifiedSample) EmeraldSurface else AmberSurface

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("tx_row_${transaction.transactionId}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(statusBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isVerifiedSample) Icons.Default.VerifiedUser else Icons.Default.HourglassTop,
                    contentDescription = transaction.verificationStatus,
                    tint = statusColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isVerifiedSample) TextSecondary else AmberPending
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    SampleOriginPill(isSampleData = transaction.isSampleData)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "UTR: ${transaction.utrReference}",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = JetBrainsMonoFamily,
                        color = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                val prefix = if (transaction.amountInr >= 0) "+" else ""
                Text(
                    text = "$prefix${formatInr(transaction.amountInr)}",
                    style = MaterialTheme.typography.titleMedium,
                    color = if (!isVerifiedSample) AmberPending else if (transaction.amountInr >= 0) EmeraldVerified else TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isVerifiedSample) "SAMPLE VERIFIED" else "NOT CREDITED",
                    style = MaterialTheme.typography.labelSmall,
                    color = statusColor
                )
                Text(
                    text = formatTimestamp(transaction.timestampEpochMs).substringBefore(","),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextMuted
                )
            }
        }
    }
}
