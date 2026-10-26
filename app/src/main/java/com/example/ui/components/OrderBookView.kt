package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderBook
import com.example.ui.theme.CandleGreen
import com.example.ui.theme.CandleRed
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.OrderAskBar
import com.example.ui.theme.OrderBidBar
import com.example.ui.theme.RobotoMono
import com.example.ui.theme.SpaceGrotesk
import com.example.ui.theme.SurfaceBorder
import com.example.ui.theme.SurfaceCard
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OrderBookView(
  orderBook: OrderBook?,
  currentPrice: Double,
  modifier: Modifier = Modifier
) {
  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("order_book_card"),
    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
    shape = RoundedCornerShape(12.dp),
    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(SurfaceBorder))
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "L2 ORDER BOOK",
            fontFamily = SpaceGrotesk,
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = TextPrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "SUB-MS DEPTH",
            fontFamily = RobotoMono,
            fontSize = 9.sp,
            color = NeonCyan
          )
        }

        if (orderBook != null) {
          Text(
            text = String.format("Spread: $%.2f (%.1f bps)", orderBook.spreadUsd, orderBook.spreadBps),
            fontFamily = RobotoMono,
            fontSize = 10.sp,
            color = TextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Imbalance Bar
      if (orderBook != null) {
        val imbalance = orderBook.imbalanceRatio // -1.0 to 1.0
        val bidRatio = ((imbalance + 1.0) / 2.0).toFloat().coerceIn(0.05f, 0.95f)
        val askRatio = 1f - bidRatio

        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = String.format("BUY PRESS %.0f%%", bidRatio * 100),
              fontFamily = RobotoMono,
              fontSize = 9.sp,
              color = CandleGreen
            )
            Text(
              text = String.format("SELL PRESS %.0f%%", askRatio * 100),
              fontFamily = RobotoMono,
              fontSize = 9.sp,
              color = CandleRed
            )
          }

          Spacer(modifier = Modifier.height(3.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .height(4.dp)
              .background(SurfaceBorder, RoundedCornerShape(2.dp))
          ) {
            Box(
              modifier = Modifier
                .weight(bidRatio)
                .fillMaxHeight()
                .background(CandleGreen, RoundedCornerShape(topStart = 2.dp, bottomStart = 2.dp))
            )
            Box(
              modifier = Modifier
                .weight(askRatio)
                .fillMaxHeight()
                .background(CandleRed, RoundedCornerShape(topEnd = 2.dp, bottomEnd = 2.dp))
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Columns header
      Row(modifier = Modifier.fillMaxWidth()) {
        Text(
          text = "BID QTY",
          fontFamily = RobotoMono,
          fontSize = 9.sp,
          color = TextMuted,
          modifier = Modifier.weight(1f)
        )
        Text(
          text = "PRICE (USD)",
          fontFamily = RobotoMono,
          fontSize = 9.sp,
          color = TextMuted,
          modifier = Modifier.weight(1.2f)
        )
        Text(
          text = "ASK QTY",
          fontFamily = RobotoMono,
          fontSize = 9.sp,
          color = TextMuted,
          modifier = Modifier.weight(1f),
          textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
      }

      Spacer(modifier = Modifier.height(4.dp))

      // Display Top 5 Bids and Asks side by side
      val bids = orderBook?.bids?.take(5) ?: emptyList()
      val asks = orderBook?.asks?.take(5) ?: emptyList()
      val rowsCount = maxOf(bids.size, asks.size)

      for (i in 0 until rowsCount) {
        val bid = bids.getOrNull(i)
        val ask = asks.getOrNull(i)

        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(20.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Bid side (with depth bar)
          Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (bid != null) {
              Box(
                modifier = Modifier
                  .fillMaxHeight()
                  .fillMaxWidth(bid.depthPercent.coerceIn(0.1f, 1f))
                  .background(OrderBidBar)
              )
              Text(
                text = String.format("%.3f", bid.size),
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = CandleGreen,
                modifier = Modifier.padding(start = 2.dp)
              )
            }
          }

          // Center prices
          Row(
            modifier = Modifier.weight(1.2f),
            horizontalArrangement = Arrangement.Center
          ) {
            if (bid != null) {
              Text(
                text = String.format("%.1f", bid.price),
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = TextPrimary
              )
            }
            Text(
              text = " / ",
              fontFamily = RobotoMono,
              fontSize = 10.sp,
              color = TextMuted
            )
            if (ask != null) {
              Text(
                text = String.format("%.1f", ask.price),
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = TextPrimary
              )
            }
          }

          // Ask side (with depth bar)
          Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) {
            if (ask != null) {
              Box(
                modifier = Modifier
                  .fillMaxHeight()
                  .fillMaxWidth(ask.depthPercent.coerceIn(0.1f, 1f))
                  .align(Alignment.CenterEnd)
                  .background(OrderAskBar)
              )
              Text(
                text = String.format("%.3f", ask.size),
                fontFamily = RobotoMono,
                fontSize = 11.sp,
                color = CandleRed,
                modifier = Modifier.padding(end = 2.dp)
              )
            }
          }
        }
      }
    }
  }
}
