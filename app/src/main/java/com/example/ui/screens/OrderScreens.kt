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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.model.AdminOrderEntity
import com.example.data.model.OrderType
import com.example.data.model.PaymentSubmissionEntity
import com.example.ui.components.AdminOrderCard
import com.example.ui.components.OrderStatusChip
import com.example.ui.components.OrderTypeBadge
import com.example.ui.components.SampleDataBanner
import com.example.ui.components.SampleOriginPill
import com.example.ui.components.formatInr
import com.example.ui.components.formatInrDetailed
import com.example.ui.components.formatTimestamp
import com.example.ui.theme.AmberPending
import com.example.ui.theme.AmberSurface
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
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

@Composable
fun ActiveOrdersScreen(
    orders: List<AdminOrderEntity>,
    selectedFilter: OrderType?,
    searchQuery: String,
    onFilterChange: (OrderType?) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onOrderClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("active_orders_screen")
    ) {
        // Header & Filter Controls
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavySurface)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Active Admin Orders",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Administrator-issued INR OTC liquidity batches",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
                SampleOriginPill(isSampleData = true)
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Search Order ID, Desk, or Batch Code...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = CyanAccent)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search", tint = TextSecondary)
                        }
                    }
                },
                singleLine = true,
                colors = mexcpayTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("orders_search_input")
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { onFilterChange(null) },
                    label = { Text("All Desks") },
                    colors = filterChipColors(),
                    modifier = Modifier.testTag("filter_all_chip")
                )
                FilterChip(
                    selected = selectedFilter == OrderType.BUY_INR,
                    onClick = { onFilterChange(OrderType.BUY_INR) },
                    label = { Text("BUY INR") },
                    colors = filterChipColors(),
                    modifier = Modifier.testTag("filter_buy_inr_chip")
                )
                FilterChip(
                    selected = selectedFilter == OrderType.SELL_INR,
                    onClick = { onFilterChange(OrderType.SELL_INR) },
                    label = { Text("SELL INR") },
                    colors = filterChipColors(),
                    modifier = Modifier.testTag("filter_sell_inr_chip")
                )
            }
        }

        HorizontalDivider(color = NavyBorder)

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SampleDataBanner(compact = true)
            }

            if (orders.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = NavySurface),
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(28.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No matching admin orders found",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Try clearing your search filter or switching between BUY INR and SELL INR desks.",
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary
                            )
                        }
                    }
                }
            } else {
                items(
                    items = orders,
                    key = { it.orderId }
                ) { order ->
                    AdminOrderCard(
                        order = order,
                        onClick = { onOrderClick(order.orderId) }
                    )
                }
            }
        }
    }
}

@Composable
private fun filterChipColors() = FilterChipDefaults.filterChipColors(
    containerColor = NavySurfaceElevated,
    labelColor = TextSecondary,
    selectedContainerColor = ElectricBlue,
    selectedLabelColor = NavyBackground
)

@Composable
fun OrderDetailsScreen(
    order: AdminOrderEntity?,
    submissionsForOrder: List<PaymentSubmissionEntity>,
    onBack: () -> Unit,
    onProceedToPaymentInstructions: (String) -> Unit,
    onProceedToUtrSubmission: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyBackground)
            .testTag("order_details_screen")
    ) {
        // Top Bar
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
                    modifier = Modifier.testTag("order_details_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order Details",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order?.orderId ?: "Loading order...",
                        style = MaterialTheme.typography.labelSmall,
                        fontFamily = JetBrainsMonoFamily,
                        color = CyanAccent
                    )
                }
                if (order != null) {
                    SampleOriginPill(isSampleData = order.isSampleData)
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        if (order == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Order not found in sandbox database.",
                    color = TextSecondary
                )
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
            SampleDataBanner()

            // Primary Order Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = NavySurface),
                border = BorderStroke(1.dp, CyanAccent.copy(alpha = 0.45f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OrderTypeBadge(orderType = order.parsedOrderType)
                        OrderStatusChip(status = order.parsedStatus)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Order Ticket Principal",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = formatInrDetailed(order.inrAmount),
                        style = MaterialTheme.typography.displayMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Breakdown grid
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = NavySurfaceElevated,
                        border = BorderStroke(1.dp, NavyBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            DetailRow(
                                label = "Order ID",
                                value = order.orderId,
                                isMono = true
                            )
                            DetailRow(
                                label = "Bonus Percentage",
                                value = "+${order.bonusPercentage}%",
                                valueColor = CyanAccent
                            )
                            DetailRow(
                                label = "Bonus Reward (Upon Backend Audit)",
                                value = "+${formatInrDetailed(order.bonusInrAmount)}",
                                valueColor = EmeraldVerified
                            )
                            HorizontalDivider(color = NavyBorder)
                            DetailRow(
                                label = "Total Expected Settlement Value",
                                value = formatInrDetailed(order.totalReceivableInr),
                                valueColor = TextPrimary,
                                isBold = true
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Availability Quota bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Available Batch Quota",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                        Text(
                            text = "${formatInr(order.availableQuotaInr)} / ${formatInr(order.totalQuotaInr)}",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanAccent
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { order.availabilityPercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ElectricBlue,
                        trackColor = NavySurfaceHigh
                    )
                }
            }

            // Administrator Workflow Metadata Card
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
                            text = "Administrator Issuance Metadata",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    HorizontalDivider(color = NavyBorder)
                    DetailRow(label = "Created By Admin ID", value = order.createdByAdminId, isMono = true)
                    DetailRow(label = "Issuing OTC Desk", value = order.adminDeskName)
                    DetailRow(label = "Batch Reference Code", value = order.adminBatchCode, isMono = true)
                    DetailRow(label = "Placeholder UPI Handle", value = order.assignedPlaceholderUpiId, isMono = true)
                    DetailRow(
                        label = "Settlement Window",
                        value = "${order.settlementWindowMinutes} Minutes"
                    )
                    DetailRow(
                        label = "Issued Timestamp",
                        value = formatTimestamp(order.createdAtEpochMs)
                    )
                    DetailRow(
                        label = "Data Classification",
                        value = if (order.isSampleData) "SAMPLE SANDBOX ORDER" else "LIVE ORDER",
                        valueColor = CyanAccent
                    )
                }
            }

            // Existing UTR Submissions for this Order
            if (submissionsForOrder.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = AmberSurface.copy(alpha = 0.45f)),
                    border = BorderStroke(1.dp, AmberPending.copy(alpha = 0.6f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = AmberPending,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Recorded UTR Submissions (${submissionsForOrder.size})",
                                style = MaterialTheme.typography.titleSmall,
                                color = AmberPending,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        submissionsForOrder.forEach { sub ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = NavySurface,
                                border = BorderStroke(1.dp, NavyBorder)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "UTR: ${sub.submittedUtrNumber}",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontFamily = JetBrainsMonoFamily,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = sub.parsedVerificationState.displayLabel,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = AmberPending,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = sub.adminAuditNote,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Action Buttons: Payment Instructions & UTR Submission
            Button(
                onClick = { onProceedToPaymentInstructions(order.orderId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp)
                    .testTag("view_payment_instructions_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricBlue,
                    contentColor = NavyBackground
                )
            ) {
                Icon(Icons.Default.QrCode2, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "View Payment Instructions (Sample UPI & QR)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = { onProceedToUtrSubmission(order.orderId) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("direct_submit_utr_button"),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, CyanAccent)
            ) {
                Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = CyanAccent)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Submit 12-Digit UTR for Verification",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold
                )
            }

            // Security Footnote
            Surface(
                shape = RoundedCornerShape(12.dp),
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
                        text = "Security Guarantee: MEXC PAY does not perform fake client-side payment verification. Balances are only updated by authorized backend settlement records.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = TextPrimary,
    isMono: Boolean = false,
    isBold: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            modifier = Modifier.weight(0.45f)
        )
        Text(
            text = value,
            style = if (isMono) MaterialTheme.typography.labelMedium else MaterialTheme.typography.bodyMedium,
            fontFamily = if (isMono) JetBrainsMonoFamily else MaterialTheme.typography.bodyMedium.fontFamily,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = valueColor
        )
    }
}
