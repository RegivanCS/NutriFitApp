package com.nutrifit.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifit.app.data.local.entities.MealScheduleEntity
import com.nutrifit.app.data.repository.NutriFitRepository
import com.nutrifit.app.notification.AlarmScheduler
import com.nutrifit.app.nutrition.MealRecommendationEngine
import com.nutrifit.app.nutrition.RefeicaoRecomendada
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ScheduleUiState(
    val schedules: List<MealScheduleEntity> = emptyList(),
    val recomendacoes: Map<String, RefeicaoRecomendada> = emptyMap(),
    val userId: Int = 0,
    val isLoading: Boolean = true,
    val mensagemSucesso: String? = null,
    val objetivo: String = "emagrecer",
    val caloriasMeta: Double = 1850.0,
    val peso: Double = 70.0
)

/**
 * ViewModel para gerenciar o agendamento de refeições, alarmes e recomendações inteligentes.
 */
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val application: Application,
    private val repository: NutriFitRepository
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(ScheduleUiState())
    val uiState: StateFlow<ScheduleUiState> = _uiState.asStateFlow()

    private val horariosPadrao = listOf(
        MealScheduleEntity(userId = 0, tipo = "cafe_da_manha", horario = "07:00", nome = "Café da Manhã"),
        MealScheduleEntity(userId = 0, tipo = "lanche_manha", horario = "09:30", nome = "Lanche da Manhã"),
        MealScheduleEntity(userId = 0, tipo = "almoco", horario = "12:00", nome = "Almoço"),
        MealScheduleEntity(userId = 0, tipo = "lanche_tarde", horario = "15:30", nome = "Lanche da Tarde"),
        MealScheduleEntity(userId = 0, tipo = "jantar", horario = "19:00", nome = "Jantar"),
        MealScheduleEntity(userId = 0, tipo = "ceia", horario = "21:30", nome = "Ceia")
    )

    init {
        carregarAgenda()
    }

    fun carregarAgenda() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                _uiState.update {
                    it.copy(
                        userId = user.id,
                        objetivo = user.objetivo,
                        peso = user.pesoAtualKg
                    )
                }

                // Pegar metas do Python
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
                    caloriasMeta = metas.getJSONObject("metas").optDouble("calorias_meta", 1850.0)
                }
                _uiState.update { it.copy(caloriasMeta = caloriasMeta) }

                // Verificar se já tem agenda, se não, criar padrão
                val existing = repository.getUserSchedulesSync(user.id)
                if (existing.isEmpty()) {
                    criarAgendaPadrao(user.id, user.objetivo, caloriasMeta, user.pesoAtualKg)
                } else {
                    val recomendacoes = gerarRecomendacoes(existing, user.objetivo, caloriasMeta, user.pesoAtualKg)
                    _uiState.update {
                        it.copy(
                            schedules = existing,
                            recomendacoes = recomendacoes,
                            isLoading = false
                        )
                    }
                    agendarAlarmes(existing, user.objetivo)
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun criarAgendaPadrao(userId: Int, objetivo: String, caloriasMeta: Double, peso: Double) {
        val schedules = horariosPadrao.map { it.copy(userId = userId) }
        repository.insertAllSchedules(schedules)
        val recomendacoes = gerarRecomendacoes(schedules, objetivo, caloriasMeta, peso)
        _uiState.update {
            it.copy(
                schedules = schedules,
                recomendacoes = recomendacoes,
                isLoading = false,
                mensagemSucesso = "✅ Agenda criada! Toque em cada refeição para ver o que comer! 📖"
            )
        }
        agendarAlarmes(schedules, objetivo)
    }

    private fun gerarRecomendacoes(
        schedules: List<MealScheduleEntity>,
        objetivo: String,
        caloriasMeta: Double,
        peso: Double
    ): Map<String, RefeicaoRecomendada> {
        return schedules.associate { schedule ->
            val recomendacao = MealRecommendationEngine.getRecommendation(schedule, objetivo, caloriasMeta, peso)
            schedule.tipo to recomendacao
        }
    }

    fun atualizarHorario(schedule: MealScheduleEntity, novoHorario: String) {
        viewModelScope.launch {
            val updated = schedule.copy(horario = novoHorario)
            repository.updateSchedule(updated)

            val user = repository.getCurrentUser()
            if (user != null) {
                val schedules = repository.getUserSchedulesSync(user.id)
                val recomendacoes = gerarRecomendacoes(schedules, user.objetivo, _uiState.value.caloriasMeta, user.pesoAtualKg)
                _uiState.update {
                    it.copy(
                        schedules = schedules,
                        recomendacoes = recomendacoes,
                        mensagemSucesso = "✅ Horário atualizado! Agora às $novoHorario!"
                    )
                }
                agendarAlarmes(schedules, user.objetivo)
            }
        }
    }

    fun alternarAtivo(schedule: MealScheduleEntity) {
        viewModelScope.launch {
            val updated = schedule.copy(ativo = !schedule.ativo)
            repository.updateSchedule(updated)

            val user = repository.getCurrentUser()
            if (user != null) {
                val schedules = repository.getUserSchedulesSync(user.id)
                val recomendacoes = gerarRecomendacoes(schedules, user.objetivo, _uiState.value.caloriasMeta, user.pesoAtualKg)
                _uiState.update {
                    it.copy(schedules = schedules, recomendacoes = recomendacoes)
                }
                agendarAlarmes(schedules, user.objetivo)
            }
        }
    }

    private fun agendarAlarmes(schedules: List<MealScheduleEntity>, objetivo: String) {
        AlarmScheduler.cancelAllAlarms(application)
        var requestCode = 1000
        schedules.filter { it.ativo }.forEach { schedule ->
            val partes = schedule.horario.split(":")
            val hora = partes[0].toIntOrNull() ?: return@forEach
            val minuto = partes[1].toIntOrNull() ?: return@forEach

            // Pegar a recomendação para passar na notificação
            val recomendacao = _uiState.value.recomendacoes[schedule.tipo]
            val nomeRefeicao = recomendacao?.nome ?: schedule.nome

            AlarmScheduler.scheduleMealReminder(
                application, hora, minuto,
                schedule.tipo, nomeRefeicao,
                objetivo = objetivo,
                requestCode = requestCode++
            )
        }

        // Lembrete de exercício às 18:00
        AlarmScheduler.scheduleExerciseReminder(application, 18, 0, 2000)

        _uiState.update { it.copy(mensagemSucesso = "🔔 Alarmes programados! Seu celular vai tocar na hora certa!") }
    }

    fun limparMensagem() {
        _uiState.update { it.copy(mensagemSucesso = null) }
    }
}