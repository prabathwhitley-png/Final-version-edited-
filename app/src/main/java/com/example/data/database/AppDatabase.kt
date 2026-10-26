package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.TradeOrder
import com.example.data.model.TradingBot

@Database(entities = [TradingBot::class, TradeOrder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun botDao(): BotDao
  abstract fun tradeOrderDao(): TradeOrderDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "apex_trade_db"
        ).fallbackToDestructiveMigration(true).build()
        INSTANCE = instance
        instance
      }
    }
  }
}
