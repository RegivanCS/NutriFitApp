package com.nutrifit.app.di

import android.content.Context
import com.nutrifit.app.data.local.AppDatabase
import com.nutrifit.app.data.local.dao.*
import com.nutrifit.app.data.repository.NutriFitRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return AppDatabase.getDatabase(context)
    }

    @Provides
    fun provideUserDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    fun provideMealDao(db: AppDatabase): MealDao = db.mealDao()

    @Provides
    fun provideFoodDao(db: AppDatabase): FoodDao = db.foodDao()

    @Provides
    fun provideProgressDao(db: AppDatabase): ProgressDao = db.progressDao()

    @Provides
    fun provideWaterDao(db: AppDatabase): WaterDao = db.waterDao()

    @Provides
    fun provideAchievementDao(db: AppDatabase): AchievementDao = db.achievementDao()

    @Provides
    fun provideMealScheduleDao(db: AppDatabase): MealScheduleDao = db.mealScheduleDao()
}