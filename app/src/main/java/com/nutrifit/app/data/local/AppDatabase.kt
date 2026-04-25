package com.nutrifit.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.nutrifit.app.data.local.dao.*
import com.nutrifit.app.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        MealEntity::class,
        FoodEntity::class,
        ProgressEntity::class,
        WaterEntity::class,
        AchievementEntity::class,
        MealScheduleEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun mealDao(): MealDao
    abstract fun foodDao(): FoodDao
    abstract fun progressDao(): ProgressDao
    abstract fun waterDao(): WaterDao
    abstract fun achievementDao(): AchievementDao
    abstract fun mealScheduleDao(): MealScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "nutrifit_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}