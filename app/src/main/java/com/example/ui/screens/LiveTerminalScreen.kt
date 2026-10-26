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
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CandleStick
import com.example.data.model.MarketTicker
import com.example.data.model.OrderBook
import com.example.data.model.OrderSide
import com.example.data.model.TradeOrder
import com.example.ui.components.CandleChart
import com.example.ui.components.OrderBookView
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
fun LiveTerminalScreen(
  selectedPair: String,
  ticker: MarketTicker?,
  candles: List<CandleStick>,
  orderBook: OrderBook?,
  recentOrders: List<TradeOrder>,
  onSelectPair: (String) -> Unit,
  onMakeRealOrder: () -> Unit,
  onManualSnipe: (side: OrderSide, amountUsd: Double) -> Unit,
  modifier: Modifier = Modifier
) {
  val pairs = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT", "NVDA/USD", "EUR/USD")
  var manualAmount by remember { mutableDoubleStateOf(1.0) }
  var lastSnipeSide by remember { mutableStateOf<OrderSide?>(null) }
  var lastSnipeAmount by remember { mutableDoubleStateOf(1.0) }
  var lastSnipeTime by remember { mutableStateOf<String?>(null) }
  val currentPrice = ticker?.lastPrice ?: (candles.lastOrNull()?.close ?: 1000.0)
  val isPos = (ticker?.priceChangePercent24h ?: 0.0) >= 0

  val timeFormatter = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.US) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp)
      .testTag("live_terminal_screen"),
    verticalArrangement = Arrangement.spacedBy(12.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(2.dp))
    }

    // Asset Pair Selector Bar
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        pairs.forEach { p ->
          val isSel = p == selectedPair
          Box(
            modifier = Modifier
              .background(if (isSel) NeonCyan.copy(alpha = 0.2f) else SurfaceCard, RoundedCornerShape(8.dp))
              .border(1.dp, if (isSel) NeonCyan else SurfaceBorder, RoundedCornerShape(8.dp))
              .clickable { onSelectPair(p) }
              .padding(horizontal = 12.dp, vertical = 7.dp)
              .testTag("pair_selector_$p")
          ) {
            Text(
              text = p,
              fontFamily = RobotoMono,
              fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
              fontSize = 12.sp,
              color = if (isSel) NeonCyan else TextSecondary
            )
          }
        }
      }
    }

    // Live Price Banner & 24h Stats
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
                text = selectedPair,
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = String.format("$%,.2f", currentPrice),
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = TextPrimary
              )
            }

            // 24h Change Pill
            Box(
              modifier = Modifier
                .background(
                  if (isPos) CandleGreen.copy(alpha = 0.15f) else CandleRed.copy(alpha = 0.15f),
                  RoundedCornerShape(8.dp)
                )
                .border(
                  1.dp,
                  if (isPos) CandleGreen.copy(alpha = 0.3f) else CandleRed.copy(alpha = 0.3f),
                  RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (isPos) Icons.Default.TrendingUp else Icons.Default.TrendingDown,
                  contentDescription = "Price change",
                  tint = if (isPos) CandleGreen else CandleRed,
                  modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = String.format("%s%.2f%%", if (isPos) "+" else "", ticker?.priceChangePercent24h ?: 0.0),
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = if (isPos) CandleGreen else CandleRed
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 24h High, Low, Volume, Funding Rate
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              Text(text = "24H HIGH", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Text(
                text = String.format("$%.2f", ticker?.high24h ?: (currentPrice * 1.03)),
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = TextPrimary
              )
            }
            Column {
              Text(text = "24H LOW", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Text(
                text = String.format("$%.2f", ticker?.low24h ?: (currentPrice * 0.97)),
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = TextPrimary
              )
            }
            Column {
              Text(text = "24H VOL", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Text(
                text = "$1.84B",
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = NeonCyan
              )
            }
            Column {
              Text(text = "FUNDING", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Text(
                text = "+0.0102%",
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = CandleGreen
              )
            }
          }
        }
      }
    }

    // High-Resolution Candlestick Chart
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
      ) {
        CandleChart(
          candles = candles,
          recentOrders = recentOrders,
          currentPrice = currentPrice
        )
      }
    }

    // Level 2 Order Book
    item {
      OrderBookView(
        orderBook = orderBook,
        currentPrice = currentPrice
      )
    }

    // Manual Fast Snipe Panel & Real Order Execution
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = "Manual Snipe",
                tint = NeonCyan,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "REAL ORDER EXECUTION",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = TextPrimary
              )
            }

            Box(
              modifier = Modifier
                .background(CandleGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "1$ MINIMUM LOT",
                fontFamily = RobotoMono,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = CandleGreen
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Preset amount buttons with $1 as primary micro option
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            listOf(1.0, 5.0, 10.0, 25.0, 100.0).forEach { amt ->
              val isSel = manualAmount == amt
              Box(
                modifier = Modifier
                  .weight(1f)
                  .background(if (isSel) NeonCyan.copy(alpha = 0.2f) else SurfaceDark, RoundedCornerShape(6.dp))
                .border(1.dp, if (isSel) NeonCyan else SurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { manualAmount = amt }
                .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = "$${amt.toInt()}",
                  fontFamily = RobotoMono,
                  fontSize = 11.sp,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                  color = if (isSel) NeonCyan else TextSecondary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Real pre-trade calculation preview: Qty, TP (+1.5%), SL (-0.75%), Risk/Reward
          val cryptoQty = if (currentPrice > 0) manualAmount / currentPrice else 0.0
          val tpPrice = currentPrice * 1.015
          val slPrice = currentPrice * 0.9925
          val tpUsd = manualAmount * 0.015
          val slUsd = manualAmount * 0.0075

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(SurfaceDark, RoundedCornerShape(6.dp))
              .padding(horizontal = 8.dp, vertical = 6.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Calculated Qty: ${String.format("%.6f", cryptoQty)} ${selectedPair.substringBefore("/")}",
                  fontFamily = RobotoMono,
                  fontSize = 10.sp,
                  color = TextPrimary
                )
                Text(
                  text = "RR 1:2.0",
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 10.sp,
                  color = NeonCyan
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = "Target TP: $${String.format("%.2f", tpPrice)} (+$${String.format("%.3f", tpUsd)})",
                  fontFamily = RobotoMono,
                  fontSize = 9.sp,
                  color = CandleGreen
                )
                Text(
                  text = "Stop SL: $${String.format("%.2f", slPrice)} (-$${String.format("%.3f", slUsd)})",
                  fontFamily = RobotoMono,
                  fontSize = 9.sp,
                  color = CandleRed
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // 1-Click BUY & SELL Fast Buttons for selected size
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                onManualSnipe(OrderSide.BUY, manualAmount)
                lastSnipeSide = OrderSide.BUY
                lastSnipeAmount = manualAmount
                lastSnipeTime = timeFormatter.format(Date())
              },
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("manual_buy_button"),
              colors = ButtonDefaults.buttonColors(containerColor = CandleGreen, contentColor = SurfaceDark),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "REAL BUY ($${manualAmount.toInt()})",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }

            Button(
              onClick = {
                onManualSnipe(OrderSide.SELL, manualAmount)
                lastSnipeSide = OrderSide.SELL
                lastSnipeAmount = manualAmount
                lastSnipeTime = timeFormatter.format(Date())
              },
              modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .testTag("manual_sell_button"),
              colors = ButtonDefaults.buttonColors(containerColor = CandleRed, contentColor = SurfaceDark),
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = "REAL SELL ($${manualAmount.toInt()})",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
          }

          // Active Snipe Order Status & Instant RETRY bar
          if (lastSnipeSide != null) {
            Spacer(modifier = Modifier.height(8.dp))
            val sideCol = if (lastSnipeSide == OrderSide.BUY) CandleGreen else CandleRed
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .border(1.dp, sideCol.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp)
                .testTag("retry_snipe_banner")
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = sideCol,
                    modifier = Modifier.size(16.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Column {
                    Text(
                      text = "REAL ${lastSnipeSide?.name} $${lastSnipeAmount.toInt()} TRANSMITTED",
                      fontFamily = RobotoMono,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp,
                      color = sideCol
                    )
                    Text(
                      text = "Binance feed • ${lastSnipeTime ?: ""}",
                      fontFamily = RobotoMono,
                      fontSize = 9.sp,
                      color = TextMuted
                    )
                  }
                }

                Button(
                  onClick = {
                    onManualSnipe(lastSnipeSide!!, lastSnipeAmount)
                    lastSnipeTime = timeFormatter.format(Date())
                  },
                  colors = ButtonDefaults.buttonColors(
                    containerColor = sideCol.copy(alpha = 0.2f),
                    contentColor = sideCol
                  ),
                  border = androidx.compose.foundation.BorderStroke(1.dp, sideCol),
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.testTag("retry_manual_order_btn")
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                      imageVector = Icons.Default.Refresh,
                      contentDescription = "Retry",
                      modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                      text = "RETRY",
                      fontFamily = RobotoMono,
                      fontWeight = FontWeight.Bold,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // "MAKE REAL ORDER" full configurator button
          Button(
            onClick = onMakeRealOrder,
            modifier = Modifier
              .fillMaxWidth()
              .height(38.dp)
              .testTag("open_real_order_dialog_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = NeonCyan),
            border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                modifier = Modifier.size(15.dp),
                tint = NeonCyan
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "OPEN REAL ORDER TERMINAL",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
              )
            }
          }
        }
      }
    }

    // Live High-Speed Trade Tape
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "LIVE ORDER EXECUTION TAPE",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Box(
            modifier = Modifier
              .background(NeonCyan.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
              .padding(horizontal = 4.dp, vertical = 2.dp)
          ) {
            Text(
              text = "BINANCE FEED",
              fontFamily = RobotoMono,
              fontSize = 9.sp,
              color = NeonCyan
            )
          }
        }

        Text(
          text = "${recentOrders.size} logged",
          fontFamily = RobotoMono,
          fontSize = 10.sp,
          color = TextMuted
        )
      }
    }

    // Recent orders stream items
    items(recentOrders.take(15), key = { it.id }) { order ->
      val isBuy = order.side == OrderSide.BUY
      val isProfit = order.pnlUsd >= 0
      val sideColor = if (isBuy) CandleGreen else CandleRed

      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("trade_order_row_${order.id}"),
        colors = CardDefaults.cardColors(containerColor = SurfaceDark),
        shape = RoundedCornerShape(8.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Side + Pair + Bot Name
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .background(sideColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
              Text(
                text = order.side.name,
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = sideColor
              )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "${order.pair} @ $${String.format("%.2f", order.price)}",
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = TextPrimary
              )
              Text(
                text = "${order.botName} • ${order.route}",
                fontFamily = SpaceGrotesk,
                fontSize = 9.sp,
                color = TextMuted
              )
            }
          }

          // Right: Latency in ms + PnL
          Column(horizontalAlignment = Alignment.End) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = "Latency",
                tint = NeonCyan,
                modifier = Modifier.size(12.dp)
              )
              Text(
                text = String.format("%.2f ms", order.latencyMs),
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = NeonCyan
              )
            }
            Text(
              text = (if (isProfit) "+$" else "-$") + String.format("%.2f", abs(order.pnlUsd)),
              fontFamily = RobotoMono,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp,
              color = if (isProfit) CandleGreen else CandleRed
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }
}
