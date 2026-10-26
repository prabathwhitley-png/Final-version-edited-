package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.BotStrategy
import com.example.data.model.OrderSide
import com.example.data.model.RiskCalculator
import com.example.data.model.TradingBot
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("ApexTrade", appName)
  }

  @Test
  fun `verify trading bot win rate and profit calculations`() {
    val bot = TradingBot(
      id = "test-bot",
      name = "Test FlashScalp",
      strategy = BotStrategy.FLASH_SCALP,
      pair = "BTC/USDT",
      allocatedCapitalUsd = 10000.0,
      totalTrades = 100,
      winningTrades = 80,
      netProfitUsd = 2500.0
    )
    assertEquals(80.0, bot.winRatePercent, 0.001)
    assertEquals(25.0, bot.profitPercent, 0.001)
    assertTrue(bot.winRatePercent > 50.0)
  }

  @Test
  fun `verify pre-trade risk calculator TP SL leverage and quantity`() {
    val calc = RiskCalculator.calculate(
      pair = "BTC/USDT",
      side = OrderSide.BUY,
      entryPrice = 80000.0,
      marginUsd = 1.0,
      leverage = 10,
      tpPercent = 1.5,
      slPercent = 0.75,
      orderBookImbalance = 0.35
    )

    // Position size = Margin $1.00 * 10x = $10.00
    assertEquals(10.0, calc.positionSizeUsd, 0.001)
    // Crypto quantity = $10.00 / 80000 = 0.000125 BTC
    assertEquals(0.000125, calc.quantityCrypto, 0.000001)
    // TP price = 80000 * 1.015 = 81200
    assertEquals(81200.0, calc.tpPrice, 0.01)
    // SL price = 80000 * 0.9925 = 79400
    assertEquals(79400.0, calc.slPrice, 0.01)
    // Expected profit = 10 * 0.015 = 0.15
    assertEquals(0.15, calc.expectedProfitUsd, 0.001)
    // Max loss = 10 * 0.0075 = 0.075
    assertEquals(0.075, calc.maxLossUsd, 0.001)
    // Risk / Reward ratio = 1.5 / 0.75 = 2.0
    assertEquals(2.0, calc.riskRewardRatio, 0.001)
    assertTrue(calc.liquidationPrice < calc.entryPrice)
  }

  @Test
  fun `verify pre-trade risk calculator for sell short order`() {
    val calc = RiskCalculator.calculate(
      pair = "ETH/USDT",
      side = OrderSide.SELL,
      entryPrice = 3000.0,
      marginUsd = 5.0,
      leverage = 20,
      tpPercent = 2.0,
      slPercent = 1.0,
      orderBookImbalance = -0.4
    )

    // Position size = $5 * 20x = $100
    assertEquals(100.0, calc.positionSizeUsd, 0.001)
    // Crypto quantity = 100 / 3000 = 0.033333 ETH
    assertEquals(100.0 / 3000.0, calc.quantityCrypto, 0.0001)
    // For SELL, TP is below entry price: 3000 * (1 - 0.02) = 2940
    assertEquals(2940.0, calc.tpPrice, 0.01)
    // For SELL, SL is above entry price: 3000 * (1 + 0.01) = 3030
    assertEquals(3030.0, calc.slPrice, 0.01)
    // Liquidation price is higher than entry price for shorts
    assertTrue(calc.liquidationPrice > calc.entryPrice)
  }
}
