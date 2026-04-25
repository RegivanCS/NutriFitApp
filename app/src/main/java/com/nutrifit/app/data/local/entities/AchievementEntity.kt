package com.nutrifit.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val tipo: String, // streak, peso, consistencia, agua, etc
    val titulo: String,
    val descricao: String,
    val icone: String, // nome do ícone
    val desbloqueado_em: Long = System.currentTimeMillis(),
    val visto: Boolean = false
)
