package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cable
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Key
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.api.ApiConnectionStatus
import com.example.data.api.ExchangeMode
import com.example.ui.theme.CandleGreen
import com.example.ui.theme.CandleRed
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.RobotoMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun ApiSettingsDialog(
  initialKey: String,
  initialSecret: String,
  initialMode: ExchangeMode,
  currentStatus: ApiConnectionStatus,
  onDismiss: () -> Unit,
  onSave: (key: String, secret: String, mode: ExchangeMode) -> Unit,
  onTestConnection: (onResult: (ApiConnectionStatus) -> Unit) -> Unit
) {
  var apiKey by remember { mutableStateOf(initialKey) }
  var apiSecret by remember { mutableStateOf(initialSecret) }
  var selectedMode by remember { mutableStateOf(initialMode) }
  var isTesting by remember { mutableStateOf(false) }
  var testResult by remember { mutableStateOf<ApiConnectionStatus?>(null) }
  val focusManager = LocalFocusManager.current

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = SurfaceCard,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Key,
          contentDescription = "Exchange Settings",
          tint = NeonCyan,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "EXCHANGE API CREDENTIALS",
          fontFamily = SpaceGrotesk,
          fontWeight = FontWeight.Bold,
          fontSize = 16.sp,
          color = TextPrimary
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .verticalScroll(rememberScrollState())
          .fillMaxWidth()
      ) {
        // Exchange Network Mode
        Text(text = "EXCHANGE NETWORK", fontFamily = SpaceGrotesk, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        ExchangeMode.values().forEach { mode ->
          val isSel = mode == selectedMode
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 3.dp)
              .background(if (isSel) NeonCyan.copy(alpha = 0.15f) else SurfaceDark, RoundedCornerShape(8.dp))
              .border(1.dp, if (isSel) NeonCyan else SurfaceBorder, RoundedCornerShape(8.dp))
              .clickable { selectedMode = mode }
              .padding(10.dp)
          ) {
            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text(
                  text = mode.displayName,
                  fontFamily = SpaceGrotesk,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp,
                  color = if (isSel) NeonCyan else TextPrimary
                )
                if (mode.isLiveMoney) {
                  Box(
                    modifier = Modifier
                      .background(NeonAmber.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                      .padding(horizontal = 4.dp, vertical = 2.dp)
                  ) {
                    Text(text = "REAL FUNDS", fontFamily = RobotoMono, fontSize = 8.sp, color = NeonAmber)
                  }
                } else {
                  Box(
                    modifier = Modifier
                      .background(CandleGreen.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                      .padding(horizontal = 4.dp, vertical = 2.dp)
                  ) {
                    Text(text = "SAFE 1$ MODE", fontFamily = RobotoMono, fontSize = 8.sp, color = CandleGreen)
                  }
                }
              }
              Text(
                text = mode.baseUrl,
                fontFamily = RobotoMono,
                fontSize = 9.sp,
                color = TextMuted
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // API Key Field
        OutlinedTextField(
          value = apiKey,
          onValueChange = { apiKey = it },
          label = { Text("Binance API Key", fontFamily = SpaceGrotesk) },
          placeholder = { Text("Paste your 64-char API key", fontFamily = RobotoMono, color = TextMuted, fontSize = 10.sp) },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth().testTag("api_key_input"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // API Secret Field
        OutlinedTextField(
          value = apiSecret,
          onValueChange = { apiSecret = it },
          label = { Text("Binance API Secret", fontFamily = SpaceGrotesk) },
          placeholder = { Text("Paste your HMAC secret", fontFamily = RobotoMono, color = TextMuted, fontSize = 10.sp) },
          visualTransformation = PasswordVisualTransformation(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = NeonCyan,
            unfocusedBorderColor = SurfaceBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary
          ),
          modifier = Modifier.fillMaxWidth().testTag("api_secret_input"),
          singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Live Test Connection Button
        Button(
          onClick = {
            isTesting = true
            onTestConnection { res ->
              isTesting = false
              testResult = res
            }
          },
          enabled = !isTesting,
          modifier = Modifier.fillMaxWidth().height(38.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SurfaceDark, contentColor = NeonCyan),
          border = androidx.compose.foundation.BorderStroke(1.dp, NeonCyan),
          shape = RoundedCornerShape(8.dp)
        ) {
          if (isTesting) {
            CircularProgressIndicator(modifier = Modifier.size(14.dp), color = NeonCyan, strokeWidth = 2.dp)
            Spacer(modifier = Modifier.width(6.dp))
            Text("PINGING BINANCE API...", fontFamily = SpaceGrotesk, fontSize = 11.sp)
          } else {
            Icon(imageVector = Icons.Default.Cable, contentDescription = "Test", modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("TEST LIVE EXCHANGE PING", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold, fontSize = 11.sp)
          }
        }

        if (testResult != null) {
          Spacer(modifier = Modifier.height(8.dp))
          val r = testResult!!
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(if (r.isConnected) CandleGreen.copy(alpha = 0.15f) else CandleRed.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
              .padding(8.dp)
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (r.isConnected) Icons.Default.CheckCircle else Icons.Default.Cable,
                contentDescription = null,
                tint = if (r.isConnected) CandleGreen else CandleRed,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Column {
                Text(
                  text = if (r.isConnected) "CONNECTION SUCCESS" else "FAILED TO REACH API",
                  fontFamily = SpaceGrotesk,
                  fontWeight = FontWeight.Bold,
                  fontSize = 11.sp,
                  color = if (r.isConnected) CandleGreen else CandleRed
                )
                Text(
                  text = if (r.isConnected) "Roundtrip Ping: ${r.pingMs} ms to Binance Server" else (r.lastError ?: "Timeout"),
                  fontFamily = RobotoMono,
                  fontSize = 9.sp,
                  color = TextSecondary
                )
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          focusManager.clearFocus()
          onSave(apiKey, apiSecret, selectedMode)
          onDismiss()
        },
        colors = ButtonDefaults.buttonColors(containerColor = NeonCyan, contentColor = SurfaceDark),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.testTag("save_api_settings_btn")
      ) {
        Text("SAVE CREDENTIALS", fontFamily = SpaceGrotesk, fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      OutlinedButton(
        onClick = {
          focusManager.clearFocus()
          onDismiss()
        },
        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextSecondary),
        shape = RoundedCornerShape(8.dp)
      ) {
        Text("CANCEL", fontFamily = SpaceGrotesk)
      }
    }
  )
}
