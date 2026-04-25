package com.nutrifit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifit.app.data.local.entities.UserEntity
import com.nutrifit.app.data.repository.NutriFitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingUiState(
    val isLoading: Boolean = false,
    val isComplete: Boolean = false,
    val mensagemErro: String? = null
)

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val repository: NutriFitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun salvarUsuario(user: UserEntity) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                repository.insertUser(user)
                _uiState.update { it.copy(isLoading = false, isComplete = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, mensagemErro = "Erro ao salvar: ${e.message}") }
            }
        }
    }
}