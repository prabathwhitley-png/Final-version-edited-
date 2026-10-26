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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BotStrategy
import com.example.data.model.ExecutionSpeed
import com.example.ui.theme.CandleGreen
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

@Composable
fun CreateBotDialog(
  onDismiss: () -> Unit,
  onConfirm: (
    name: String,
    strategy: BotStrategy,
    pair: String,
    capitalUsd: Double,
    leverage: Int,
    speed: ExecutionSpeed,
    takeProfitPercent: Double,
    stopLossPercent: Double
  ) -> Unit
) {
  var botName by remember { mutableStateOf("") }
  var selectedStrategy by remember { mutableStateOf(BotStrategy.FLASH_SCALP) }
  var selectedPair by remember { mutableStateOf("BTC/USDT") }
  var capitalUsd by remember { mutableDoubleStateOf(10000.0) }
  var leverage by remember { mutableIntStateOf(10) }
  var selectedSpeed by remember { mutableStateOf(ExecutionSpeed.TURBO_BLITZ) }
  var takeProfit by remember { mutableDoubleStateOf(1.5) }
  var stopLoss by remember { mutableDoubleStateOf(0.8) }
  val focusManager = LocalFocusManager.current

  val pairs = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT", "NVDA/USD", "EUR/USD")

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceCard,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
          text = "DEPLOY NEW ALGO BOT",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = NeonCyan
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .fillMaxWidth()
      ) {
        // Name Field
        OutlinedTextField(
          value = botName,
          onValueChange = { botName = it },
          label = { Text("Bot Name (Optional)", fontFamily = SpaceGrotesk) },
          placeholder = { Text("e.g. Apex Arbitrage Prime", fontFamily = SpaceGrotesk, color = TextMuted) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("create_bot_name_input"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Strategy Selector
        Text(
          text = "CORE ALGORITHM STRATEGY",
          fontFamily = SpaceGrotesk,
          fontSize = 11.sp,
          color = TextSecondary
        )
        Spacer(modifier = Modifier.height(6.dp))
        BotStrategy.values().forEach { strategy ->
          val isSel = strategy == selectedStrategy
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
              .background(if (isSel) NeonCyan.copy(alpha = 0.15f) else SurfaceDark, RoundedCornerShape(8.dp))
              .border(1.dp, if (isSel) NeonCyan else SurfaceBorder, RoundedCornerShape(8.dp))
              .clickable { selectedStrategy = strategy }
              .padding(10.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = strategy.displayName,
                  fontFamily = SpaceGrotesk,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = if (isSel) NeonCyan else TextPrimary
                )
                Text(
                  text = "${strategy.defaultLatencyTargetMs} ms",
                  fontFamily = RobotoMono,
                  fontSize = 11.sp,
                  color = CandleGreen
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = strategy.description,
                fontFamily = SpaceGrotesk,
                fontSize = 10.sp,
                color = TextSecondary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Trading Pair
        Text(text = "TARGET ASSET PAIR", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          pairs.forEach { p ->
            val isSel = p == selectedPair
            Box(
              modifier = Modifier
                .weight(1f)
                .background(if (isSel) CandleGreen.copy(alpha = 0.2f) else SurfaceDark, RoundedCornerShape(6.dp))
                .border(1.dp, if (isSel) CandleGreen else SurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { selectedPair = p }
                .padding(vertical = 6.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = p.substringBefore("/"),
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                color = if (isSel) CandleGreen else TextPrimary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Capital Allocation
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "CAPITAL ALLOCATION", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextSecondary)
          Text(
            text = String.format("$%,.0f", capitalUsd),
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = CandleGreen
          )
        }
        Slider(
          value = capitalUsd.toFloat(),
          onValueChange = { capitalUsd = it.toDouble() },
          valueRange = 1000f..50000f,
          steps = 48,
          colors = SliderDefaults.colors(
            thumbColor = CandleGreen,
            activeTrackColor = CandleGreen,
            inactiveTrackColor = SurfaceBorder
          )
        )

        // Leverage
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Text(text = "LEVERAGE", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextSecondary)
          Text(
            text = "${leverage}x",
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
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

        Spacer(modifier = Modifier.height(8.dp))

        // Execution Speed
        Text(text = "LATENCY PRIORITY", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          ExecutionSpeed.values().forEach { spd ->
            val isSel = spd == selectedSpeed
            Box(
              modifier = Modifier
                .weight(1f)
                .background(if (isSel) NeonCyan.copy(alpha = 0.2f) else SurfaceDark, RoundedCornerShape(6.dp))
                .border(1.dp, if (isSel) NeonCyan else SurfaceBorder, RoundedCornerShape(6.dp))
                .clickable { selectedSpeed = spd }
                .padding(vertical = 8.dp),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = spd.label.substringBefore(" ("),
                fontFamily = SpaceGrotesk,
                fontSize = 10.sp,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                color = if (isSel) NeonCyan else TextSecondary
              )
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          focusManager.clearFocus()
          onConfirm(
            botName,
            selectedStrategy,
            selectedPair,
            capitalUsd,
            leverage,
            selectedSpeed,
            takeProfit,
            stopLoss
          )
        },
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = SurfaceDark),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("create_bot_confirm_button")
      ) {
        Text("DEPLOY BOT", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = {
          focusManager.clearFocus()
          onDismiss()
        },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("CANCEL", fontFamily = SpaceGrotesk)
      }
    }
  )
}
