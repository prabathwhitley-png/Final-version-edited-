package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class BinancePriceTicker(
  @param:Json(name = "symbol") val symbol: String,
  @param:Json(name = "price") val price: String
)

@JsonClass(generateAdapter = true)
data class BinanceTicker24Hr(
  @param:Json(name = "symbol") val symbol: String,
  @param:Json(name = "lastPrice") val lastPrice: String,
  @param:Json(name = "priceChange") val priceChange: String,
  @param:Json(name = "priceChangePercent") val priceChangePercent: String,
  @param:Json(name = "highPrice") val highPrice: String,
  @param:Json(name = "lowPrice") val lowPrice: String,
  @param:Json(name = "volume") val volume: String,
  @param:Json(name = "quoteVolume") val quoteVolume: String
)

@JsonClass(generateAdapter = true)
data class BinanceDepth(
  @param:Json(name = "lastUpdateId") val lastUpdateId: Long,
  @param:Json(name = "bids") val bids: List<List<String>>,
  @param:Json(name = "asks") val asks: List<List<String>>
)

@JsonClass(generateAdapter = true)
data class BinanceRecentTrade(
  @param:Json(name = "id") val id: Long,
  @param:Json(name = "price") val price: String,
  @param:Json(name = "qty") val qty: String,
  @param:Json(name = "quoteQty") val quoteQty: String,
  @param:Json(name = "time") val time: Long,
  @param:Json(name = "isBuyerMaker") val isBuyerMaker: Boolean
)

@JsonClass(generateAdapter = true)
data class BinanceServerTime(
  @param:Json(name = "serverTime") val serverTime: Long
)

@JsonClass(generateAdapter = true)
data class BinanceBalance(
  @param:Json(name = "asset") val asset: String,
  @param:Json(name = "free") val free: String,
  @param:Json(name = "locked") val locked: String
)

@JsonClass(generateAdapter = true)
data class BinanceAccountResponse(
  @param:Json(name = "canTrade") val canTrade: Boolean?,
  @param:Json(name = "canWithdraw") val canWithdraw: Boolean?,
  @param:Json(name = "canDeposit") val canDeposit: Boolean?,
  @param:Json(name = "updateTime") val updateTime: Long?,
  @param:Json(name = "accountType") val accountType: String?,
  @param:Json(name = "balances") val balances: List<BinanceBalance>?
)

@JsonClass(generateAdapter = true)
data class BinanceOrderResponse(
  @param:Json(name = "symbol") val symbol: String?,
  @param:Json(name = "orderId") val orderId: Long?,
  @param:Json(name = "clientOrderId") val clientOrderId: String?,
  @param:Json(name = "transactTime") val transactTime: Long?,
  @param:Json(name = "price") val price: String?,
  @param:Json(name = "origQty") val origQty: String?,
  @param:Json(name = "executedQty") val executedQty: String?,
  @param:Json(name = "cummulativeQuoteQty") val cummulativeQuoteQty: String?,
  @param:Json(name = "status") val status: String?
)

@JsonClass(generateAdapter = true)
data class BinanceApiError(
  @param:Json(name = "code") val code: Int?,
  @param:Json(name = "msg") val msg: String?
)
