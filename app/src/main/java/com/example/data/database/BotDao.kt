package com.example.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.TradingBot
import kotlinx.coroutines.flow.Flow

@Dao
interface BotDao {
  @Query("SELECT * FROM trading_bots ORDER BY createdAt DESC")
  fun getAllBots(): Flow<List<TradingBot>>

  @Query("SELECT * FROM trading_bots WHERE id = :id")
  suspend fun getBotById(id: String): TradingBot?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBot(bot: TradingBot)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertBots(bots: List<TradingBot>)

  @Update
  suspend fun updateBot(bot: TradingBot)

  @Delete
  suspend fun deleteBot(bot: TradingBot)

  @Query("DELETE FROM trading_bots WHERE id = :botId")
  suspend fun deleteBotById(botId: String)

  @Query("UPDATE trading_bots SET status = 'PAUSED'")
  suspend fun pauseAllBots()

  @Query("UPDATE trading_bots SET status = 'RUNNING'")
  suspend fun startAllBots()
}
