package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BotStrategy(val displayName: String, val description: String, val defaultLatencyTargetMs: Double) {
  FLASH_SCALP("FlashScalp AI", "Micro-second orderbook imbalance exploitation with momentum trigger", 0.45),
  TRIANGULAR_ARBITRAGE("Triangular Arb", "Cross-pair zero-risk latency arbitrage across routing paths", 0.32),
  DYNAMIC_GRID("Dynamic ATR Grid", "Auto-adjusting grid bands that adapt to real-time market volatility", 1.20),
  MARKET_MAKER("High-Freq Market Maker", "Continuous bid/ask spread capture with dynamic inventory hedge", 0.60),
  MOMENTUM_BLITZ("Momentum Blitz", "Volume-weighted surge detection on 1-second breakouts", 0.85)
}

enum class BotStatus {
  RUNNING,
  PAUSED,
  STOPPED
}

enum class ExecutionSpeed(val label: String, val minIntervalMs: Long, val multiplier: Float) {
  TURBO_BLITZ("Turbo Blitz (50ms)", 50L, 5.0f),
  ULTRA_FAST("Ultra Fast (200ms)", 200L, 2.5f),
  BALANCED("Balanced (800ms)", 800L, 1.0f)
}

@Entity(tableName = "trading_bots")
data class TradingBot(
  @PrimaryKey
  val id: String,
  val name: String,
  val strategy: BotStrategy,
  val pair: String,
  val allocatedCapitalUsd: Double,
  val leverage: Int = 10,
  val status: BotStatus = BotStatus.RUNNING,
  val speed: ExecutionSpeed = ExecutionSpeed.TURBO_BLITZ,
  val targetTakeProfitPercent: Double = 1.5,
  val stopLossPercent: Double = 0.8,
  val trailingStop: Boolean = true,
  val totalTrades: Int = 0,
  val winningTrades: Int = 0,
  val netProfitUsd: Double = 0.0,
  val avgLatencyMs: Double = 0.64,
  val maxDrawdownPercent: Double = 1.2,
  val activePositions: Int = 0,
  val createdAt: Long = System.currentTimeMillis()
) {
  val winRatePercent: Double
    get() = if (totalTrades > 0) (winningTrades.toDouble() / totalTrades.toDouble()) * 100.0 else 0.0

  val profitPercent: Double
    get() = if (allocatedCapitalUsd > 0) (netProfitUsd / allocatedCapitalUsd) * 100.0 else 0.0
}
