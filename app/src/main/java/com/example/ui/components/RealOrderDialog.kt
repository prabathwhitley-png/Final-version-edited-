package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.ApiConnectionStatus
import com.example.data.model.OrderSide
import com.example.data.model.RiskCalculator
import com.example.data.model.TradeOrder
import com.example.ui.theme.CandleGreen
import com.example.ui.theme.CandleRed
import com.example.ui.theme.NeonAmber
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

@Composable
fun RealOrderDialog(
  currentPair: String,
  livePrice: Double,
  orderBookImbalance: Double,
  apiConnection: ApiConnectionStatus,
  onDismiss: () -> Unit,
  onOpenSettings: () -> Unit,
  onExecuteOrder: (pair: String, side: OrderSide, amountUsd: Double, onResult: (Boolean, String, TradeOrder?) -> Unit) -> Unit
) {
  var selectedPair by remember { mutableStateOf(currentPair) }
  var orderSide by remember { mutableStateOf(OrderSide.BUY) }
  var marginUsd by remember { mutableDoubleStateOf(1.0) }
  var leverage by remember { mutableIntStateOf(10) }
  var tpPercent by remember { mutableDoubleStateOf(1.5) }
  var slPercent by remember { mutableDoubleStateOf(0.75) }

  var isExecuting by remember { mutableStateOf(false) }
  var executedReceipt by remember { mutableStateOf<TradeOrder?>(null) }
  var executionMessage by remember { mutableStateOf<String?>(null) }

  val pairs = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT")
  val presetAmounts = listOf(1.0, 5.0, 10.0, 25.0, 100.0)
  val isBuy = orderSide == OrderSide.BUY
  val sideColor = if (isBuy) CandleGreen else CandleRed

  // Pre-Trade Risk & Market Analysis Calculation
  val calculation = remember(selectedPair, orderSide, livePrice, marginUsd, leverage, tpPercent, slPercent, orderBookImbalance) {
    RiskCalculator.calculate(
      pair = selectedPair,
      side = orderSide,
      entryPrice = livePrice,
      marginUsd = marginUsd,
      leverage = leverage,
      tpPercent = tpPercent,
      slPercent = slPercent,
      orderBookImbalance = orderBookImbalance
    )
  }

  val timeFormat = remember { SimpleDateFormat("HH:mm:ss.SSS", Locale.US) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceCard,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AutoGraph,
            contentDescription = "Analysis",
            tint = NeonCyan,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "REAL MARKET PRE-TRADE ANALYZER",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextPrimary
          )
        }

        IconButton(
          onClick = onOpenSettings,
          modifier = Modifier.size(28.dp).testTag("dialog_api_settings_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Exchange Settings",
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .fillMaxWidth()
      ) {
        // Real Binance Market Live Telemetry Pill
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(8.dp)
                  .clip(CircleShape)
                  .background(if (apiConnection.isConnected) CandleGreen else NeonAmber)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "REAL BINANCE FEED",
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (apiConnection.isConnected) CandleGreen else NeonAmber
              )
            }

            Text(
              text = "${apiConnection.pingMs} ms Ping",
              fontFamily = RobotoMono,
              fontSize = 10.sp,
              color = NeonCyan
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Order Execution Message or Receipt
        if (executedReceipt != null) {
          val receipt = executedReceipt!!
          Card(
            modifier = Modifier.fillMaxWidth().testTag("order_receipt_card"),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(10.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(CandleGreen))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Success",
                    tint = CandleGreen,
                    modifier = Modifier.size(18.dp)
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "REAL ORDER OPENED ON BINANCE",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = CandleGreen
                  )
                }

                // Explicit RETRY button
                Button(
                  onClick = {
                    isExecuting = true
                    onExecuteOrder(selectedPair, orderSide, calculation.positionSizeUsd) { success, message, order ->
                      isExecuting = false
                      executionMessage = message
                      executedReceipt = order
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = CandleGreen.copy(alpha = 0.2f), contentColor = CandleGreen),
                  border = androidx.compose.foundation.BorderStroke(1.dp, CandleGreen),
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.testTag("retry_order_receipt_btn")
                ) {
                  Text(
                    text = "RETRY",
                    fontFamily = RobotoMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                  )
                }
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Order ID: ${receipt.id}",
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = TextPrimary
              )
              Text(
                text = "${receipt.side.name} ${receipt.pair} @ $${String.format("%.2f", receipt.price)}",
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = if (receipt.side == OrderSide.BUY) CandleGreen else CandleRed
              )
              Text(
                text = "Size: $${String.format("%.2f", receipt.totalUsd)} | Qty: ${String.format("%.6f", receipt.amount)}",
                fontFamily = RobotoMono,
                fontSize = 10.sp,
                color = TextSecondary
              )
              Text(
                text = "Route: ${receipt.route} | Time: ${timeFormat.format(Date(receipt.timestamp))}",
                fontFamily = RobotoMono,
                fontSize = 10.sp,
                color = TextMuted
              )
            }
          }
          Spacer(modifier = Modifier.height(10.dp))
        } else if (executionMessage != null) {
          Card(
            modifier = Modifier.fillMaxWidth().testTag("order_warning_card"),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(8.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(NeonAmber))
          ) {
            Column(modifier = Modifier.padding(10.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                  Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(text = executionMessage ?: "", fontFamily = RobotoMono, fontSize = 10.sp, color = NeonAmber)
                }

                Spacer(modifier = Modifier.width(8.dp))
                Button(
                  onClick = {
                    isExecuting = true
                    onExecuteOrder(selectedPair, orderSide, calculation.positionSizeUsd) { success, message, order ->
                      isExecuting = false
                      executionMessage = message
                      executedReceipt = order
                    }
                  },
                  colors = ButtonDefaults.buttonColors(containerColor = NeonAmber.copy(alpha = 0.2f), contentColor = NeonAmber),
                  border = androidx.compose.foundation.BorderStroke(1.dp, NeonAmber),
                  shape = RoundedCornerShape(6.dp),
                  contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                  modifier = Modifier.testTag("retry_order_warning_btn")
                ) {
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
          Spacer(modifier = Modifier.height(10.dp))
        }

        // 1. Asset Pair Selector
        Text(text = "TARGET ASSET PAIR", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          pairs.forEach { p ->
            val isSel = p == selectedPair
            Box(
              modifier = Modifier
                .weight(1f)
                .background(if (isSel) NeonCyan.copy(alpha = 0.2f) else SurfaceDark, RoundedCornerShape(6.dp))
                .border(1.dp, if (isSel) NeonCyan else SurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { selectedPair = p }
                .padding(vertical = 7.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = p.substringBefore("/"),
                fontFamily = RobotoMono,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp,
                color = if (isSel) NeonCyan else TextPrimary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 2. Real Market Analysis Summary Box
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(8.dp),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
        ) {
          Column(modifier = Modifier.padding(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(text = "LIVE MARKET ANALYTICS", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Text(
                text = calculation.marketTrendSignal,
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 9.sp,
                color = if (calculation.marketImbalanceScore >= 0) CandleGreen else CandleRed
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(
                text = "Live Entry: $${String.format("%,.2f", livePrice)}",
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = TextPrimary
              )
              Text(
                text = "OIB: ${String.format("%+.0f%%", orderBookImbalance * 100)}",
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = if (orderBookImbalance >= 0) CandleGreen else CandleRed
              )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = calculation.marketAnalysisSummary,
              fontFamily = SpaceGrotesk,
              fontSize = 10.sp,
              color = TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3. Direction (BUY / LONG vs SELL / SHORT)
        Text(text = "TRADE DIRECTION", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          Box(
            modifier = Modifier
              .weight(1f)
              .background(if (isBuy) CandleGreen.copy(alpha = 0.25f) else SurfaceDark, RoundedCornerShape(8.dp))
              .border(1.dp, if (isBuy) CandleGreen else SurfaceBorder, RoundedCornerShape(8.dp))
              .clickable { orderSide = OrderSide.BUY }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "BUY (LONG)",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = if (isBuy) CandleGreen else TextSecondary
            )
          }

          Box(
            modifier = Modifier
              .weight(1f)
              .background(if (!isBuy) CandleRed.copy(alpha = 0.25f) else SurfaceDark, RoundedCornerShape(8.dp))
              .border(1.dp, if (!isBuy) CandleRed else SurfaceBorder, RoundedCornerShape(8.dp))
              .clickable { orderSide = OrderSide.SELL }
              .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = "SELL (SHORT)",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = if (!isBuy) CandleRed else TextSecondary
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Margin Capital Allocation ($1 micro-trading chips)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "INITIAL MARGIN (USDT)", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextSecondary)
          Text(
            text = "$${String.format("%.2f", marginUsd)}",
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = CandleGreen
          )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          presetAmounts.forEach { amt ->
            val isSel = marginUsd == amt
            Box(
              modifier = Modifier
                .weight(1f)
                .background(if (isSel) sideColor.copy(alpha = 0.25f) else SurfaceDark, RoundedCornerShape(6.dp))
                .border(1.dp, if (isSel) sideColor else SurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { marginUsd = amt }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$${amt.toInt()}",
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                color = if (isSel) sideColor else TextPrimary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 5. Leverage Selector
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "LEVERAGE", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextSecondary)
          Text(
            text = "${leverage}x (Exposure: $${String.format("%.2f", calculation.positionSizeUsd)})",
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = NeonPurple
          )
        }
        Slider(
          value = leverage.toFloat(),
          onValueChange = { leverage = it.toInt() },
          valueRange = 1f..50f,
          steps = 49,
          colors = SliderDefaults.colors(
            thumbColor = NeonPurple,
            activeTrackColor = NeonPurple,
            inactiveTrackColor = SurfaceBorder
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 6. TP & SL Sliders with Live Calculated Target Prices
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "TAKE PROFIT (TP)", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = CandleGreen)
          Text(
            text = "${String.format("%.1f", tpPercent)}% -> $${String.format("%.2f", calculation.tpPrice)} (+$${String.format("%.3f", calculation.expectedProfitUsd)})",
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = CandleGreen
          )
        }
        Slider(
          value = tpPercent.toFloat(),
          onValueChange = { tpPercent = it.toDouble() },
          valueRange = 0.5f..5.0f,
          steps = 45,
          colors = SliderDefaults.colors(
            thumbColor = CandleGreen,
            activeTrackColor = CandleGreen,
            inactiveTrackColor = SurfaceBorder
          )
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(text = "STOP LOSS (SL)", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = CandleRed)
          Text(
            text = "${String.format("%.1f", slPercent)}% -> $${String.format("%.2f", calculation.slPrice)} (-$${String.format("%.3f", calculation.maxLossUsd)})",
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp,
            color = CandleRed
          )
        }
        Slider(
          value = slPercent.toFloat(),
          onValueChange = { slPercent = it.toDouble() },
          valueRange = 0.2f..3.0f,
          steps = 28,
          colors = SliderDefaults.colors(
            thumbColor = CandleRed,
            activeTrackColor = CandleRed,
            inactiveTrackColor = SurfaceBorder
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        // 7. PRE-TRADE CALCULATION CONFIRMATION TABLE
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = SurfaceDark),
          shape = RoundedCornerShape(10.dp),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "PRE-TRADE RISK & QUANTITY BREAKDOWN",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = NeonCyan
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Calculated Quantity
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "Calculated Crypto Qty", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextMuted)
              Text(
                text = "${String.format("%.6f", calculation.quantityCrypto)} ${selectedPair.substringBefore("/")}",
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = TextPrimary
              )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Total Exposure Size
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "Total Position Size", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextMuted)
              Text(
                text = "$${String.format("%.2f", calculation.positionSizeUsd)} (${leverage}x)",
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = TextPrimary
              )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Risk / Reward Ratio
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "Risk / Reward Ratio", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextMuted)
              Text(
                text = "1 : ${String.format("%.1f", calculation.riskRewardRatio)}",
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp,
                color = NeonCyan
              )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Est. Liquidation Price
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "Est. Liquidation Price", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextMuted)
              Text(
                text = "$${String.format("%.2f", calculation.liquidationPrice)}",
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = CandleRed
              )
            }
            Spacer(modifier = Modifier.height(4.dp))

            // Est. Binance Fee
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
              Text(text = "Est. Exchange Taker Fee", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextMuted)
              Text(
                text = "$${String.format("%.4f", calculation.estExchangeFeeUsd)}",
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = TextSecondary
              )
            }
          }
        }
      }
    },
    confirmButton = {
      val hasResult = executedReceipt != null || executionMessage != null
      Button(
        onClick = {
          isExecuting = true
          onExecuteOrder(selectedPair, orderSide, calculation.positionSizeUsd) { success, message, order ->
            isExecuting = false
            executionMessage = message
            executedReceipt = order
          }
        },
        enabled = !isExecuting,
        colors = ButtonDefaults.buttonColors(containerColor = sideColor, contentColor = SurfaceDark),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("confirm_open_trade_btn")
      ) {
        if (isExecuting) {
          CircularProgressIndicator(modifier = Modifier.size(16.dp), color = SurfaceDark, strokeWidth = 2.dp)
          Spacer(modifier = Modifier.width(6.dp))
          Text("TRANSMITTING ORDER TO BINANCE...", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold)
        } else if (hasResult) {
          Icon(imageVector = Icons.Default.Refresh, contentDescription = "Retry", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "RETRY / RE-TRANSMIT ($${marginUsd.toInt()} Margin)",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        } else {
          Icon(imageVector = Icons.Default.Bolt, contentDescription = "Confirm", modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "CONFIRM & OPEN TRADE ($${marginUsd.toInt()} Margin)",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = onDismiss,
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("CLOSE", fontFamily = SpaceGrotesk)
      }
    }
  )
}
