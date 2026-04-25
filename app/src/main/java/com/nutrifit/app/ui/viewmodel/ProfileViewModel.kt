package com.nutrifit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifit.app.data.local.entities.UserEntity
import com.nutrifit.app.data.repository.NutriFitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

data class ProfileUiState(
    val user: UserEntity? = null,
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val editNome: String = "",
    val editIdade: String = "",
    val editPesoAtual: String = "",
    val editPesoMeta: String = "",
    val editAltura: String = "",
    val metasCalculadas: JSONObject? = null,
    val mensagemSucesso: String? = null,
    val mensagemErro: String? = null
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: NutriFitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        carregarUsuario()
    }

    private fun carregarUsuario() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                val metas = calcularMetas(user)
                _uiState.update {
                    it.copy(
                        user = user,
                        isLoading = false,
                        editNome = user.nome,
                        editIdade = user.idade.toString(),
                        editPesoAtual = user.pesoAtualKg.toString(),
                        editPesoMeta = user.pesoMetaKg.toString(),
                        editAltura = user.alturaCm.toString(),
                        metasCalculadas = metas
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun calcularMetas(user: UserEntity): JSONObject? {
        return repository.calculateAll(
            peso = user.pesoAtualKg,
            altura = user.alturaCm,
            idade = user.idade,
            sexo = user.sexo,
            atividade = user.nivelAtividade,
            objetivo = user.objetivo,
            pesoMeta = user.pesoMetaKg
        )
    }

    fun toggleEditing() {
        _uiState.update { it.copy(isEditing = !it.isEditing) }
    }

    fun updateNome(valor: String) { _uiState.update { it.copy(editNome = valor) } }
    fun updateIdade(valor: String) { _uiState.update { it.copy(editIdade = valor) } }
    fun updatePesoAtual(valor: String) { _uiState.update { it.copy(editPesoAtual = valor) } }
    fun updatePesoMeta(valor: String) { _uiState.update { it.copy(editPesoMeta = valor) } }
    fun updateAltura(valor: String) { _uiState.update { it.copy(editAltura = valor) } }

    fun salvarAlteracoes() {
        val state = _uiState.value
        val user = state.user ?: return

        try {
            val idade = state.editIdade.toInt()
            val pesoAtual = state.editPesoAtual.toDouble()
            val pesoMeta = state.editPesoMeta.toDouble()
            val altura = state.editAltura.toDouble()

            viewModelScope.launch {
                val updatedUser = user.copy(
                    nome = state.editNome,
                    idade = idade,
                    pesoAtualKg = pesoAtual,
                    pesoMetaKg = pesoMeta,
                    alturaCm = altura
                )
                repository.updateUser(updatedUser)
                carregarUsuario()
                _uiState.update { it.copy(mensagemSucesso = "Perfil atualizado com sucesso!") }
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(mensagemErro = "Verifique os dados informados") }
        }
    }

    fun limparMensagens() {
        _uiState.update { it.copy(mensagemSucesso = null, mensagemErro = null) }
    }

    fun resetarDados() {
        viewModelScope.launch {
            val user = _uiState.value.user ?: return@launch
            repository.updateWeight(user.id, user.pesoInicialKg)
            carregarUsuario()
            _uiState.update { it.copy(mensagemSucesso = "Dados resetados com sucesso!") }
        }
    }
}