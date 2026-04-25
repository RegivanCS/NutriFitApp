package com.nutrifit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifit.app.data.local.entities.MealEntity
import com.nutrifit.app.data.repository.NutriFitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class DiaryUiState(
    val userId: Int = 0,
    val selectedDate: LocalDate = LocalDate.now(),
    val meals: List<MealEntity> = emptyList(),
    val totalCalorias: Double = 0.0,
    val totalProteinas: Double = 0.0,
    val totalCarboidratos: Double = 0.0,
    val totalGorduras: Double = 0.0,
    val isLoading: Boolean = false,
    val showAddDialog: Boolean = false,
    val showFoodSearch: Boolean = false,
    val searchQuery: String = "",
    val searchResults: List<com.nutrifit.app.data.local.entities.FoodEntity> = emptyList(),
    val editMeal: MealEntity? = null,
    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null
)

@HiltViewModel
class DiaryViewModel @Inject constructor(
    private val repository: NutriFitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DiaryUiState())
    val uiState: StateFlow<DiaryUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        carregarUsuario()
    }

    private fun carregarUsuario() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                _uiState.update { it.copy(userId = user.id) }
                carregarRefeicoes()
            }
        }
    }

    private fun carregarRefeicoes() {
        val state = _uiState.value
        if (state.userId == 0) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val data = state.selectedDate.format(dateFormatter)
            val meals = repository.getMealsByDateSync(state.userId, data)

            val totalCal = meals.sumOf { it.calorias }
            val totalProt = meals.sumOf { it.proteinas }
            val totalCarb = meals.sumOf { it.carboidratos }
            val totalGord = meals.sumOf { it.gorduras }

            _uiState.update {
                it.copy(
                    meals = meals,
                    totalCalorias = totalCal,
                    totalProteinas = totalProt,
                    totalCarboidratos = totalCarb,
                    totalGorduras = totalGord,
                    isLoading = false
                )
            }
        }
    }

    fun changeDate(days: Long) {
        _uiState.update {
            it.copy(selectedDate = it.selectedDate.plusDays(days))
        }
        carregarRefeicoes()
    }

    fun showAddMealDialog() {
        _uiState.update { it.copy(showAddDialog = true, editMeal = null) }
    }

    fun showEditMealDialog(meal: MealEntity) {
        _uiState.update { it.copy(showAddDialog = true, editMeal = meal) }
    }

    fun hideAddMealDialog() {
        _uiState.update { it.copy(showAddDialog = false, editMeal = null) }
    }

    fun showFoodSearch() {
        _uiState.update { it.copy(showFoodSearch = true) }
    }

    fun hideFoodSearch() {
        _uiState.update { it.copy(showFoodSearch = false, searchQuery = "", searchResults = emptyList()) }
    }

    fun searchFood(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        if (query.length >= 2) {
            viewModelScope.launch {
                val results = repository.searchFoods(query).first()
                _uiState.update { it.copy(searchResults = results) }
            }
        }
    }

    fun saveMeal(
        tipo: String,
        nome: String,
        calorias: Double,
        proteinas: Double = 0.0,
        carboidratos: Double = 0.0,
        gorduras: Double = 0.0,
        fibras: Double = 0.0
    ) {
        val state = _uiState.value
        viewModelScope.launch {
            try {
                val data = state.selectedDate.format(dateFormatter)
                val horario = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

                if (state.editMeal != null) {
                    val updated = state.editMeal.copy(
                        tipo = tipo,
                        nome = nome,
                        calorias = calorias,
                        proteinas = proteinas,
                        carboidratos = carboidratos,
                        gorduras = gorduras,
                        fibras = fibras
                    )
                    repository.updateMeal(updated)
                    _uiState.update { it.copy(mensagemSucesso = "Refeição atualizada!") }
                } else {
                    val meal = MealEntity(
                        userId = state.userId,
                        data = data,
                        tipo = tipo,
                        horario = horario,
                        nome = nome,
                        calorias = calorias,
                        proteinas = proteinas,
                        carboidratos = carboidratos,
                        gorduras = gorduras,
                        fibras = fibras
                    )
                    repository.insertMeal(meal)
                    _uiState.update { it.copy(mensagemSucesso = "Refeição registrada!") }
                }

                hideAddMealDialog()
                carregarRefeicoes()
            } catch (e: Exception) {
                _uiState.update { it.copy(mensagemErro = "Erro ao salvar refeição") }
            }
        }
    }

    fun deleteMeal(meal: MealEntity) {
        viewModelScope.launch {
            repository.deleteMeal(meal)
            carregarRefeicoes()
            _uiState.update { it.copy(mensagemSucesso = "Refeição removida!") }
        }
    }

    fun limparMensagens() {
        _uiState.update { it.copy(mensagemSucesso = null, mensagemErro = null) }
    }
}