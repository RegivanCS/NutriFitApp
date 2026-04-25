package com.nutrifit.app.data.local.dao

import androidx.room.*
import com.nutrifit.app.data.local.entities.ProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgressDao {
    @Query("""
        SELECT * FROM progress 
        WHERE userId = :userId 
        ORDER BY data ASC
    """)
    fun getAllProgress(userId: Int): Flow<List<ProgressEntity>>

    @Query("""
        SELECT * FROM progress 
        WHERE userId = :userId 
        ORDER BY data DESC 
        LIMIT 1
    """)
    suspend fun getLatestProgress(userId: Int): ProgressEntity?

    @Query("""
        SELECT * FROM progress 
        WHERE userId = :userId AND data BETWEEN :startDate AND :endDate 
        ORDER BY data ASC
    """)
    fun getProgressByPeriod(userId: Int, startDate: String, endDate: String): Flow<List<ProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: ProgressEntity): Long

    @Update
    suspend fun updateProgress(progress: ProgressEntity)

    @Delete
    suspend fun deleteProgress(progress: ProgressEntity)
}
