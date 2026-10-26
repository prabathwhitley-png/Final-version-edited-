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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.api.ApiConnectionStatus
import com.example.data.api.RealAccountBalance
import com.example.data.model.BotStatus
import com.example.data.model.HftTelemetry
import com.example.data.model.MarketTicker
import com.example.data.model.TradingBot
import com.example.ui.components.BotCard
import com.example.ui.components.CreateBotDialog
import com.example.ui.components.TelemetryBar
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
import kotlin.math.abs

@Composable
fun DashboardScreen(
  bots: List<TradingBot>,
  telemetry: HftTelemetry,
  tickers: Map<String, MarketTicker>,
  apiConnection: ApiConnectionStatus,
  realAccountBalance: RealAccountBalance?,
  isOneDollarMode: Boolean,
  onToggleOneDollarMode: () -> Unit,
  onMakeRealOrder: () -> Unit,
  onOpenApiSettings: () -> Unit,
  onToggleStatus: (TradingBot) -> Unit,
  onDeleteBot: (TradingBot) -> Unit,
  onCreateBot: (
    name: String,
    strategy: com.example.data.model.BotStrategy,
    pair: String,
    capitalUsd: Double,
    leverage: Int,
    speed: com.example.data.model.ExecutionSpeed,
    takeProfit: Double,
    stopLoss: Double
  ) -> Unit,
  onToggleBlitz: () -> Unit,
  onEmergencyKill: () -> Unit,
  onResumeEngine: () -> Unit,
  onSelectPair: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var showCreateDialog by remember { mutableStateOf(false) }

  val totalProfit = bots.sumOf { it.netProfitUsd }
  val totalTrades = bots.sumOf { it.totalTrades }
  val totalWins = bots.sumOf { it.winningTrades }
  val fleetWinRate = if (totalTrades > 0) (totalWins.toDouble() / totalTrades.toDouble()) * 100.0 else 0.0
  val activeCount = bots.count { it.status == BotStatus.RUNNING }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp)
      .testTag("dashboard_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(4.dp))
    }

    // Real API Status Banner + Fast Order Bar
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(12.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
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
            Column {
              Text(
                text = if (apiConnection.isConnected) "REAL BINANCE API CONNECTED" else "CONNECTING TO BINANCE...",
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (apiConnection.isConnected) CandleGreen else NeonAmber
              )
              Text(
                text = if (apiConnection.hasCredentials) {
                  "Spot Account: ${if (realAccountBalance != null) String.format("%.2f USDT", realAccountBalance.usdtFree) else "Verified"}"
                } else {
                  "Public Market Feed • Tap Key to link Real Funds"
                },
                fontFamily = RobotoMono,
                fontSize = 9.sp,
                color = if (apiConnection.hasCredentials) CandleGreen else TextSecondary
              )
            }
          }

          // 1$ Mode Switcher Pill
          Box(
            modifier = Modifier
              .background(
                if (isOneDollarMode) CandleGreen.copy(alpha = 0.2f) else SurfaceDark,
                RoundedCornerShape(6.dp)
              )
              .border(
                1.dp,
                if (isOneDollarMode) CandleGreen else SurfaceBorder,
                RoundedCornerShape(6.dp)
              )
              .clickable { onToggleOneDollarMode() }
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = if (isOneDollarMode) CandleGreen else TextMuted,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(3.dp))
              Text(
                text = if (isOneDollarMode) "1$ MODE ON" else "1$ MODE OFF",
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = if (isOneDollarMode) CandleGreen else TextMuted
              )
            }
          }
        }
      }
    }

    // Top Portfolio / Real Wallet Balance Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("portfolio_overview_card"),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(16.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = if (apiConnection.hasCredentials) "REAL BINANCE SPOT WALLET" else "BINANCE SPOT WALLET",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Medium,
                fontSize = 11.sp,
                color = TextSecondary
              )
              Spacer(modifier = Modifier.height(4.dp))
              if (apiConnection.hasCredentials && realAccountBalance != null) {
                Text(
                  text = String.format("$%,.2f USDT", realAccountBalance.usdtFree),
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 24.sp,
                  color = TextPrimary
                )
                Text(
                  text = "Live On-Exchange Real Funds",
                  fontFamily = SpaceGrotesk,
                  fontSize = 11.sp,
                  color = CandleGreen
                )
              } else {
                Text(
                  text = "API Key Required",
                  fontFamily = SpaceGrotesk,
                  fontWeight = FontWeight.Bold,
                  fontSize = 18.sp,
                  color = NeonAmber
                )
                Text(
                  text = "Link Binance API for live real wallet funds",
                  fontFamily = RobotoMono,
                  fontSize = 10.sp,
                  color = TextMuted
                )
              }
            }

            if (!apiConnection.hasCredentials) {
              Button(
                onClick = onOpenApiSettings,
                colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = NeonCyan),
                border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(imageVector = Icons.Default.Key, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = "LINK API", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            } else {
              Box(
                modifier = Modifier
                  .background(CandleGreen.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                  .border(1.dp, CandleGreen.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = Icons.Default.TrendingUp,
                    contentDescription = "Profit",
                    tint = CandleGreen,
                    modifier = Modifier.size(14.dp)
                  )
                  Spacer(modifier = Modifier.width(4.dp))
                  val isPos = totalProfit >= 0
                  Text(
                    text = (if (isPos) "+$" else "-$") + String.format("%.2f", abs(totalProfit)),
                    fontFamily = RobotoMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isPos) CandleGreen else CandleRed
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3-Column stats: Win Rate, Total HFT Orders, Active Bots
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
              Text(text = "WIN RATE", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = String.format("%.1f%%", fleetWinRate),
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = CandleGreen
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
              modifier = Modifier
                .weight(1f)
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Text(text = "HFT ORDERS", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = String.format("%,d", totalTrades),
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeonCyan
              )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(
              modifier = Modifier
                .weight(1f)
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              Text(text = "ACTIVE BOTS", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "$activeCount / ${bots.size}",
                fontFamily = RobotoMono,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = NeonPurple
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Primary "MAKE REAL ORDER" Button
          Button(
            onClick = onMakeRealOrder,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("dashboard_make_real_order_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = CandleGreen, contentColor = SurfaceDark),
            shape = RoundedCornerShape(8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.FlashOn,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "MAKE REAL ORDER ON BINANCE",
                fontFamily = SpaceGrotesk,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
              )
            }
          }
        }
      }
    }

    // High-Frequency Telemetry Control Bar
    item {
      TelemetryBar(
        telemetry = telemetry,
        onToggleBlitz = onToggleBlitz,
        onEmergencyKill = onEmergencyKill,
        onResume = onResumeEngine
      )
    }

    // Live Streaming Market Tickers (Horizontal carousel)
    item {
      Column {
        Text(
          text = "LIVE BINANCE MARKET FEEDS",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = TextSecondary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          tickers.values.forEach { ticker ->
            val isPos = ticker.priceChangePercent24h >= 0
            Box(
              modifier = Modifier
                .background(SurfaceCard, RoundedCornerShape(10.dp))
                .border(1.dp, SurfaceBorder, RoundedCornerShape(10.dp))
                .clickable { onSelectPair(ticker.pair) }
                .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
              Column {
                Row(
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Text(
                    text = ticker.pair,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = TextPrimary
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = String.format("%s%.2f%%", if (isPos) "+" else "", ticker.priceChangePercent24h),
                    fontFamily = RobotoMono,
                    fontSize = 10.sp,
                    color = if (isPos) CandleGreen else CandleRed
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = String.format("$%,.2f", ticker.lastPrice),
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = TextPrimary
                )
              }
            }
          }
        }
      }
    }

    // Fleet Bots Header + "Deploy Bot" button
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "ALGORITHMIC BOT FLEET",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextPrimary
          )
          Text(
            text = "Automated high-frequency execution engines",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = TextMuted
          )
        }

        Button(
          onClick = { showCreateDialog = true },
          colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = SurfaceDark),
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.testTag("deploy_new_bot_button")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add Bot",
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "DEPLOY",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp
          )
        }
      }
    }

    // Bot Cards
    items(bots, key = { it.id }) { bot ->
      BotCard(
        bot = bot,
        onToggleStatus = { onToggleStatus(bot) },
        onDelete = { onDeleteBot(bot) }
      )
    }

    item {
      Spacer(modifier = Modifier.height(20.dp))
    }
  }

  if (showCreateDialog) {
    CreateBotDialog(
      onDismiss = { showCreateDialog = false },
      onConfirm = { name, strategy, pair, capital, leverage, speed, tp, sl ->
        onCreateBot(name, strategy, pair, capital, leverage, speed, tp, sl)
        showCreateDialog = false
      }
    )
  }
}
