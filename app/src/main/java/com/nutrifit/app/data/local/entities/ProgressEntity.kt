package com.nutrifit.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class ProgressEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val data: String, // YYYY-MM-DD
    val pesoKg: Double,
    val cinturaCm: Double? = null,
    val quadrilCm: Double? = null,
    val bracoCm: Double? = null,
    val fotoPath: String? = null, // Caminho da foto do progresso
    val created_at: Long = System.currentTimeMillis()
)
