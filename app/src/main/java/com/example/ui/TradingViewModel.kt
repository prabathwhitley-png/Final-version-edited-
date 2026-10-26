package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.ApiConnectionStatus
import com.example.data.api.ExchangeMode
import com.example.data.database.AppDatabase
import com.example.data.engine.TradingBotEngine
import com.example.data.model.BacktestConfig
import com.example.data.model.BacktestResult
import com.example.data.model.BotStatus
import com.example.data.model.BotStrategy
import com.example.data.model.CandleStick
import com.example.data.model.ExecutionSpeed
import com.example.data.model.HftTelemetry
import com.example.data.model.MarketScenario
import com.example.data.model.MarketTicker
import com.example.data.model.OrderBook
import com.example.data.model.OrderSide
import com.example.data.model.TradeOrder
import com.example.data.model.TradingBot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TradingViewModel(application: Application) : AndroidViewModel(application) {
  private val database = AppDatabase.getDatabase(application)
  val engine = TradingBotEngine(application, database)

  val allBots: StateFlow<List<TradingBot>> = database.botDao().getAllBots()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val recentOrders: StateFlow<List<TradeOrder>> = database.tradeOrderDao().getRecentOrders()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  val selectedPair: StateFlow<String> = engine.selectedPair
  val tickers: StateFlow<Map<String, MarketTicker>> = engine.tickers
  val candles: StateFlow<List<CandleStick>> = engine.candles
  val orderBook: StateFlow<OrderBook?> = engine.orderBook
  val telemetry: StateFlow<HftTelemetry> = engine.telemetry
  val apiConnection: StateFlow<ApiConnectionStatus> = engine.apiConnection
  val realAccountBalance: StateFlow<com.example.data.api.RealAccountBalance?> = engine.realAccountBalance
  val isOneDollarMode: StateFlow<Boolean> = engine.isOneDollarMode
  val engineEvents = engine.engineEvents

  // Backtest state
  private val _backtestConfig = MutableStateFlow(
    BacktestConfig(
      strategy = BotStrategy.FLASH_SCALP,
      pair = "BTC/USDT",
      initialCapitalUsd = 10000.0,
      leverage = 20,
      scenario = MarketScenario.BULL_MOMENTUM_RUN
    )
  )
  val backtestConfig: StateFlow<BacktestConfig> = _backtestConfig.asStateFlow()

  private val _backtestResult = MutableStateFlow<BacktestResult?>(null)
  val backtestResult: StateFlow<BacktestResult?> = _backtestResult.asStateFlow()

  private val _isBacktesting = MutableStateFlow(false)
  val isBacktesting: StateFlow<Boolean> = _isBacktesting.asStateFlow()

  init {
    runBacktest()
  }

  fun selectPair(pair: String) {
    engine.setPair(pair)
  }

  fun toggleOneDollarMode() {
    engine.toggleOneDollarMode()
  }

  fun toggleTurboBlitz() {
    engine.toggleTurboBlitz()
  }

  fun triggerEmergencyKill() {
    engine.emergencyKillSwitch()
  }

  fun resumeEngine() {
    engine.resumeAfterHalt()
  }

  fun toggleBotStatus(bot: TradingBot) {
    viewModelScope.launch {
      val newStatus = when (bot.status) {
        BotStatus.RUNNING -> BotStatus.PAUSED
        BotStatus.PAUSED -> BotStatus.RUNNING
        BotStatus.STOPPED -> BotStatus.RUNNING
      }
      database.botDao().updateBot(bot.copy(status = newStatus))
    }
  }

  fun deleteBot(bot: TradingBot) {
    viewModelScope.launch {
      database.botDao().deleteBot(bot)
    }
  }

  fun createBot(
    name: String,
    strategy: BotStrategy,
    pair: String,
    capitalUsd: Double,
    leverage: Int,
    speed: ExecutionSpeed,
    takeProfitPercent: Double,
    stopLossPercent: Double
  ) {
    viewModelScope.launch {
      val newBot = TradingBot(
        id = "bot-${UUID.randomUUID().toString().take(6)}",
        name = name.ifBlank { "${strategy.displayName} #${(10..99).random()}" },
        strategy = strategy,
        pair = pair,
        allocatedCapitalUsd = capitalUsd,
        leverage = leverage,
        speed = speed,
        targetTakeProfitPercent = takeProfitPercent,
        stopLossPercent = stopLossPercent,
        status = BotStatus.RUNNING,
        avgLatencyMs = strategy.defaultLatencyTargetMs
      )
      database.botDao().insertBot(newBot)
    }
  }

  fun manualTrade(side: OrderSide, amountUsd: Double) {
    engine.triggerManualSnipe(side, selectedPair.value, amountUsd)
  }

  fun executeRealOrder(
    pair: String,
    side: OrderSide,
    amountUsd: Double,
    onResult: (Boolean, String, TradeOrder?) -> Unit
  ) {
    viewModelScope.launch {
      val (success, receiptData) = engine.executeRealOrderOnBinance(pair, side, amountUsd)
      val (msg, order) = receiptData
      onResult(success, msg, order)
    }
  }

  fun testApiConnection(onResult: (ApiConnectionStatus) -> Unit) {
    viewModelScope.launch {
      val res = engine.apiClient.checkConnection()
      onResult(res)
    }
  }

  fun updateApiCredentials(key: String, secret: String, mode: ExchangeMode) {
    engine.apiClient.apiKey = key
    engine.apiClient.apiSecret = secret
    engine.apiClient.currentMode = mode
    testApiConnection {}
  }

  fun updateBacktestConfig(
    strategy: BotStrategy = _backtestConfig.value.strategy,
    pair: String = _backtestConfig.value.pair,
    initialCapital: Double = _backtestConfig.value.initialCapitalUsd,
    leverage: Int = _backtestConfig.value.leverage,
    scenario: MarketScenario = _backtestConfig.value.scenario
  ) {
    _backtestConfig.value = BacktestConfig(
      strategy = strategy,
      pair = pair,
      initialCapitalUsd = initialCapital,
      leverage = leverage,
      scenario = scenario
    )
  }

  fun runBacktest() {
    viewModelScope.launch {
      _isBacktesting.value = true
      kotlinx.coroutines.delay(400)
      _backtestResult.value = engine.runBacktest(_backtestConfig.value)
      _isBacktesting.value = false
    }
  }

  fun clearTradeHistory() {
    viewModelScope.launch {
      database.tradeOrderDao().clearAllOrders()
    }
  }
}
