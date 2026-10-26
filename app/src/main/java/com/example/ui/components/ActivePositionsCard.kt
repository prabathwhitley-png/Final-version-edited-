package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ActivePosition
import com.example.data.model.OrderSide
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
import kotlin.math.abs

@Composable
fun ActivePositionsCard(
  positions: List<ActivePosition>,
  onClosePosition: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  val totalUnrealizedPnl = positions.sumOf { it.unrealizedPnlUsd }
  val isTotalProfitable = totalUnrealizedPnl >= 0

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("active_positions_card"),
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
            imageVector = Icons.Default.TrendingUp,
            contentDescription = null,
            tint = if (isTotalProfitable) CandleGreen else CandleRed,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "LIVE OPEN POSITIONS (${positions.size})",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = TextPrimary
          )
        }

        if (positions.isNotEmpty()) {
          Text(
            text = (if (isTotalProfitable) "+$" else "-$") + String.format("%.2f Unrealized", abs(totalUnrealizedPnl)),
            fontFamily = RobotoMono,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = if (isTotalProfitable) CandleGreen else CandleRed
          )
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      if (positions.isEmpty()) {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark, RoundedCornerShape(8.dp))
            .padding(16.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
              text = "No open positions active",
              fontFamily = SpaceGrotesk,
              fontSize = 12.sp,
              color = TextMuted
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "The bot will automatically enter on live market signals, or tap 'MAKE REAL ORDER'",
              fontFamily = RobotoMono,
              fontSize = 10.sp,
              color = TextSecondary,
              textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
          }
        }
      } else {
        positions.forEach { pos ->
          val isLong = pos.side == OrderSide.BUY
          val isPosProfit = pos.unrealizedPnlUsd >= 0
          val posColor = if (isPosProfit) CandleGreen else CandleRed

          Card(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceDark),
            shape = RoundedCornerShape(10.dp),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
          ) {
            Column(modifier = Modifier.padding(12.dp)) {
              // Header: Pair + Side + Leverage + Close Button
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .background(
                        if (isLong) CandleGreen.copy(alpha = 0.2f) else CandleRed.copy(alpha = 0.2f),
                        RoundedCornerShape(4.dp)
                      )
                      .padding(horizontal = 6.dp, vertical = 2.dp)
                  ) {
                    Text(
                      text = if (isLong) "LONG" else "SHORT",
                      fontFamily = RobotoMono,
                      fontWeight = FontWeight.Bold,
                      fontSize = 10.sp,
                      color = if (isLong) CandleGreen else CandleRed
                    )
                  }
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "${pos.pair} ${pos.leverage}x",
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    color = TextPrimary
                  )
                }

                // Live Unrealized PnL
                Text(
                  text = (if (isPosProfit) "+$" else "-$") + String.format("%.3f (%+.2f%%)", abs(pos.unrealizedPnlUsd), pos.unrealizedPnlPercent),
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = posColor
                )
              }

              Spacer(modifier = Modifier.height(8.dp))

              // Prices: Entry vs Mark Price
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Column {
                  Text(text = "ENTRY PRICE", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
                  Text(
                    text = String.format("$%,.2f", pos.entryPrice),
                    fontFamily = RobotoMono,
                    fontSize = 11.sp,
                    color = TextSecondary
                  )
                }
                Column {
                  Text(text = "LIVE MARK", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
                  Text(
                    text = String.format("$%,.2f", pos.currentPrice),
                    fontFamily = RobotoMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = TextPrimary
                  )
                }
                Column {
                  Text(text = "MARGIN", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
                  Text(
                    text = "$${String.format("%.2f", pos.marginUsd)}",
                    fontFamily = RobotoMono,
                    fontSize = 11.sp,
                    color = CandleGreen
                  )
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text(text = "QUANTITY", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
                  Text(
                    text = String.format("%.6f", pos.quantityCrypto),
                    fontFamily = RobotoMono,
                    fontSize = 11.sp,
                    color = TextSecondary
                  )
                }
              }

              Spacer(modifier = Modifier.height(8.dp))

              // TP & SL target bar + Market Exit Button
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = "TP: $${String.format("%.1f", pos.tpPrice)}",
                    fontFamily = RobotoMono,
                    fontSize = 10.sp,
                    color = CandleGreen
                  )
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = "SL: $${String.format("%.1f", pos.slPrice)}",
                    fontFamily = RobotoMono,
                    fontSize = 10.sp,
                    color = CandleRed
                  )
                }

                Button(
                  onClick = { onClosePosition(pos.id) },
                  modifier = Modifier.height(28.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard, contentColor = TextPrimary),
                  border = androidx.compose.foundation.BorderStroke(1.dp, SurfaceBorder),
                  shape = RoundedCornerShape(6.dp)
                ) {
                  Text(text = "MARKET EXIT", fontFamily = SpaceGrotesk, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    }
  }
}
