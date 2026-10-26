package com.example.ui.components

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HftTelemetry
import com.example.ui.theme.CandleGreen
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
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
fun TelemetryBar(
  telemetry: HftTelemetry,
  onToggleBlitz: () -> Unit,
  onEmergencyKill: () -> Unit,
  onResume: () -> Unit,
  modifier: Modifier = Modifier
) {
  val isHalted = telemetry.emergencyHaltTriggered
  val isTurbo = telemetry.isTurboBlitzActive

  val statusColor by animateColorAsState(
    targetValue = when {
      isHalted -> NeonRed
      isTurbo -> NeonCyan
      else -> CandleGreen
    },
    label = "status_color"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("telemetry_bar_card"),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    shape = RoundedCornerShape(14.dp),
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
              .size(10.dp)
              .clip(CircleShape)
              .background(statusColor)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = if (isHalted) "ENGINE HALTED" else if (isTurbo) "TURBO BLITZ (HFT)" else "STANDARD DMA",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = statusColor
          )
        }

        Text(
          text = telemetry.colocationNode,
          fontFamily = RobotoMono,
          fontSize = 10.sp,
          color = TextMuted
        )
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Metrics Grid: Latency, Jitter, Throughput, Success Rate
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        // Latency
        Column(
          modifier = Modifier
            .weight(1f)
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .padding(8.dp)
        ) {
          Text(text = "LATENCY", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(2.dp))
          val latMs = telemetry.latencyMicroseconds / 1000.0
          Text(
            text = String.format("%.2f ms", latMs),
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (latMs < 0.5) NeonCyan else CandleGreen
          )
          Text(
            text = "${telemetry.latencyMicroseconds} µs",
            fontFamily = RobotoMono,
            fontSize = 9.sp,
            color = TextMuted
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Jitter
        Column(
          modifier = Modifier
            .weight(1f)
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .padding(8.dp)
        ) {
          Text(text = "JITTER", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${telemetry.jitterMicroseconds} µs",
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextPrimary
          )
          Text(
            text = "DMA Direct",
            fontFamily = RobotoMono,
            fontSize = 9.sp,
            color = TextMuted
          )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Throughput
        Column(
          modifier = Modifier
            .weight(1f)
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .padding(8.dp)
        ) {
          Text(text = "THROUGHPUT", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "${telemetry.ordersPerSecond}/s",
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (telemetry.ordersPerSecond > 500) NeonCyan else CandleGreen
          )
          Text(
            text = "99.88% Fill",
            fontFamily = RobotoMono,
            fontSize = 9.sp,
            color = TextMuted
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Control Buttons: Turbo Blitz Toggle + Emergency Stop
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        // Blitz button
        ElevatedButton(
          onClick = onToggleBlitz,
          modifier = Modifier
            .weight(1.2f)
            .height(38.dp)
            .testTag("turbo_blitz_toggle_button"),
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (isTurbo) NeonCyan.copy(alpha = 0.15f) else SurfaceDark,
            contentColor = if (isTurbo) NeonCyan else TextSecondary
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isTurbo) NeonCyan else SurfaceBorder
          ),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Bolt,
              contentDescription = "Turbo Blitz",
              modifier = Modifier.size(16.dp),
              tint = if (isTurbo) NeonCyan else TextSecondary
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isTurbo) "BLITZ ACTIVE" else "ACTIVATE BLITZ",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }

        // Kill switch / Resume button
        ElevatedButton(
          onClick = { if (isHalted) onResume() else onEmergencyKill() },
          modifier = Modifier
            .weight(1f)
            .height(38.dp)
            .testTag("emergency_kill_button"),
          colors = ButtonDefaults.elevatedButtonColors(
            containerColor = if (isHalted) CandleGreen.copy(alpha = 0.2f) else NeonRed.copy(alpha = 0.15f),
            contentColor = if (isHalted) CandleGreen else NeonRed
          ),
          border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isHalted) CandleGreen else NeonRed
          ),
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = "Kill Switch",
              modifier = Modifier.size(14.dp),
              tint = if (isHalted) CandleGreen else NeonRed
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (isHalted) "RESUME" else "KILL ALL",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 11.sp
            )
          }
        }
      }
    }
  }
}
