package com.nutrifit.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nutrifit.app.data.repository.NutriFitRepository
import com.nutrifit.app.ui.screens.RecipeData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

// Lista de receitas padrão - fora da classe para ser acessível no estado inicial
internal val defaultRecipes = listOf(
    RecipeData(
        nome = "Frango Grelhado com Quinoa",
        descricao = "Peito de frango temperado com ervas, acompanhado de quinoa e legumes salteados",
        ingredientes = listOf(
            "200g peito de frango",
            "1/2 xícara de quinoa",
            "1 xícara de brócolis",
            "1 cenoura picada",
            "1 dente de alho",
            "Azeite, sal e ervas a gosto"
        ),
        modoPreparo = "1. Tempere o frango com sal, alho e ervas. 2. Cozinhe a quinoa conforme instruções. 3. Grelhe o frango em frigideira antiaderente. 4. Refogue os legumes no azeite. 5. Sirva tudo junto.",
        calorias = 420,
        proteinas = 38,
        carboidratos = 35,
        gorduras = 12,
        tempo = 25,
        dificuldade = "Médio",
        categoria = "almoco"
    ),
    RecipeData(
        nome = "Omelete de Claras com Aveia",
        descricao = "Omelete leve com claras, aveia e queijo minas. Perfeito para o café",
        ingredientes = listOf(
            "4 claras de ovo",
            "2 colheres de aveia",
            "50g queijo minas",
            "Orégano e sal a gosto"
        ),
        modoPreparo = "1. Bata as claras até espumar. 2. Adicione a aveia e temperos. 3. Despeje em frigideira antiaderente. 4. Quando começar a firmar, adicione o queijo. 5. Dobre ao meio e sirva.",
        calorias = 280,
        proteinas = 28,
        carboidratos = 18,
        gorduras = 10,
        tempo = 10,
        dificuldade = "Fácil",
        categoria = "cafe"
    ),
    RecipeData(
        nome = "Salmão com Legumes Assados",
        descricao = "Filé de salmão ao forno com batata doce, brócolis e cenoura",
        ingredientes = listOf(
            "150g filé de salmão",
            "1 batata doce pequena",
            "1/2 xícara de brócolis",
            "1 cenoura",
            "Azeite, limão, sal e pimenta"
        ),
        modoPreparo = "1. Corte a batata doce em cubos e cozinhe por 5 min. 2. Tempere o salmão com limão, sal e pimenta. 3. Coloque tudo em assadeira. 4. Asse a 200°C por 20-25 min. 5. Sirva com os legumes assados.",
        calorias = 480,
        proteinas = 35,
        carboidratos = 30,
        gorduras = 22,
        tempo = 35,
        dificuldade = "Médio",
        categoria = "jantar"
    ),
    RecipeData(
        nome = "Sopa Detox de Legumes",
        descricao = "Sopa nutritiva com abóbora, cenoura, batata doce e gengibre",
        ingredientes = listOf(
            "200g de abóbora",
            "1 cenoura",
            "1 batata doce pequena",
            "1 pedaço de gengibre",
            "1 dente de alho",
            "Cebola, sal e cheiro verde"
        ),
        modoPreparo = "1. Pique todos os legumes em cubos. 2. Refogue a cebola e o alho. 3. Adicione os legumes e cubra com água. 4. Cozinhe por 20 min. 5. Bata no liquidificador (opcional). 6. Tempere e sirva.",
        calorias = 280,
        proteinas = 8,
        carboidratos = 50,
        gorduras = 5,
        tempo = 30,
        dificuldade = "Fácil",
        categoria = "sopas"
    ),
    RecipeData(
        nome = "Wrap Integral de Frango",
        descricao = "Wrap recheado com frango desfiado, alface, tomate e cream cheese light",
        ingredientes = listOf(
            "1 wrap integral",
            "150g peito de frango desfiado",
            "Alface, tomate e cenoura ralada",
            "2 colheres de cream cheese light",
            "Sal e pimenta"
        ),
        modoPreparo = "1. Desfie o frango cozido e tempere. 2. Espalhe o cream cheese no wrap. 3. Coloque o frango e os vegetais. 4. Enrole firmemente. 5. Corte ao meio e sirva.",
        calorias = 350,
        proteinas = 32,
        carboidratos = 28,
        gorduras = 12,
        tempo = 15,
        dificuldade = "Fácil",
        categoria = "lanche"
    ),
    RecipeData(
        nome = "Panqueca de Banana Integral",
        descricao = "Panqueca saudável de banana com aveia e canela",
        ingredientes = listOf(
            "1 banana madura",
            "2 ovos",
            "3 colheres de aveia",
            "Canela a gosto",
            "Mel ou adoçante (opcional)"
        ),
        modoPreparo = "1. Amasse a banana com um garfo. 2. Misture os ovos, aveia e canela. 3. Despeje em frigideira antiaderente. 4. Cozinhe dos dois lados. 5. Sirva com mel ou frutas.",
        calorias = 320,
        proteinas = 18,
        carboidratos = 42,
        gorduras = 10,
        tempo = 15,
        dificuldade = "Fácil",
        categoria = "cafe"
    ),
    RecipeData(
        nome = "Strogonoff de Frango Fit",
        descricao = "Versão light do strogonoff com creme de leite light e cogumelos",
        ingredientes = listOf(
            "200g peito de frango picado",
            "1/2 xícara de cogumelos",
            "1/2 cebola picada",
            "2 colheres de creme de leite light",
            "1 colher de molho de tomate",
            "Mostarda, sal e pimenta"
        ),
        modoPreparo = "1. Refogue a cebola no azeite. 2. Adicione o frango e doure. 3. Acrescente os cogumelos. 4. Adicione o molho de tomate e mostarda. 5. Finalize com creme de leite. 6. Sirva com arroz integral.",
        calorias = 420,
        proteinas = 38,
        carboidratos = 20,
        gorduras = 18,
        tempo = 20,
        dificuldade = "Fácil",
        categoria = "almoco"
    ),
    RecipeData(
        nome = "Salada Completa com Abacate",
        descricao = "Salada nutritiva com mix de folhas, frango grelhado, abacate e castanhas",
        ingredientes = listOf(
            "Mix de folhas verdes",
            "150g frango grelhado",
            "1/2 abacate",
            "Tomate cereja",
            "Castanhas ou nozes",
            "Azeite, limão e sal"
        ),
        modoPreparo = "1. Lave e seque as folhas. 2. Corte o frango em tiras. 3. Corte o abacate e tomate. 4. Misture tudo em uma tigela. 5. Tempere com azeite, limão e sal. 6. Finalize com as castanhas.",
        calorias = 380,
        proteinas = 35,
        carboidratos = 12,
        gorduras = 22,
        tempo = 15,
        dificuldade = "Fácil",
        categoria = "jantar"
    ),
    RecipeData(
        nome = "Smoothie Verde Energético",
        descricao = "Smoothie com couve, maçã, gengibre e whey protein",
        ingredientes = listOf(
            "1 folha de couve",
            "1 maçã verde",
            "1 scoop de whey protein",
            "200ml água de coco",
            "1 pedaço de gengibre",
            "Gelo a gosto"
        ),
        modoPreparo = "1. Lave bem a couve e a maçã. 2. Corte a maçã em pedaços. 3. Coloque tudo no liquidificador. 4. Bata até ficar homogêneo. 5. Sirva com gelo.",
        calorias = 200,
        proteinas = 25,
        carboidratos = 18,
        gorduras = 3,
        tempo = 5,
        dificuldade = "Fácil",
        categoria = "lanche"
    )
)

data class RecipesUiState(
    val recipes: List<RecipeData> = defaultRecipes,
    val caloriasMeta: Double = 0.0,
    val isLoading: Boolean = false
)

@HiltViewModel
class RecipesViewModel @Inject constructor(
    private val repository: NutriFitRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecipesUiState())
    val uiState: StateFlow<RecipesUiState> = _uiState.asStateFlow()

    init {
        carregarPreferencias()
    }

    private fun carregarPreferencias() {
        viewModelScope.launch {
            val user = repository.getCurrentUser()
            if (user != null) {
                val metas = repository.calculateAll(
                    peso = user.pesoAtualKg,
                    altura = user.alturaCm,
                    idade = user.idade,
                    sexo = user.sexo,
                    atividade = user.nivelAtividade,
                    objetivo = user.objetivo,
                    pesoMeta = user.pesoMetaKg
                )

                var caloriasMeta = 0.0
                if (metas != null && metas.has("metas")) {
                    caloriasMeta = metas.getJSONObject("metas").optDouble("calorias_meta", 0.0)
                }

                _uiState.update { it.copy(caloriasMeta = caloriasMeta) }
            }
        }
    }
}