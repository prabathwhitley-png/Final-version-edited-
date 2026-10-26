package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.api.ApiConnectionStatus
import com.example.ui.TradingViewModel
import com.example.ui.components.ApiSettingsDialog
import com.example.ui.components.RealOrderDialog
import com.example.ui.screens.BacktestScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.LiveTerminalScreen
import com.example.ui.screens.TradeHistoryScreen
import com.example.ui.theme.CandleGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonRed
import com.example.ui.theme.RobotoMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

enum class MainTab(val title: String, val icon: ImageVector) {
  DASHBOARD("Fleet", Icons.Default.Dashboard),
  TERMINAL("Terminal", Icons.AutoMirrored.Filled.ShowChart),
  BACKTEST("Strategy Lab", Icons.Default.AutoGraph),
  HISTORY("Audit Log", Icons.Default.History)
}

class MainActivity : ComponentActivity() {
  private val viewModel: TradingViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        MainScreen(viewModel = viewModel)
      }
    }
  }
}

@Composable
fun MainScreen(viewModel: TradingViewModel) {
  var selectedTab by remember { mutableStateOf(MainTab.DASHBOARD) }
  var showRealOrderDialog by remember { mutableStateOf(false) }
  var showApiSettingsDialog by remember { mutableStateOf(false) }

  val snackbarHostState = remember { SnackbarHostState() }

  val bots by viewModel.allBots.collectAsStateWithLifecycle()
  val recentOrders by viewModel.recentOrders.collectAsStateWithLifecycle()
  val selectedPair by viewModel.selectedPair.collectAsStateWithLifecycle()
  val tickers by viewModel.tickers.collectAsStateWithLifecycle()
  val candles by viewModel.candles.collectAsStateWithLifecycle()
  val orderBook by viewModel.orderBook.collectAsStateWithLifecycle()
  val telemetry by viewModel.telemetry.collectAsStateWithLifecycle()
  val apiConnection by viewModel.apiConnection.collectAsStateWithLifecycle()
  val realAccountBalance by viewModel.realAccountBalance.collectAsStateWithLifecycle()
  val isOneDollarMode by viewModel.isOneDollarMode.collectAsStateWithLifecycle()
  val backtestConfig by viewModel.backtestConfig.collectAsStateWithLifecycle()
  val backtestResult by viewModel.backtestResult.collectAsStateWithLifecycle()
  val isBacktesting by viewModel.isBacktesting.collectAsStateWithLifecycle()

  // Handle system back gesture
  BackHandler(enabled = selectedTab != MainTab.DASHBOARD) {
    selectedTab = MainTab.DASHBOARD
  }

  // Collect engine event messages for Snackbars
  LaunchedEffect(Unit) {
    viewModel.engineEvents.collectLatest { msg ->
      snackbarHostState.showSnackbar(
        message = msg,
        withDismissAction = true
      )
    }
  }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = SurfaceDark,
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      TopNavBar(
        telemetry = telemetry,
        apiConnection = apiConnection,
        isOneDollarMode = isOneDollarMode,
        onToggleBlitz = { viewModel.toggleTurboBlitz() },
        onOpenRealOrder = { showRealOrderDialog = true },
        onOpenApiSettings = { showApiSettingsDialog = true }
      )
    },
    bottomBar = {
      NavigationBar(
        modifier = Modifier
          .navigationBarsPadding()
          .testTag("main_bottom_nav"),
        containerColor = SurfaceCard,
        tonalElevation = 0.dp
      ) {
        MainTab.values().forEach { tab ->
          val isSel = tab == selectedTab
          NavigationBarItem(
            selected = isSel,
            onClick = { selectedTab = tab },
            modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}"),
            icon = {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.title,
                tint = if (isSel) NeonCyan else TextMuted
              )
            },
            label = {
              Text(
                text = tab.title,
                fontFamily = SpaceGrotesk,
                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                fontSize = 11.sp,
                color = if (isSel) NeonCyan else TextMuted
              )
            },
            colors = NavigationBarItemDefaults.colors(
              indicatorColor = NeonCyan.copy(alpha = 0.15f)
            )
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        MainTab.DASHBOARD -> {
          DashboardScreen(
            bots = bots,
            telemetry = telemetry,
            tickers = tickers,
            apiConnection = apiConnection,
            realAccountBalance = realAccountBalance,
            isOneDollarMode = isOneDollarMode,
            onToggleOneDollarMode = { viewModel.toggleOneDollarMode() },
            onMakeRealOrder = { showRealOrderDialog = true },
            onOpenApiSettings = { showApiSettingsDialog = true },
            onToggleStatus = { viewModel.toggleBotStatus(it) },
            onDeleteBot = { viewModel.deleteBot(it) },
            onCreateBot = { name, strategy, pair, capital, leverage, speed, tp, sl ->
              viewModel.createBot(name, strategy, pair, capital, leverage, speed, tp, sl)
            },
            onToggleBlitz = { viewModel.toggleTurboBlitz() },
            onEmergencyKill = { viewModel.triggerEmergencyKill() },
            onResumeEngine = { viewModel.resumeEngine() },
            onSelectPair = {
              viewModel.selectPair(it)
              selectedTab = MainTab.TERMINAL
            }
          )
        }

        MainTab.TERMINAL -> {
          LiveTerminalScreen(
            selectedPair = selectedPair,
            ticker = tickers[selectedPair],
            candles = candles,
            orderBook = orderBook,
            recentOrders = recentOrders,
            onSelectPair = { viewModel.selectPair(it) },
            onMakeRealOrder = { showRealOrderDialog = true },
            onManualSnipe = { side, amount -> viewModel.manualTrade(side, amount) }
          )
        }

        MainTab.BACKTEST -> {
          BacktestScreen(
            config = backtestConfig,
            result = backtestResult,
            isBacktesting = isBacktesting,
            onUpdateConfig = { strategy, pair, capital, leverage, scenario ->
              viewModel.updateBacktestConfig(strategy, pair, capital, leverage, scenario)
            },
            onRunBacktest = { viewModel.runBacktest() }
          )
        }

        MainTab.HISTORY -> {
          TradeHistoryScreen(
            orders = recentOrders,
            onClearHistory = { viewModel.clearTradeHistory() }
          )
        }
      }
    }
  }

  // Make Real Order Dialog
  if (showRealOrderDialog) {
    val livePrice = tickers[selectedPair]?.lastPrice ?: (candles.lastOrNull()?.close ?: 80000.0)
    val imbalance = orderBook?.imbalanceRatio ?: 0.0
    RealOrderDialog(
      currentPair = selectedPair,
      livePrice = livePrice,
      orderBookImbalance = imbalance,
      apiConnection = apiConnection,
      onDismiss = { showRealOrderDialog = false },
      onOpenSettings = {
        showRealOrderDialog = false
        showApiSettingsDialog = true
      },
      onExecuteOrder = { pair, side, amountUsd, onResult ->
        viewModel.executeRealOrder(pair, side, amountUsd, onResult)
      }
    )
  }

  // API Credentials Settings Dialog
  if (showApiSettingsDialog) {
    ApiSettingsDialog(
      initialKey = viewModel.engine.apiClient.apiKey,
      initialSecret = viewModel.engine.apiClient.apiSecret,
      initialMode = viewModel.engine.apiClient.currentMode,
      currentStatus = apiConnection,
      onDismiss = { showApiSettingsDialog = false },
      onSave = { key, secret, mode ->
        viewModel.updateApiCredentials(key, secret, mode)
      },
      onTestConnection = { onResult ->
        viewModel.testApiConnection(onResult)
      }
    )
  }
}

@Composable
fun TopNavBar(
  telemetry: com.example.data.model.HftTelemetry,
  apiConnection: ApiConnectionStatus,
  isOneDollarMode: Boolean,
  onToggleBlitz: () -> Unit,
  onOpenRealOrder: () -> Unit,
  onOpenApiSettings: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .background(SurfaceCard)
      .statusBarsPadding()
      .padding(horizontal = 12.dp, vertical = 8.dp)
      .testTag("top_nav_bar")
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      // App Branding + Real API Status
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(if (apiConnection.isConnected) CandleGreen else NeonAmber)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "APEXTRADE",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = TextPrimary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (apiConnection.isConnected) "BINANCE API" else "CONNECTING",
          fontFamily = RobotoMono,
          fontWeight = FontWeight.Bold,
          fontSize = 9.sp,
          color = if (apiConnection.isConnected) CandleGreen else NeonAmber
        )
      }

      // Fast Order Button + Settings Key
      Row(verticalAlignment = Alignment.CenterVertically) {
        // Fast "1$ ORDER" trigger
        Box(
          modifier = Modifier
            .background(CandleGreen.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .border(1.dp, CandleGreen, RoundedCornerShape(16.dp))
            .clickable { onOpenRealOrder() }
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag("top_fast_real_order_btn")
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.FlashOn,
              contentDescription = "Real Order",
              tint = CandleGreen,
              modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "REAL ORDER",
              fontFamily = SpaceGrotesk,
              fontWeight = FontWeight.Bold,
              fontSize = 10.sp,
              color = CandleGreen
            )
          }
        }

        Spacer(modifier = Modifier.width(6.dp))

        // API Settings Key Button
        IconButton(
          onClick = onOpenApiSettings,
          modifier = Modifier.size(32.dp).testTag("top_api_settings_btn")
        ) {
          Icon(
            imageVector = Icons.Default.Key,
            contentDescription = "API Keys",
            tint = NeonCyan,
            modifier = Modifier.size(16.dp)
          )
        }
      }
    }
  }
}
