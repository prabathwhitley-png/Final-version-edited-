package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CandleStick
import com.example.data.model.TradeOrder
import com.example.ui.theme.CandleGreen
import com.example.ui.theme.CandleRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RobotoMono
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import kotlin.math.max
import kotlin.math.min

@Composable
fun CandleChart(
  candles: List<CandleStick>,
  recentOrders: List<TradeOrder>,
  currentPrice: Double,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .height(230.dp)
      .background(SurfaceDark)
      .testTag("candlestick_chart_container")
  ) {
    if (candles.isEmpty()) {
      Text(
        text = "Connecting to market feed...",
        color = TextMuted,
        fontFamily = RobotoMono,
        fontSize = 12.sp,
        modifier = Modifier.align(Alignment.Center)
      )
      return
    }

    val minPrice = remember(candles) { (candles.minOfOrNull { it.low } ?: 100.0) * 0.999 }
    val maxPrice = remember(candles) { (candles.maxOfOrNull { it.high } ?: 105.0) * 1.001 }
    val priceRange = max(0.0001, maxPrice - minPrice)
    val maxVolume = remember(candles) { max(1.0, candles.maxOfOrNull { it.volume } ?: 10.0) }

    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 8.dp, vertical = 12.dp)
        .testTag("candle_canvas")
    ) {
      val w = size.width
      val h = size.height
      val candleCount = candles.size
      val slotWidth = w / candleCount
      val candleWidth = max(2f, slotWidth * 0.65f)

      // Draw horizontal grid lines (3 levels)
      for (i in 1..3) {
        val y = h * (i / 4f)
        drawLine(
          color = SurfaceBorder.copy(alpha = 0.5f),
          start = Offset(0f, y),
          end = Offset(w, y),
          strokeWidth = 1f
        )
      }

      // Draw Volume Bars at bottom (occupying lowest 25% height)
      val volHeight = h * 0.22f
      candles.forEachIndexed { idx, c ->
        val x = (idx * slotWidth) + (slotWidth / 2f)
        val vRatio = (c.volume / maxVolume).toFloat().coerceIn(0f, 1f)
        val barH = vRatio * volHeight
        val isGreen = c.close >= c.open
        drawRect(
          color = if (isGreen) CandleGreen.copy(alpha = 0.25f) else CandleRed.copy(alpha = 0.25f),
          topLeft = Offset(x - (candleWidth / 2f), h - barH),
          size = Size(candleWidth, barH)
        )
      }

      // Draw EMA 9 and EMA 21 lines
      val ema9Path = Path()
      val ema21Path = Path()
      candles.forEachIndexed { idx, c ->
        val x = (idx * slotWidth) + (slotWidth / 2f)
        val y9 = h - (((c.ema9 - minPrice) / priceRange).toFloat() * h)
        val y21 = h - (((c.ema21 - minPrice) / priceRange).toFloat() * h)
        if (idx == 0) {
          ema9Path.moveTo(x, y9)
          ema21Path.moveTo(x, y21)
        } else {
          ema9Path.lineTo(x, y9)
          ema21Path.lineTo(x, y21)
        }
      }
      drawPath(ema9Path, color = NeonCyan.copy(alpha = 0.8f), style = Stroke(width = 1.5f))
      drawPath(ema21Path, color = NeonPurple.copy(alpha = 0.8f), style = Stroke(width = 1.5f))

      // Draw Candlesticks (wicks and body)
      candles.forEachIndexed { idx, c ->
        val x = (idx * slotWidth) + (slotWidth / 2f)
        val isGreen = c.close >= c.open
        val color = if (isGreen) CandleGreen else CandleRed

        val highY = h - (((c.high - minPrice) / priceRange).toFloat() * h)
        val lowY = h - (((c.low - minPrice) / priceRange).toFloat() * h)
        val openY = h - (((c.open - minPrice) / priceRange).toFloat() * h)
        val closeY = h - (((c.close - minPrice) / priceRange).toFloat() * h)

        // Wick line
        drawLine(
          color = color,
          start = Offset(x, highY),
          end = Offset(x, lowY),
          strokeWidth = 1.5f
        )

        // Body rect
        val bodyTop = min(openY, closeY)
        val bodyBottom = max(openY, closeY)
        val bodyHeight = max(2f, bodyBottom - bodyTop)

        drawRect(
          color = color,
          topLeft = Offset(x - (candleWidth / 2f), bodyTop),
          size = Size(candleWidth, bodyHeight)
        )
      }

      // Draw current live price horizontal dashed indicator
      val curY = h - (((currentPrice - minPrice) / priceRange).toFloat() * h)
      if (curY in 0f..h) {
        drawLine(
          color = NeonCyan,
          start = Offset(0f, curY),
          end = Offset(w, curY),
          strokeWidth = 1.5f
        )
      }

      // Draw Bot Trade Markers (last 5 trades)
      val displayedOrders = recentOrders.take(5)
      displayedOrders.forEachIndexed { oIdx, order ->
        val candleIdx = (candleCount - 1 - (oIdx * 4)).coerceIn(0, candleCount - 1)
        val x = (candleIdx * slotWidth) + (slotWidth / 2f)
        val isBuy = order.side == com.example.data.model.OrderSide.BUY
        val markerColor = if (isBuy) CandleGreen else CandleRed

        val orderY = h - (((order.price - minPrice) / priceRange).toFloat() * h).coerceIn(10f, h - 10f)

        // Draw triangle marker for bot entry/exit
        val markerPath = Path()
        if (isBuy) {
          // Pointing up
          markerPath.moveTo(x, orderY + 8f)
          markerPath.lineTo(x - 5f, orderY + 16f)
          markerPath.lineTo(x + 5f, orderY + 16f)
        } else {
          // Pointing down
          markerPath.moveTo(x, orderY - 8f)
          markerPath.lineTo(x - 5f, orderY - 16f)
          markerPath.lineTo(x + 5f, orderY - 16f)
        }
        markerPath.close()
        drawPath(markerPath, color = markerColor)
      }
    }

    // High & Low legend overlay
    Text(
      text = String.format("H: %.2f", maxPrice),
      color = TextMuted,
      fontFamily = RobotoMono,
      fontSize = 10.sp,
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(6.dp)
    )
    Text(
      text = String.format("L: %.2f", minPrice),
      color = TextMuted,
      fontFamily = RobotoMono,
      fontSize = 10.sp,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(bottom = 6.dp, end = 6.dp)
    )

    // Indicator legend
    Text(
      text = "EMA(9) • EMA(21) • VOL",
      color = NeonCyan.copy(alpha = 0.7f),
      fontFamily = RobotoMono,
      fontSize = 10.sp,
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(6.dp)
    )
  }
}
