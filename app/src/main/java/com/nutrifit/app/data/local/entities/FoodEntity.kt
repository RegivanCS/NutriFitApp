package com.nutrifit.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "foods")
data class FoodEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val porcao_g: Double = 100.0,
    val calorias: Double,
    val proteinas: Double,
    val carboidratos: Double,
    val gorduras: Double,
    val fibras: Double = 0.0,
    val categoria: String, // graos, carnes, frutas, verduras, laticinios, oleos, doces, bebidas
    val codigo_barras: String = "" // Para scanner
)
