package com.example.weatherapp.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alerts_table")
data class Alerts(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val triggerType: String,
    val thresholdValue: Int,
    val deliveryType: String,
    val startDate: Long,
    val endDate: Long,
    val isEnabled: Boolean = true
)
