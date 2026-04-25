package com.nutrifit.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meals")
data class MealEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val data: String, // YYYY-MM-DD
    val tipo: String, // cafe_da_manha, lanche_manha, almoco, lanche_tarde, jantar
    val horario: String, // HH:mm
    val nome: String,
    val calorias: Double,
    val proteinas: Double,
    val carboidratos: Double,
    val gorduras: Double,
    val fibras: Double = 0.0,
    val notas: String = "",
    val created_at: Long = System.currentTimeMillis()
)
