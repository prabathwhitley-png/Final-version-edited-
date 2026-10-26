package com.example.data.model

data class CandleStick(
  val timestamp: Long,
  val open: Double,
  val high: Double,
  val low: Double,
  val close: Double,
  val volume: Double,
  val ema9: Double = close,
  val ema21: Double = close
)

data class OrderBookLevel(
  val price: Double,
  val size: Double,
  val total: Double,
  val depthPercent: Float
)

data class OrderBook(
  val pair: String,
  val bids: List<OrderBookLevel>,
  val asks: List<OrderBookLevel>,
  val spreadUsd: Double,
  val spreadBps: Double,
  val imbalanceRatio: Double // positive means more bid depth (bullish buy pressure)
)

data class MarketTicker(
  val pair: String,
  val lastPrice: Double,
  val priceChange24h: Double,
  val priceChangePercent24h: Double,
  val high24h: Double,
  val low24h: Double,
  val volume24hUsd: Double,
  val fundingRatePercent: Double,
  val openInterestUsd: Double
)

data class HftTelemetry(
  val latencyMicroseconds: Long, // e.g. 740 µs (0.74 ms)
  val jitterMicroseconds: Long,
  val ordersPerSecond: Int,
  val executionSuccessRate: Double,
  val activeExchanges: List<String>,
  val colocationNode: String,
  val memoryFootprintMb: Double,
  val cpuUtilizationPercent: Double,
  val isTurboBlitzActive: Boolean,
  val emergencyHaltTriggered: Boolean
)

data class BacktestConfig(
  val strategy: BotStrategy,
  val pair: String,
  val initialCapitalUsd: Double = 10000.0,
  val leverage: Int = 10,
  val scenario: MarketScenario = MarketScenario.BULL_MOMENTUM_RUN
)

enum class MarketScenario(val title: String, val description: String, val volatility: String) {
  BULL_MOMENTUM_RUN("ETF Inflow Blitz", "Rapid upward trending impulses with shallow pullbacks", "High"),
  FLASH_CRASH_RECOVERY("Flash Crash & V-Shape", "Instant 12% cascade drop followed by algorithmic snapback", "Extreme"),
  SIDEWAYS_VOLATILITY("High-Frequency Chop", "Range-bound micro-oscillations, ideal for Grid & Market Maker", "Medium"),
  BEAR_TREND_DOWN("Institutional Distribution", "Persistent downward pressure with failed bull traps", "High")
}

data class BacktestResult(
  val totalReturnPercent: Double,
  val netProfitUsd: Double,
  val maxDrawdownPercent: Double,
  val winRatePercent: Double,
  val profitFactor: Double,
  val sharpeRatio: Double,
  val totalTrades: Int,
  val winningTrades: Int,
  val losingTrades: Int,
  val avgTradeDurationMs: Long,
  val simulatedFeesUsd: Double,
  val equityCurve: List<Double>
)
