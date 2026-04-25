package com.nutrifit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifit.app.data.local.entities.ProgressEntity
import com.nutrifit.app.data.repository.NutriFitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

data class ProgressUiState(
    val pesoInicial: Double = 0.0,
    val pesoAtual: Double = 0.0,
    val pesoMeta: Double = 0.0,
    val kgPerdidos: Double = 0.0,
    val kgRestantes: Double = 0.0,
    val progressoPercentual: Double = 0.0,
    val diasRegistrando: Int = 0,
    val semanaCalorias: List<Pair<String, Double>> = emptyList(),
    val mensagemMotivacional: String = "",
    val showWeightDialog: Boolean = false,
    val novoPeso: String = "",
    val isLoading: Boolean = true,
    val mensagemSucesso: String? = null
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    private val repository: NutriFitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        carregarDados()
    }

    private fun carregarDados() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                val kgPerdidos = user.pesoInicialKg - user.pesoAtualKg
                val kgRestantes = maxOf(0.0, user.pesoAtualKg - user.pesoMetaKg)
                val progresso = if (user.pesoInicialKg > user.pesoMetaKg) {
                    ((user.pesoInicialKg - user.pesoAtualKg) / (user.pesoInicialKg - user.pesoMetaKg) * 100)
                        .coerceIn(0.0, 100.0)
                } else 0.0

                // Dados da semana (últimos 7 dias)
                val hoje = LocalDate.now()
                val semanaCalorias = mutableListOf<Pair<String, Double>>()
                for (i in 6 downTo 0) {
                    val dia = hoje.minusDays(i.toLong())
                    val data = dia.format(dateFormatter)
                    val meals = repository.getMealsByDateSync(user.id, data)
                    val totalCal = meals.sumOf { it.calorias }
                    semanaCalorias.add(
                        dia.format(DateTimeFormatter.ofPattern("dd/MM")) to totalCal
                    )
                }

                _uiState.update {
                    it.copy(
                        pesoInicial = user.pesoInicialKg,
                        pesoAtual = user.pesoAtualKg,
                        pesoMeta = user.pesoMetaKg,
                        kgPerdidos = kgPerdidos,
                        kgRestantes = kgRestantes,
                        progressoPercentual = progresso,
                        diasRegistrando = 1,
                        semanaCalorias = semanaCalorias,
                        mensagemMotivacional = when {
                            progresso >= 75 -> "🌟 Incrível! Você está quase lá!"
                            progresso >= 50 -> "💪 Ótimo progresso! Continue assim!"
                            progresso >= 25 -> "🔥 Mandou bem! Continue firme!"
                            progresso > 0 -> "🌱 Todo começo é difícil, mas você já está no caminho!"
                            else -> "🎯 Vamos começar? Registre seu primeiro progresso!"
                        },
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun showWeightInput() {
        _uiState.update { it.copy(showWeightDialog = true, novoPeso = "") }
    }

    fun hideWeightInput() {
        _uiState.update { it.copy(showWeightDialog = false, novoPeso = "") }
    }

    fun updateNovoPeso(valor: String) {
        _uiState.update { it.copy(novoPeso = valor) }
    }

    fun salvarNovoPeso() {
        val peso = _uiState.value.novoPeso.toDoubleOrNull() ?: return
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                repository.updateWeight(user.id, peso)

                val progress = ProgressEntity(
                    userId = user.id,
                    data = LocalDate.now().format(dateFormatter),
                    pesoKg = peso
                )
                repository.insertProgress(progress)

                _uiState.update {
                    it.copy(
                        showWeightDialog = false,
                        mensagemSucesso = "Peso atualizado para ${peso} kg!"
                    )
                }
                carregarDados()
            }
        }
    }

    fun limparMensagem() {
        _uiState.update { it.copy(mensagemSucesso = null) }
    }
}