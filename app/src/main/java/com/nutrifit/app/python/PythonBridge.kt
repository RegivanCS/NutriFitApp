package com.nutrifit.app.python

import android.content.Context
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Ponte de comunicação entre Kotlin e o motor Python.
 * Usa Chaquopy para executar código Python nativamente no Android.
 */
@Singleton
class PythonBridge @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var python: Python? = null
    private var nutritionModule: com.chaquo.python.PyObject? = null
    private var mealModule: com.chaquo.python.PyObject? = null
    private var dbModule: com.chaquo.python.PyObject? = null

    init {
        initPython()
    }

    private fun initPython() {
        try {
            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(context))
            }
            python = Python.getInstance()
            nutritionModule = python?.getModule("nutrition_engine")
            mealModule = python?.getModule("meal_suggester")
            dbModule = python?.getModule("database_manager")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // ==================== CÁLCULOS NUTRICIONAIS ====================

    /**
     * Calcula todos os dados nutricionais de uma vez.
     */
    fun calculateAll(
        peso: Double,
        altura: Double,
        idade: Int,
        sexo: String,
        atividade: String,
        objetivo: String,
        pesoMeta: Double? = null
    ): JSONObject? {
        return try {
            val result = nutritionModule?.callAttr(
                "calculate_all",
                peso, altura, idade, sexo, atividade, objetivo, pesoMeta
            )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calcula IMC e retorna dados completos.
     */
    fun calculateIMC(peso: Double, altura: Double): JSONObject? {
        return try {
            val result = nutritionModule?.callAttr(
                "NutritionEngine.calcular_imc",
                peso, altura
            )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calcula metas diárias completas.
     */
    fun calculateDailyGoals(
        peso: Double,
        altura: Double,
        idade: Int,
        sexo: String,
        atividade: String,
        objetivo: String,
        pesoMeta: Double? = null
    ): JSONObject? {
        return try {
            val result = nutritionModule?.callAttr(
                "NutritionEngine.calcular_metas_diarias",
                peso, altura, idade, sexo, atividade, objetivo, pesoMeta
            )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Calcula progresso semanal.
     */
    fun calculateProgress(
        pesoAtual: Double,
        pesoInicial: Double,
        pesoMeta: Double,
        caloriasMedia: Double,
        caloriasMeta: Double
    ): JSONObject? {
        return try {
            val result = python?.getModule("nutrition_engine")
                ?.callAttr(
                    "calculate_progress",
                    pesoAtual, pesoInicial, pesoMeta, caloriasMedia, caloriasMeta
                )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Busca informações nutricionais de um alimento.
     */
    fun getNutritionalInfo(alimentoNome: String, quantidadeG: Double = 100.0): JSONObject? {
        return try {
            val result = nutritionModule?.callAttr(
                "get_nutritional_info",
                alimentoNome, quantidadeG
            )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ==================== SUGESTÕES DE REFEIÇÕES ====================

    /**
     * Sugere um dia completo de refeições.
     */
    fun suggestMeals(caloriasMeta: Double, preferencias: List<String> = emptyList()): JSONObject? {
        return try {
            val prefArray = JSONArray(preferencias)
            val result = mealModule?.callAttr(
                "suggest_meals",
                caloriasMeta, prefArray.toString()
            )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Sugere substituição saudável para um alimento.
     */
    fun suggestSubstitution(receita: String): JSONObject? {
        return try {
            val result = mealModule?.callAttr(
                "suggest_substitution",
                receita
            )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // ==================== RELATÓRIOS E ANÁLISES ====================

    /**
     * Gera relatório semanal do usuário.
     */
    fun generateWeeklyReport(
        userId: Int,
        startDate: String,
        endDate: String,
        dbPath: String
    ): JSONObject? {
        return try {
            val result = dbModule?.callAttr(
                "generate_report",
                userId, startDate, endDate, dbPath
            )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Analisa padrões alimentares.
     */
    fun analyzeEatingPatterns(userId: Int, dbPath: String): JSONObject? {
        return try {
            val result = dbModule?.callAttr(
                "analyze_patterns",
                userId, dbPath
            )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Obtém dados de progresso para gráficos.
     */
    fun getProgressData(userId: Int, dbPath: String): JSONObject? {
        return try {
            val result = dbModule?.callAttr(
                "get_progress",
                userId, dbPath
            )?.toString()
            result?.let { JSONObject(it) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
