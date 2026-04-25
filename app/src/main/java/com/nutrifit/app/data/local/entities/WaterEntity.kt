package com.nutrifit.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water")
data class WaterEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val data: String, // YYYY-MM-DD
    val quantidade_ml: Double,
    val horario: String, // HH:mm
    val created_at: Long = System.currentTimeMillis()
)
