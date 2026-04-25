package com.nutrifit.app.data.local.dao

import androidx.room.*
import com.nutrifit.app.data.local.entities.AchievementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementDao {
    @Query("SELECT * FROM achievements WHERE userId = :userId ORDER BY desbloqueado_em DESC")
    fun getUserAchievements(userId: Int): Flow<List<AchievementEntity>>

    @Query("SELECT COUNT(*) FROM achievements WHERE userId = :userId AND visto = 0")
    fun getUnseenCount(userId: Int): Flow<Int>

    @Query("SELECT * FROM achievements WHERE userId = :userId AND tipo = :tipo")
    suspend fun getAchievementByType(userId: Int, tipo: String): AchievementEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAchievement(achievement: AchievementEntity): Long

    @Query("UPDATE achievements SET visto = 1 WHERE userId = :userId AND id = :id")
    suspend fun markAsSeen(userId: Int, id: Int)
}
