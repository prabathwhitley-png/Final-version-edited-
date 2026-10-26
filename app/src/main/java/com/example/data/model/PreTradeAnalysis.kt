package com.example.data.model

data class PreTradeCalculation(
  val pair: String,
  val side: OrderSide,
  val entryPrice: Double,
  val marginAmountUsd: Double,
  val leverage: Int,
  val positionSizeUsd: Double,
  val quantityCrypto: Double,
  val tpPercent: Double,
  val tpPrice: Double,
  val expectedProfitUsd: Double,
  val slPercent: Double,
  val slPrice: Double,
  val maxLossUsd: Double,
  val riskRewardRatio: Double,
  val liquidationPrice: Double,
  val estExchangeFeeUsd: Double,
  val marketImbalanceScore: Double,
  val marketTrendSignal: String,
  val marketAnalysisSummary: String
)

object RiskCalculator {
  fun calculate(
    pair: String,
    side: OrderSide,
    entryPrice: Double,
    marginUsd: Double,
    leverage: Int,
    tpPercent: Double = 1.5,
    slPercent: Double = 0.75,
    orderBookImbalance: Double = 0.0
  ): PreTradeCalculation {
    val posSize = marginUsd * leverage
    val qty = if (entryPrice > 0) posSize / entryPrice else 0.0

    val isBuy = side == OrderSide.BUY
    val tpPrice = if (isBuy) entryPrice * (1.0 + (tpPercent / 100.0)) else entryPrice * (1.0 - (tpPercent / 100.0))
    val slPrice = if (isBuy) entryPrice * (1.0 - (slPercent / 100.0)) else entryPrice * (1.0 + (slPercent / 100.0))

    val expProfit = posSize * (tpPercent / 100.0)
    val maxLoss = posSize * (slPercent / 100.0)
    val rr = if (slPercent > 0) tpPercent / slPercent else 2.0

    // Liquidation price calculation: margin maintenance ~90%
    val liqDistanceRatio = (1.0 / leverage) * 0.90
    val liqPrice = if (isBuy) {
      entryPrice * (1.0 - liqDistanceRatio).coerceAtLeast(0.0)
    } else {
      entryPrice * (1.0 + liqDistanceRatio)
    }

    val fee = posSize * 0.0004 // 0.04% taker fee

    val trendSignal = when {
      orderBookImbalance > 0.25 -> "STRONG BULLISH PRESSURE"
      orderBookImbalance < -0.25 -> "STRONG BEARISH PRESSURE"
      else -> "NEUTRAL / SIDEWAYS CHOP"
    }

    val summary = if (isBuy) {
      if (orderBookImbalance > 0) "Order Book Imbalance (+${String.format("%.0f%%", orderBookImbalance * 100)}) supports BUY momentum."
      else "Counter-trend scalp: tighter Stop Loss recommended."
    } else {
      if (orderBookImbalance < 0) "Order Book Imbalance (${String.format("%.0f%%", orderBookImbalance * 100)}) supports SHORT breakdown."
      else "Counter-trend scalp: risk guarded with strict SL."
    }

    return PreTradeCalculation(
      pair = pair,
      side = side,
      entryPrice = entryPrice,
      marginAmountUsd = marginUsd,
      leverage = leverage,
      positionSizeUsd = posSize,
      quantityCrypto = qty,
      tpPercent = tpPercent,
      tpPrice = tpPrice,
      expectedProfitUsd = expProfit,
      slPercent = slPercent,
      slPrice = slPrice,
      maxLossUsd = maxLoss,
      riskRewardRatio = rr,
      liquidationPrice = liqPrice,
      estExchangeFeeUsd = fee,
      marketImbalanceScore = orderBookImbalance,
      marketTrendSignal = trendSignal,
      marketAnalysisSummary = summary
    )
  }
}
