package com.example.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface BinanceApiService {

  @GET("api/v3/ping")
  suspend fun ping(): Response<Unit>

  @GET("api/v3/time")
  suspend fun getServerTime(): BinanceServerTime

  @GET("api/v3/ticker/price")
  suspend fun getPrice(
    @Query("symbol") symbol: String
  ): BinancePriceTicker

  @GET("api/v3/ticker/24hr")
  suspend fun getTicker24Hr(
    @Query("symbol") symbol: String
  ): BinanceTicker24Hr

  @GET("api/v3/depth")
  suspend fun getDepth(
    @Query("symbol") symbol: String,
    @Query("limit") limit: Int = 10
  ): BinanceDepth

  @GET("api/v3/trades")
  suspend fun getRecentTrades(
    @Query("symbol") symbol: String,
    @Query("limit") limit: Int = 15
  ): List<BinanceRecentTrade>

  @GET("api/v3/klines")
  suspend fun getKlines(
    @Query("symbol") symbol: String,
    @Query("interval") interval: String = "1m",
    @Query("limit") limit: Int = 40
  ): List<List<Any>>

  // Real Account Balances & Permissions
  @GET("api/v3/account")
  suspend fun getAccountInformation(
    @Header("X-MBX-APIKEY") apiKey: String,
    @Query("timestamp") timestamp: Long,
    @Query("recvWindow") recvWindow: Long = 5000L,
    @Query("signature") signature: String
  ): Response<BinanceAccountResponse>

  // Test Order placement (Validates real order matching on exchange without spending real funds)
  @POST("api/v3/order/test")
  suspend fun testOrder(
    @Header("X-MBX-APIKEY") apiKey: String,
    @Query("symbol") symbol: String,
    @Query("side") side: String,
    @Query("type") type: String = "MARKET",
    @Query("quoteOrderQty") quoteOrderQty: String,
    @Query("timestamp") timestamp: Long,
    @Query("recvWindow") recvWindow: Long = 5000L,
    @Query("signature") signature: String
  ): Response<Unit>

  // Real Order placement (For Live Real Money / Testnet execution)
  @POST("api/v3/order")
  suspend fun placeOrder(
    @Header("X-MBX-APIKEY") apiKey: String,
    @Query("symbol") symbol: String,
    @Query("side") side: String,
    @Query("type") type: String = "MARKET",
    @Query("quoteOrderQty") quoteOrderQty: String,
    @Query("timestamp") timestamp: Long,
    @Query("recvWindow") recvWindow: Long = 5000L,
    @Query("signature") signature: String
  ): Response<BinanceOrderResponse>
}
