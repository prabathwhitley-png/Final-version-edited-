package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.TradeOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface TradeOrderDao {
  @Query("SELECT * FROM trade_orders ORDER BY timestamp DESC LIMIT 200")
  fun getRecentOrders(): Flow<List<TradeOrder>>

  @Query("SELECT * FROM trade_orders WHERE botId = :botId ORDER BY timestamp DESC")
  fun getOrdersByBot(botId: String): Flow<List<TradeOrder>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrder(order: TradeOrder)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertOrders(orders: List<TradeOrder>)

  @Query("DELETE FROM trade_orders")
  suspend fun clearAllOrders()

  @Query("SELECT COUNT(*) FROM trade_orders")
  suspend fun getOrderCount(): Int
}
