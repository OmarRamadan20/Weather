package com.example.weather.data.config.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.weatherapp.data.config.db.AlertsDao
import com.example.weatherapp.data.models.Alerts

@Database(entities = [FavLocation::class, Alerts::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun weatherDao(): WeatherDao
    abstract fun alertsDao(): AlertsDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "weather_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}