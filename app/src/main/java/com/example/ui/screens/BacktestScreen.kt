package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BacktestConfig
import com.example.data.model.BacktestResult
import com.example.data.model.BotStrategy
import com.example.data.model.MarketScenario
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
import kotlin.math.max

@Composable
fun BacktestScreen(
  config: BacktestConfig,
  result: BacktestResult?,
  isBacktesting: Boolean,
  onUpdateConfig: (strategy: BotStrategy, pair: String, capital: Double, leverage: Int, scenario: MarketScenario) -> Unit,
  onRunBacktest: () -> Unit,
  modifier: Modifier = Modifier
) {
  val pairs = listOf("BTC/USDT", "ETH/USDT", "SOL/USDT", "NVDA/USD")

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 14.dp)
      .testTag("backtest_screen"),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(2.dp))
    }

    // Title Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Psychology,
              contentDescription = "Strategy Lab",
              tint = NeonCyan,
              modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "STRATEGY BACKTEST LAB",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp,
              color = TextPrimary
            )
          }
          Text(
            text = "Stress-test sub-ms trading algorithms on historical market cycles",
            fontFamily = SpaceGrotesk,
            fontSize = 11.sp,
            color = TextMuted
          )
        }
      }
    }

    // Configuration Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        shape = RoundedCornerShape(14.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          // Select Strategy
          Text(text = "SELECT ALGO STRATEGY", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            BotStrategy.values().take(3).forEach { st ->
              val isSel = st == config.strategy
              Box(
                modifier = Modifier
                  .weight(1f)
                  .background(if (isSel) NeonCyan.copy(alpha = 0.2f) else SurfaceDark, RoundedCornerShape(6.dp))
                  .border(1.dp, if (isSel) NeonCyan else SurfaceBorder, RoundedCornerShape(6.dp))
                  .clickable {
                    onUpdateConfig(st, config.pair, config.initialCapitalUsd, config.leverage, config.scenario)
                  }
                  .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = st.displayName.substringBefore(" "),
                  fontFamily = SpaceGrotesk,
                  fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                  fontSize = 11.sp,
                  color = if (isSel) NeonCyan else TextSecondary
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Select Market Scenario
          Text(text = "HISTORICAL REGIME SCENARIO", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextSecondary)
          Spacer(modifier = Modifier.height(6.dp))
          MarketScenario.values().forEach { sc ->
            val isSel = sc == config.scenario
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .background(if (isSel) CandleGreen.copy(alpha = 0.15f) else SurfaceDark, RoundedCornerShape(8.dp))
                .border(1.dp, if (isSel) CandleGreen else SurfaceBorder, RoundedCornerShape(8.dp))
                .clickable {
                  onUpdateConfig(config.strategy, config.pair, config.initialCapitalUsd, config.leverage, sc)
                }
                .padding(10.dp)
            ) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column(modifier = Modifier.weight(1f)) {
                  Text(
                    text = sc.title,
                    fontFamily = SpaceGrotesk,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isSel) CandleGreen else TextPrimary
                  )
                  Text(
                    text = sc.description,
                    fontFamily = SpaceGrotesk,
                    fontSize = 10.sp,
                    color = TextMuted
                  )
                }
                Box(
                  modifier = Modifier
                    .background(SurfaceDark, RoundedCornerShape(4.dp))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                  Text(
                    text = "${sc.volatility} Vol",
                    fontFamily = RobotoMono,
                    fontSize = 9.sp,
                    color = NeonPurple
                  )
                }
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Capital & Leverage row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "CAPITAL: $${String.format("%,.0f", config.initialCapitalUsd)}",
              fontFamily = RobotoMono,
              fontSize = 11.sp,
              color = TextSecondary
            )
            Text(
              text = "LEVERAGE: ${config.leverage}x",
              fontFamily = RobotoMono,
              fontSize = 11.sp,
              color = NeonPurple
            )
          }

          Slider(
            value = config.initialCapitalUsd.toFloat(),
            onValueChange = {
              onUpdateConfig(config.strategy, config.pair, it.toDouble(), config.leverage, config.scenario)
            },
            valueRange = 5000f..100000f,
            steps = 19,
            colors = SliderDefaults.colors(
              thumbColor = NeonCyan,
              activeTrackColor = NeonCyan,
              inactiveTrackColor = SurfaceBorder
            )
          )

          Spacer(modifier = Modifier.height(8.dp))

          // Run Button
          Button(
            onClick = onRunBacktest,
            enabled = !isBacktesting,
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("run_backtest_button"),
            colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = SurfaceDark),
            shape = RoundedCornerShape(8.dp)
          ) {
            if (isBacktesting) {
              CircularProgressIndicator(modifier = Modifier.size(18.dp), color = SurfaceDark, strokeWidth = 2.dp)
              Spacer(modifier = Modifier.width(8.dp))
              Text("SIMULATING ORDER BOOK TICKS...", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold)
            } else {
              Icon(imageVector = Icons.Default.PlayArrow, contentDescription = "Run", modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("RUN HIGH-SPEED SIMULATION", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // Results Section
    if (result != null) {
      item {
        Card(
          modifier = Modifier.fillMaxWidth(),
          colors = CardDefaults.cardColors(containerColor = SurfaceCard),
          shape = RoundedCornerShape(14.dp),
          border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            // Net Return Highlight
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(text = "TOTAL SIMULATED RETURN", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(2.dp))
                val isPos = result.totalReturnPercent >= 0
                Text(
                  text = (if (isPos) "+" else "") + String.format("%.2f%%", result.totalReturnPercent),
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 24.sp,
                  color = if (isPos) CandleGreen else CandleRed
                )
                Text(
                  text = (if (isPos) "+$" else "-$") + String.format("%,.2f Net Profit", kotlin.math.abs(result.netProfitUsd)),
                  fontFamily = RobotoMono,
                  fontSize = 12.sp,
                  color = TextSecondary
                )
              }

              Box(
                modifier = Modifier
                  .background(SurfaceDark, RoundedCornerShape(8.dp))
                  .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                  .padding(horizontal = 10.dp, vertical = 6.dp)
              ) {
                Column(horizontalAlignment = Alignment.End) {
                  Text(text = "SHARPE RATIO", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
                  Text(
                    text = String.format("%.2f", result.sharpeRatio),
                    fontFamily = RobotoMono,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = NeonCyan
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Equity Growth Curve Chart (Canvas)
            Text(text = "EQUITY GROWTH COMPOUNDING CURVE", fontFamily = SpaceGrotesk, fontSize = 10.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(6.dp))

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(140.dp)
                .background(SurfaceDark, RoundedCornerShape(8.dp))
                .padding(8.dp)
            ) {
              val curve = result.equityCurve
              if (curve.isNotEmpty()) {
                val minEq = curve.minOrNull() ?: 1000.0
                val maxEq = max(minEq + 1.0, curve.maxOrNull() ?: 2000.0)
                val eqRange = maxEq - minEq

                Canvas(modifier = Modifier.fillMaxSize()) {
                  val w = size.width
                  val h = size.height
                  val path = Path()
                  val fillPath = Path()

                  val stepX = w / (curve.size - 1)

                  curve.forEachIndexed { i, eq ->
                    val x = i * stepX
                    val y = h - (((eq - minEq) / eqRange).toFloat() * h).coerceIn(4f, h - 4f)
                    if (i == 0) {
                      path.moveTo(x, y)
                      fillPath.moveTo(x, h)
                      fillPath.lineTo(x, y)
                    } else {
                      path.lineTo(x, y)
                      fillPath.lineTo(x, y)
                    }
                  }
                  fillPath.lineTo(w, h)
                  fillPath.close()

                  // Draw gradient fill under equity curve
                  drawPath(
                    fillPath,
                    brush = Brush.verticalGradient(
                      colors = listOf(CandleGreen.copy(alpha = 0.25f), Color.Transparent)
                    )
                  )
                  // Draw equity line
                  drawPath(path, color = CandleGreen, style = Stroke(width = 2.5f))
                }
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 4 Metrics Grid: Win Rate, Profit Factor, Max Drawdown, Avg Trade Duration
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column(
                modifier = Modifier
                  .weight(1f)
                  .background(SurfaceDark, RoundedCornerShape(6.dp))
                  .padding(8.dp)
              ) {
                Text(text = "WIN RATE", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
                Text(
                  text = String.format("%.1f%%", result.winRatePercent),
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = CandleGreen
                )
                Text(
                  text = "${result.winningTrades}/${result.totalTrades}",
                  fontFamily = RobotoMono,
                  fontSize = 9.sp,
                  color = TextMuted
                )
              }

              Spacer(modifier = Modifier.width(6.dp))

              Column(
                modifier = Modifier
                  .weight(1f)
                  .background(SurfaceDark, RoundedCornerShape(6.dp))
                  .padding(8.dp)
              ) {
                Text(text = "PROFIT FACTOR", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
                Text(
                  text = String.format("%.2f", result.profitFactor),
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = TextPrimary
                )
                Text(
                  text = "Gross W/L",
                  fontFamily = RobotoMono,
                  fontSize = 9.sp,
                  color = TextMuted
                )
              }

              Spacer(modifier = Modifier.width(6.dp))

              Column(
                modifier = Modifier
                  .weight(1f)
                  .background(SurfaceDark, RoundedCornerShape(6.dp))
                  .padding(8.dp)
              ) {
                Text(text = "MAX DRAWDOWN", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
                Text(
                  text = String.format("%.2f%%", result.maxDrawdownPercent),
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = CandleRed
                )
                Text(
                  text = "Protected",
                  fontFamily = RobotoMono,
                  fontSize = 9.sp,
                  color = TextMuted
                )
              }

              Spacer(modifier = Modifier.width(6.dp))

              Column(
                modifier = Modifier
                  .weight(1f)
                  .background(SurfaceDark, RoundedCornerShape(6.dp))
                  .padding(8.dp)
              ) {
                Text(text = "AVG DURATION", fontFamily = SpaceGrotesk, fontSize = 9.sp, color = TextMuted)
                Text(
                  text = "${result.avgTradeDurationMs} ms",
                  fontFamily = RobotoMono,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = NeonCyan
                )
                Text(
                  text = "Sub-second",
                  fontFamily = RobotoMono,
                  fontSize = 9.sp,
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
