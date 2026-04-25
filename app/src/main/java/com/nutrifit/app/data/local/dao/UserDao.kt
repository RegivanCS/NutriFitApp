package com.nutrifit.app.data.local.dao

import androidx.room.*
import com.nutrifit.app.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE id = :id")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    suspend fun getCurrentUser(): UserEntity?

    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    fun getCurrentUserFlow(): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("UPDATE users SET pesoAtualKg = :peso WHERE id = :userId")
    suspend fun updateWeight(userId: Int, peso: Double)

    @Query("DELETE FROM users WHERE id = :id")
    suspend fun deleteUser(id: Int)
}
