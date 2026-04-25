package com.nutrifit.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nome: String,
    val idade: Int,
    val sexo: String, // "M" ou "F"
    val alturaCm: Double,
    val pesoInicialKg: Double,
    val pesoAtualKg: Double,
    val pesoMetaKg: Double,
    val nivelAtividade: String, // sedentario, levemente_ativo, moderadamente_ativo, muito_ativo, extremamente_ativo
    val objetivo: String, // emagrecer, manter, ganhar_massa
    val created_at: Long = System.currentTimeMillis()
)
