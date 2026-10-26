package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderSide
import com.example.data.model.TradeOrder
import com.example.ui.theme.CandleGreen
import com.example.ui.theme.CandleRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RobotoMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs

@Composable
fun TradeHistoryScreen(
  orders: List<TradeOrder>,
  onClearHistory: () -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedFilter by remember { mutableStateOf("ALL") }
  val filters = listOf("ALL", "PROFITABLE", "BTC/USDT", "ETH/USDT", "SOL/USDT")
  val timeFormat = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.US) }

  val filteredOrders = remember(orders, selectedFilter) {
    when (selectedFilter) {
      "PROFITABLE" -> orders.filter { it.pnlUsd > 0 }
      "ALL" -> orders
      else -> orders.filter { it.pair == selectedFilter }
    }
  }

  val totalVolume = orders.sumOf { it.totalUsd }
  val avgLatency = if (orders.isNotEmpty()) orders.map { it.latencyMs }.average() else 0.0
  val netPnl = orders.sumOf { it.pnlUsd }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp)
      .testTag("trade_history_screen"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(2.dp))
    }

    // Top Summary Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "EXECUTION AUDIT LOG",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextPrimary
              )
              Text(
                text = "${orders.size} recorded sub-ms fills",
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = TextMuted
              )
            }

            IconButton(
              onClick = onClearHistory,
              modifier = Modifier.testTag("clear_history_button")
            ) {
              Icon(
                imageVector = Icons.Default.DeleteSweep,
                contentDescription = "Clear History",
                tint = TextMuted
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 3 Columns: Total Volume, Average Latency, Net PnL
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(
              modifier = Modifier
                .weight(1f)
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Text(text = "TOTAL TRADED", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Text(
                text = String.format("$%,.0f", totalVolume),
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
              modifier = Modifier
                .weight(1f)
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Text(text = "AVG LATENCY", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Text(
                text = String.format("%.2f ms", avgLatency),
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeonCyan
              )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Column(
              modifier = Modifier
                .weight(1f)
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Text(text = "REALIZED PNL", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              val isPos = netPnl >= 0
              Text(
                text = (if (isPos) "+$" else "-$") + String.format("%.2f", abs(netPnl)),
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isPos) CandleGreen else CandleRed
              )
            }
          }
        }
      }
    }

    // Filter Chips Row
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        filters.forEach { f ->
          val isSel = f == selectedFilter
          Box(
            modifier = Modifier
              .background(if (isSel) NeonCyan.copy(alpha = 0.2f) else SurfaceCard, RoundedCornerShape(8.dp))
              .border(1.dp, if (isSel) NeonCyan else SurfaceBorder, RoundedCornerShape(8.dp))
              .clickable { selectedFilter = f }
              .padding(horizontal = 12.dp, vertical = 6.dp)
          ) {
            Text(
              text = f,
              fontFamily = RobotoMono,
              fontSize = 11.sp,
              fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
              color = if (isSel) NeonCyan else TextSecondary
            )
          }
        }
      }
    }

    // Orders List
    if (filteredOrders.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
              imageVector = Icons.Default.FilterList,
              contentDescription = "Empty",
              tint = TextMuted,
              modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No trade executions match this filter",
              fontFamily = SpaceGrotesk,
              color = TextMuted,
              fontSize = 13.sp
            )
          }
        }
      }
    } else {
      items(filteredOrders, key = { it.id }) { order ->
        val isBuy = order.side == OrderSide.BUY
        val isProfit = order.pnlUsd >= 0
        val sideColor = if (isBuy) CandleGreen else CandleRed

        Card(
          modifier = Modifier
            .fillMaxWidth()
            .testTag("history_item_${order.id}"),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          shape = RoundedCornerShape(10.dp),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .background(sideColor.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = order.side.name,
                    fontFamily = RobotoMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = sideColor
                  )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = order.pair,
                  fontFamily = SpaceGrotesk,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = TextPrimary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "@ $${String.format("%.2f", order.price)}",
                  fontFamily = RobotoMono,
                  fontSize = 12.sp,
                  color = TextSecondary
                )
              }

              // PnL badge
              Text(
                text = (if (isProfit) "+$" else "-$") + String.format("%.2f", abs(order.pnlUsd)),
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (isProfit) CandleGreen else CandleRed
              )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Sub row: Bot Name, Route, Latency, Timestamp
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "${order.botName} • ${order.route}",
                fontFamily = SpaceGrotesk,
                fontSize = 10.sp,
                color = TextMuted
              )

              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Bolt,
                  contentDescription = "Latency",
                  tint = NeonCyan,
                  modifier = Modifier.size(11.dp)
                )
                Text(
                  text = "${String.format("%.2f", order.latencyMs)}ms • ${timeFormat.format(Date(order.timestamp))}",
                  fontFamily = RobotoMono,
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
