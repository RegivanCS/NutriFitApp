package com.nutrifit.app.data.local.dao

import androidx.room.*
import com.nutrifit.app.data.local.entities.MealScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealScheduleDao {
    @Query("SELECT * FROM meal_schedule WHERE userId = :userId AND ativo = 1 ORDER BY horario ASC")
    fun getUserSchedules(userId: Int): Flow<List<MealScheduleEntity>>

    @Query("SELECT * FROM meal_schedule WHERE userId = :userId AND ativo = 1 ORDER BY horario ASC")
    suspend fun getUserSchedulesSync(userId: Int): List<MealScheduleEntity>

    @Query("SELECT * FROM meal_schedule WHERE userId = :userId AND tipo = :tipo LIMIT 1")
    suspend fun getScheduleByTipo(userId: Int, tipo: String): MealScheduleEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: MealScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schedules: List<MealScheduleEntity>)

    @Update
    suspend fun update(schedule: MealScheduleEntity)

    @Delete
    suspend fun delete(schedule: MealScheduleEntity)

    @Query("DELETE FROM meal_schedule WHERE userId = :userId")
    suspend fun deleteAllByUser(userId: Int)
}