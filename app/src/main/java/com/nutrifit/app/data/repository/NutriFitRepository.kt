package com.nutrifit.app.data.repository

import com.nutrifit.app.data.local.dao.*
import com.nutrifit.app.data.local.entities.*
import com.nutrifit.app.python.PythonBridge
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NutriFitRepository @Inject constructor(
    private val userDao: UserDao,
    private val mealDao: MealDao,
    private val foodDao: FoodDao,
    private val progressDao: ProgressDao,
    private val waterDao: WaterDao,
    private val achievementDao: AchievementDao,
    private val mealScheduleDao: MealScheduleDao,
    private val pythonBridge: PythonBridge
) {
    // ==================== USUÁRIO ====================
    suspend fun getCurrentUser() = userDao.getCurrentUser()
    fun getCurrentUserFlow() = userDao.getCurrentUserFlow()
    suspend fun insertUser(user: UserEntity) = userDao.insertUser(user)
    suspend fun updateUser(user: UserEntity) = userDao.updateUser(user)
    suspend fun updateWeight(userId: Int, peso: Double) = userDao.updateWeight(userId, peso)

    // ==================== REFEIÇÕES ====================
    fun getMealsByDate(userId: Int, data: String) = mealDao.getMealsByDate(userId, data)
    suspend fun getMealsByDateSync(userId: Int, data: String) = mealDao.getMealsByDateSync(userId, data)
    fun getTotalCaloriesByDate(userId: Int, data: String) = mealDao.getTotalCaloriesByDate(userId, data)
    fun getDailyTotals(userId: Int, data: String) = mealDao.getDailyTotals(userId, data)
    fun getCaloriesHistory(userId: Int, startDate: String, endDate: String) = 
        mealDao.getCaloriesHistory(userId, startDate, endDate)
    suspend fun insertMeal(meal: MealEntity) = mealDao.insertMeal(meal)
    suspend fun updateMeal(meal: MealEntity) = mealDao.updateMeal(meal)
    suspend fun deleteMeal(meal: MealEntity) = mealDao.deleteMeal(meal)

    // ==================== ALIMENTOS ====================
    fun searchFoods(query: String) = foodDao.searchFoods(query)
    fun getFoodsByCategory(category: String) = foodDao.getFoodsByCategory(category)
    suspend fun getFoodByBarcode(barcode: String) = foodDao.getFoodByBarcode(barcode)
    suspend fun insertFood(food: FoodEntity) = foodDao.insert(food)
    suspend fun insertAllFoods(foods: List<FoodEntity>) = foodDao.insertAll(foods)

    // ==================== PROGRESSO ====================
    fun getAllProgress(userId: Int) = progressDao.getAllProgress(userId)
    suspend fun getLatestProgress(userId: Int) = progressDao.getLatestProgress(userId)
    fun getProgressByPeriod(userId: Int, startDate: String, endDate: String) = 
        progressDao.getProgressByPeriod(userId, startDate, endDate)
    suspend fun insertProgress(progress: ProgressEntity) = progressDao.insertProgress(progress)

    // ==================== ÁGUA ====================
    fun getWaterByDate(userId: Int, data: String) = waterDao.getWaterByDate(userId, data)
    fun getTotalWaterByDate(userId: Int, data: String) = waterDao.getTotalWaterByDate(userId, data)
    suspend fun insertWater(water: WaterEntity) = waterDao.insertWater(water)

    // ==================== CONQUISTAS ====================
    fun getUserAchievements(userId: Int) = achievementDao.getUserAchievements(userId)
    fun getUnseenCount(userId: Int) = achievementDao.getUnseenCount(userId)
    suspend fun insertAchievement(achievement: AchievementEntity) = 
        achievementDao.insertAchievement(achievement)

    // ==================== AGENDAMENTO DE REFEIÇÕES ====================
    fun getUserSchedules(userId: Int) = mealScheduleDao.getUserSchedules(userId)
    suspend fun getUserSchedulesSync(userId: Int) = mealScheduleDao.getUserSchedulesSync(userId)
    suspend fun getScheduleByTipo(userId: Int, tipo: String) = mealScheduleDao.getScheduleByTipo(userId, tipo)
    suspend fun insertSchedule(schedule: MealScheduleEntity) = mealScheduleDao.insert(schedule)
    suspend fun insertAllSchedules(schedules: List<MealScheduleEntity>) = mealScheduleDao.insertAll(schedules)
    suspend fun updateSchedule(schedule: MealScheduleEntity) = mealScheduleDao.update(schedule)
    suspend fun deleteSchedule(schedule: MealScheduleEntity) = mealScheduleDao.delete(schedule)
    suspend fun deleteAllSchedules(userId: Int) = mealScheduleDao.deleteAllByUser(userId)

    // ==================== PYTHON ENGINE ====================
    fun calculateAll(
        peso: Double, altura: Double, idade: Int, sexo: String, 
        atividade: String, objetivo: String, pesoMeta: Double? = null
    ): JSONObject? = pythonBridge.calculateAll(peso, altura, idade, sexo, atividade, objetivo, pesoMeta)

    fun calculateProgress(
        pesoAtual: Double, pesoInicial: Double, pesoMeta: Double,
        caloriasMedia: Double, caloriasMeta: Double
    ): JSONObject? = pythonBridge.calculateProgress(
        pesoAtual, pesoInicial, pesoMeta, caloriasMedia, caloriasMeta
    )

    fun suggestMeals(caloriasMeta: Double, preferencias: List<String> = emptyList()): JSONObject? = 
        pythonBridge.suggestMeals(caloriasMeta, preferencias)

    fun getNutritionalInfo(alimentoNome: String, quantidadeG: Double = 100.0): JSONObject? =
        pythonBridge.getNutritionalInfo(alimentoNome, quantidadeG)

    fun suggestSubstitution(receita: String): JSONObject? =
        pythonBridge.suggestSubstitution(receita)

    fun generateWeeklyReport(userId: Int, startDate: String, endDate: String, dbPath: String): JSONObject? =
        pythonBridge.generateWeeklyReport(userId, startDate, endDate, dbPath)
}