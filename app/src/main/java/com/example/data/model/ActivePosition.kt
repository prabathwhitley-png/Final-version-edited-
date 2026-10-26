package com.example.data.model

data class ActivePosition(
  val id: String,
  val botId: String,
  val botName: String,
  val pair: String,
  val side: OrderSide,
  val entryPrice: Double,
  val currentPrice: Double,
  val marginUsd: Double,
  val leverage: Int,
  val positionSizeUsd: Double,
  val quantityCrypto: Double,
  val tpPrice: Double,
  val slPrice: Double,
  val unrealizedPnlUsd: Double,
  val unrealizedPnlPercent: Double,
  val openedAt: Long = System.currentTimeMillis()
)
