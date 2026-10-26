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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BotStatus
import com.example.data.model.TradingBot
import com.example.ui.theme.CandleGreen
import com.example.ui.theme.CandleRed
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.NeonRed
import com.example.ui.theme.RobotoMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun BotCard(
  bot: TradingBot,
  onToggleStatus: () -> Unit,
  onDelete: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isRunning = bot.status == BotStatus.RUNNING
  val isProfitable = bot.netProfitUsd >= 0
  val pnlColor = if (isProfitable) CandleGreen else CandleRed

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("bot_card_${bot.id}"),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    shape = RoundedCornerShape(14.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header: Name + Status Pill + Quick Controls
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = bot.name,
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            // Strategy tag
            Box(
              modifier = Modifier
                .background(SurfaceDark, RoundedCornerShape(4.dp))
                .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = bot.strategy.displayName,
                fontFamily = RobotoMono,
                fontSize = 9.sp,
                color = NeonCyan
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
            // Pair & Leverage
            Text(
              text = "${bot.pair} • ${bot.leverage}x",
              fontFamily = RobotoMono,
              fontSize = 11.sp,
              color = TextSecondary
            )
          }
        }

        // Status Pill & Action Buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
          // Status indicator
          Box(
            modifier = Modifier
              .background(
                when (bot.status) {
                  BotStatus.RUNNING -> CandleGreen.copy(alpha = 0.15f)
                  BotStatus.PAUSED -> NeonAmber.copy(alpha = 0.15f)
                  BotStatus.STOPPED -> NeonRed.copy(alpha = 0.15f)
                },
                RoundedCornerShape(12.dp)
              )
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(6.dp)
                  .clip(CircleShape)
                  .background(
                    when (bot.status) {
                      BotStatus.RUNNING -> CandleGreen
                      BotStatus.PAUSED -> NeonAmber
                      BotStatus.STOPPED -> NeonRed
                    }
                  )
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = bot.status.name,
                fontFamily = RobotoMono,
                fontSize = 9.sp,
                color = when (bot.status) {
                  BotStatus.RUNNING -> CandleGreen
                  BotStatus.PAUSED -> NeonAmber
                  BotStatus.STOPPED -> NeonRed
                }
              )
            }
          }

          Spacer(modifier = Modifier.width(4.dp))

          // Toggle Run / Pause
          IconButton(
            onClick = onToggleStatus,
            modifier = Modifier
              .size(34.dp)
              .testTag("bot_toggle_status_${bot.id}")
          ) {
            Icon(
              imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
              contentDescription = if (isRunning) "Pause Bot" else "Start Bot",
              tint = if (isRunning) NeonAmber else CandleGreen,
              modifier = Modifier.size(20.dp)
            )
          }

          // Delete
          IconButton(
            onClick = onDelete,
            modifier = Modifier
              .size(34.dp)
              .testTag("bot_delete_${bot.id}")
          ) {
            Icon(
              imageVector = Icons.Default.DeleteOutline,
              contentDescription = "Delete Bot",
              tint = TextMuted,
              modifier = Modifier.size(18.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Financial Metrics: PnL + Capital + Win Rate + Latency
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Net PnL
        Column {
          Text(text = "NET PNL", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = (if (isProfitable) "+$" else "-$") + String.format("%.2f", kotlin.math.abs(bot.netProfitUsd)),
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = pnlColor
          )
          Text(
            text = String.format("%s%.2f%%", if (isProfitable) "+" else "", bot.profitPercent),
            fontFamily = RobotoMono,
            fontSize = 10.sp,
            color = pnlColor
          )
        }

        // Win Rate
        Column {
          Text(text = "WIN RATE", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = String.format("%.1f%%", bot.winRatePercent),
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextPrimary
          )
          Text(
            text = "${bot.winningTrades}/${bot.totalTrades} orders",
            fontFamily = RobotoMono,
            fontSize = 10.sp,
            color = TextMuted
          )
        }

        // Execution Latency
        Column(horizontalAlignment = Alignment.End) {
          Text(text = "SPEED", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(2.dp))
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Speed,
              contentDescription = "Latency",
              modifier = Modifier.size(13.dp),
              tint = NeonCyan
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = String.format("%.2f ms", bot.avgLatencyMs),
              fontFamily = RobotoMono,
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = NeonCyan
            )
          }
          Text(
            text = bot.speed.label.substringBefore(" ("),
            fontFamily = RobotoMono,
            fontSize = 9.sp,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Pre-calculated TP, SL, and Risk/Reward parameters
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(SurfaceDark, RoundedCornerShape(6.dp))
          .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "TP: ", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
          Text(text = "+${bot.targetTakeProfitPercent}%", fontFamily = RobotoMono, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = CandleGreen)
          Spacer(modifier = Modifier.width(8.dp))
          Text(text = "SL: ", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
          Text(text = "-${bot.stopLossPercent}%", fontFamily = RobotoMono, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = CandleRed)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = "Lev: ", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
          Text(text = "${bot.leverage}x", fontFamily = RobotoMono, fontWeight = FontWeight.Bold, fontSize = 10.sp, color = NeonPurple)
          Spacer(modifier = Modifier.width(8.dp))
          val rr = if (bot.stopLossPercent > 0) bot.targetTakeProfitPercent / bot.stopLossPercent else 2.0
          Text(text = "RR: 1:${String.format("%.1f", rr)}", fontFamily = RobotoMono, fontSize = 9.sp, color = NeonCyan)
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Win Rate progress bar
      val winRateRatio = (bot.winRatePercent / 100.0).toFloat().coerceIn(0f, 1f)
      LinearProgressIndicator(
        progress = { winRateRatio },
        modifier = Modifier
          .fillMaxWidth()
          .height(3.dp)
          .clip(RoundedCornerShape(2.dp)),
        color = CandleGreen,
        trackColor = SurfaceDark
      )
    }
  }
}
