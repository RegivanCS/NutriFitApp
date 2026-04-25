package com.nutrifit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifit.app.data.repository.NutriFitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import javax.inject.Inject

data class HomeUiState(
    val caloriasConsumidas: Double = 0.0,
    val caloriasMeta: Double = 1850.0,
    val proteinas: Double = 0.0,
    val carboidratos: Double = 0.0,
    val gorduras: Double = 0.0,
    val nomeUsuario: String = "Usuário",
    val proximaRefeicao: Triple<String, String, String>? = null,
    val isLoading: Boolean = true
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NutriFitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    init {
        carregarDados()
    }

    private fun carregarDados() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                val hoje = LocalDate.now().format(dateFormatter)
                val nomeUsuario = user.nome.split(" ").first()

                // Totais do dia
                val totais = repository.getDailyTotals(user.id, hoje).first()
                val caloriasConsumidas = totais?.total_calorias ?: 0.0
                val proteinas = totais?.total_proteinas ?: 0.0
                val carboidratos = totais?.total_carboidratos ?: 0.0
                val gorduras = totais?.total_gorduras ?: 0.0

                // Metas do Python
                val metas = repository.calculateAll(
                    peso = user.pesoAtualKg,
                    altura = user.alturaCm,
                    idade = user.idade,
                    sexo = user.sexo,
                    atividade = user.nivelAtividade,
                    objetivo = user.objetivo,
                    pesoMeta = user.pesoMetaKg
                )

                var caloriasMeta = 1850.0
                if (metas != null && metas.has("metas")) {
                    val metasObj = metas.getJSONObject("metas")
                    if (metasObj.has("calorias_meta")) {
                        caloriasMeta = metasObj.getDouble("calorias_meta")
                    }
                }

                // Calcular próxima refeição
                val proximaRefeicao = calcularProximaRefeicao(user.id)

                _uiState.update {
                    it.copy(
                        nomeUsuario = nomeUsuario,
                        caloriasConsumidas = caloriasConsumidas,
                        caloriasMeta = caloriasMeta,
                        proteinas = proteinas,
                        carboidratos = carboidratos,
                        gorduras = gorduras,
                        proximaRefeicao = proximaRefeicao,
                        isLoading = false
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun calcularProximaRefeicao(userId: Int): Triple<String, String, String>? {
        val schedules = repository.getUserSchedulesSync(userId)
        val agora = LocalTime.now()

        val nomesTipo = mapOf(
            "cafe_da_manha" to "Café da Manhã",
            "lanche_manha" to "Lanche da Manhã",
            "almoco" to "Almoço",
            "lanche_tarde" to "Lanche da Tarde",
            "jantar" to "Jantar",
            "ceia" to "Ceia"
        )

        val emojis = mapOf(
            "cafe_da_manha" to "☕",
            "lanche_manha" to "🍎",
            "almoco" to "🍗",
            "lanche_tarde" to "🥜",
            "jantar" to "🥗",
            "ceia" to "🌙"
        )

        // Encontrar a próxima refeição que ainda não passou
        for (schedule in schedules.filter { it.ativo }.sortedBy { it.horario }) {
            val partes = schedule.horario.split(":")
            val hora = partes[0].toIntOrNull() ?: continue
            val minuto = partes[1].toIntOrNull() ?: continue
            val horaAgendada = LocalTime.of(hora, minuto)

            if (horaAgendada.isAfter(agora)) {
                val minutosRestantes = ChronoUnit.MINUTES.between(agora, horaAgendada)
                val horas = minutosRestantes / 60
                val mins = minutosRestantes % 60

                val tempoRestante = when {
                    horas > 0 -> "${horas}h ${mins}min"
                    mins > 0 -> "${mins}min"
                    else -> "Agora!"
                }

                val emoji = emojis[schedule.tipo] ?: "🍽️"
                val nomeCompleto = "${emoji} ${nomesTipo[schedule.tipo] ?: schedule.tipo}"
                return Triple(nomeCompleto, schedule.horario, tempoRestante)
            }
        }

        // Se passou de todas, a primeira de amanhã
        schedules.filter { it.ativo }.minByOrNull { it.horario }?.let { primeira ->
            val emoji = emojis[primeira.tipo] ?: "🍽️"
            val nomeCompleto = "${emoji} ${nomesTipo[primeira.tipo] ?: primeira.tipo} (amanhã)"
            return Triple(nomeCompleto, primeira.horario, "---")
        }

        return null
    }
}