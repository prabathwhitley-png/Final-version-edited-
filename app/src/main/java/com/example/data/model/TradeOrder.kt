package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderSide {
  BUY,
  SELL
}

@Entity(tableName = "trade_orders")
data class TradeOrder(
  @PrimaryKey
  val id: String,
  val botId: String,
  val botName: String,
  val pair: String,
  val side: OrderSide,
  val price: Double,
  val amount: Double,
  val totalUsd: Double,
  val pnlUsd: Double,
  val pnlPercent: Double,
  val latencyMs: Double,
  val slippagePercent: Double,
  val route: String,
  val timestamp: Long = System.currentTimeMillis()
)
