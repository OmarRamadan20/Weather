package com.example.weatherapp.data.config.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.weatherapp.data.models.Alerts
import kotlinx.coroutines.flow.Flow

@Dao
interface AlertsDao {

    @Query("SELECT * FROM alerts_table")
    fun getAllAlerts(): Flow<List<Alerts>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: Alerts):Long

    @Delete
    suspend fun deleteAlert(alert: Alerts)

    @Query("UPDATE alerts_table SET isEnabled = :isEnabled WHERE id = :alertId")
    suspend fun updateAlertStatus(alertId: Int, isEnabled: Boolean)

    @Query("SELECT * FROM alerts_table WHERE id = :alertId")
    suspend fun getAlertById(alertId: Int): Alerts?
}