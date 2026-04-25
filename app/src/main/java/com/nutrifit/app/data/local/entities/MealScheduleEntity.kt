package com.nutrifit.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meal_schedule")
data class MealScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val tipo: String, // cafe_da_manha, lanche_manha, almoco, lanche_tarde, jantar, ceia
    val horario: String, // HH:mm
    val nome: String = "", // Nome sugerido ou personalizado
    val ativo: Boolean = true,
    val created_at: Long = System.currentTimeMillis()
)