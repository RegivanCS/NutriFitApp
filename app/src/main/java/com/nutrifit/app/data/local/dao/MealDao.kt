package com.nutrifit.app.data.local.dao

import androidx.room.*
import com.nutrifit.app.data.local.entities.MealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {
    @Query("""
        SELECT * FROM meals 
        WHERE userId = :userId AND data = :data 
        ORDER BY 
            CASE tipo
                WHEN 'cafe_da_manha' THEN 1
                WHEN 'lanche_manha' THEN 2
                WHEN 'almoco' THEN 3
                WHEN 'lanche_tarde' THEN 4
                WHEN 'jantar' THEN 5
            END
    """)
    fun getMealsByDate(userId: Int, data: String): Flow<List<MealEntity>>

    @Query("SELECT * FROM meals WHERE userId = :userId AND data = :data")
    suspend fun getMealsByDateSync(userId: Int, data: String): List<MealEntity>

    @Query("""
        SELECT SUM(calorias) FROM meals 
        WHERE userId = :userId AND data = :data
    """)
    fun getTotalCaloriesByDate(userId: Int, data: String): Flow<Double?>

    @Query("""
        SELECT SUM(calorias) FROM meals 
        WHERE userId = :userId AND data = :data
    """)
    suspend fun getTotalCaloriesByDateSync(userId: Int, data: String): Double?

    @Query("""
        SELECT 
            SUM(calorias) as total_calorias,
            SUM(proteinas) as total_proteinas,
            SUM(carboidratos) as total_carboidratos,
            SUM(gorduras) as total_gorduras,
            SUM(fibras) as total_fibras
        FROM meals 
        WHERE userId = :userId AND data = :data
    """)
    fun getDailyTotals(userId: Int, data: String): Flow<DailyTotals?>

    @Query("""
        SELECT data, SUM(calorias) as total_calorias
        FROM meals 
        WHERE userId = :userId AND data BETWEEN :startDate AND :endDate
        GROUP BY data
        ORDER BY data ASC
    """)
    fun getCaloriesHistory(userId: Int, startDate: String, endDate: String): Flow<List<DailyCalories>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntity): Long

    @Update
    suspend fun updateMeal(meal: MealEntity)

    @Delete
    suspend fun deleteMeal(meal: MealEntity)

    @Query("DELETE FROM meals WHERE id = :id")
    suspend fun deleteMealById(id: Int)
}

data class DailyTotals(
    val total_calorias: Double?,
    val total_proteinas: Double?,
    val total_carboidratos: Double?,
    val total_gorduras: Double?,
    val total_fibras: Double?
)

data class DailyCalories(
    val data: String,
    val total_calorias: Double?
)
