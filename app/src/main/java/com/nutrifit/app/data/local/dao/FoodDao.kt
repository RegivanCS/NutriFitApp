package com.nutrifit.app.data.local.dao

import androidx.room.*
import com.nutrifit.app.data.local.entities.FoodEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodDao {
    @Query("SELECT * FROM foods WHERE nome LIKE '%' || :query || '%' LIMIT 20")
    fun searchFoods(query: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE categoria = :category ORDER BY nome ASC")
    fun getFoodsByCategory(category: String): Flow<List<FoodEntity>>

    @Query("SELECT * FROM foods WHERE codigo_barras = :barcode LIMIT 1")
    suspend fun getFoodByBarcode(barcode: String): FoodEntity?

    @Query("SELECT * FROM foods ORDER BY nome ASC")
    fun getAllFoods(): Flow<List<FoodEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(food: FoodEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(foods: List<FoodEntity>)

    @Update
    suspend fun update(food: FoodEntity)

    @Delete
    suspend fun delete(food: FoodEntity)
}
