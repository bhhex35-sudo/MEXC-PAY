package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.GppMaybe
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.data.model.AdminOrderEntity
import com.example.data.model.OrderStatus
import com.example.data.model.OrderType
import com.example.ui.theme.AmberPending
import com.example.ui.theme.AmberSurface
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricBlueBright
import com.example.ui.theme.EmeraldSurface
import com.example.ui.theme.EmeraldVerified
import com.example.ui.theme.JetBrainsMonoFamily
import com.example.ui.theme.NavyBorder
import com.example.ui.theme.NavySurface
import com.example.ui.theme.NavySurfaceElevated
import com.example.ui.theme.NavySurfaceHigh
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatInr(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    formatter.maximumFractionDigits = 0
    return formatter.format(amount)
}

fun formatInrDetailed(amount: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    formatter.minimumFractionDigits = 2
    formatter.maximumFractionDigits = 2
    return formatter.format(amount)
}

fun formatTimestamp(epochMs: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.US)
    return sdf.format(Date(epochMs))
}

@Composable
fun SampleDataBanner(
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("sample_data_security_banner"),
        shape = RoundedCornerShape(16.dp),
        color = NavySurfaceElevated,
        border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.35f))
    ) {
        Row(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            ElectricBlue.copy(alpha = 0.16f),
                            NavySurfaceElevated
                        )
                    )
                )
                .padding(horizontal = 14.dp, vertical = if (compact) 10.dp else 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(ElectricBlueBright.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = "Sample Sandbox Mode",
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SAMPLE DATA • SANDBOX PREVIEW",
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanAccent,
                    fontWeight = FontWeight.Bold
                )
                if (!compact) {
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Admin orders, placeholder UPI IDs, and wallet records are sample data. Submitting a UTR never credits balances without backend bank verification.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun SampleOriginPill(
    isSampleData: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSampleData) NavySurfaceHigh else AmberSurface
    val borderColor = if (isSampleData) ElectricBlueBright.copy(alpha = 0.45f) else AmberPending.copy(alpha = 0.55f)
    val textColor = if (isSampleData) CyanAccent else AmberPending
    val text = if (isSampleData) "SAMPLE DATA" else "UNVERIFIED UTR CLAIM"

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = BorderStroke(1.dp, borderColor)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = textColor,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
        )
    }
}

@Composable
fun OrderTypeBadge(orderType: OrderType) {
    val isBuy = orderType == OrderType.BUY_INR
    val bg = if (isBuy) ElectricBlue.copy(alpha = 0.22f) else EmeraldSurface
    val border = if (isBuy) ElectricBlueBright.copy(alpha = 0.55f) else EmeraldVerified.copy(alpha = 0.5f)
    val tint = if (isBuy) CyanAccent else EmeraldVerified

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Text(
            text = if (isBuy) "BUY INR" else "SELL INR",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = tint,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun OrderStatusChip(status: OrderStatus) {
    val (bgColor, textColor, icon) = when (status) {
        OrderStatus.OPEN_AVAILABLE -> Triple(EmeraldSurface, EmeraldVerified, Icons.Default.VerifiedUser)
        OrderStatus.AWAITING_UTR -> Triple(AmberSurface, AmberPending, Icons.Default.HourglassTop)
        OrderStatus.UTR_SUBMITTED_PENDING_ADMIN -> Triple(AmberSurface, AmberPending, Icons.Default.GppMaybe)
        OrderStatus.VERIFIED_SETTLED -> Triple(NavySurfaceHigh, CyanAccent, Icons.Default.VerifiedUser)
        OrderStatus.EXPIRED_OR_CLOSED -> Triple(NavySurfaceHigh, TextMuted, Icons.Default.Info)
    }

    Surface(
        shape = RoundedCornerShape(8.dp),
        color = bgColor,
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = status.label,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.label,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun AdminOrderCard(
    order: AdminOrderEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag("order_card_${order.orderId}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = NavySurface),
        border = BorderStroke(1.dp, NavyBorder.copy(alpha = 0.85f))
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            NavySurfaceElevated.copy(alpha = 0.9f),
                            NavySurface
                        )
                    )
                )
                .padding(18.dp)
        ) {
            // Top Row: Order ID, Type Badge, and Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OrderTypeBadge(orderType = order.parsedOrderType)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.orderId,
                        style = MaterialTheme.typography.labelMedium,
                        fontFamily = JetBrainsMonoFamily,
                        color = TextPrimary
                    )
                }
                OrderStatusChip(status = order.parsedStatus)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Middle Row: Principal Amount & Bonus Percentage
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    Text(
                        text = "Order Ticket Amount",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatInr(order.inrAmount),
                        style = MaterialTheme.typography.headlineMedium,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = ElectricBlue.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, ElectricBlueBright.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "+${order.bonusPercentage}% BONUS",
                            style = MaterialTheme.typography.labelMedium,
                            color = CyanAccent,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "+${formatInr(order.bonusInrAmount)} Reward",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quota & Availability Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pool Availability: ${formatInr(order.availableQuotaInr)} / ${formatInr(order.totalQuotaInr)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                    Text(
                        text = "${(order.availabilityPercentage * 100).toInt()}% Open",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanAccent
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { order.availabilityPercentage },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ElectricBlueBright,
                    trackColor = NavySurfaceHigh
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer: Admin Desk Metadata + Sample Badge + Action Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    SampleOriginPill(isSampleData = order.isSampleData)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.adminDeskName,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Details",
                        style = MaterialTheme.typography.labelLarge,
                        color = CyanAccent
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open order details",
                        tint = CyanAccent,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
