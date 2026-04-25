package com.nutrifit.app.data.local.dao

import androidx.room.*
import com.nutrifit.app.data.local.entities.WaterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterDao {
    @Query("""
        SELECT * FROM water 
        WHERE userId = :userId AND data = :data 
        ORDER BY horario ASC
    """)
    fun getWaterByDate(userId: Int, data: String): Flow<List<WaterEntity>>

    @Query("""
        SELECT SUM(quantidade_ml) FROM water 
        WHERE userId = :userId AND data = :data
    """)
    fun getTotalWaterByDate(userId: Int, data: String): Flow<Double?>

    @Query("""
        SELECT COUNT(*) FROM water 
        WHERE userId = :userId AND data = :data
    """)
    fun getWaterEntriesCount(userId: Int, data: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWater(water: WaterEntity): Long

    @Delete
    suspend fun deleteWater(water: WaterEntity)
}
