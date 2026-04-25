package com.nutrifit.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    onBack: () -> Unit
) {
    var selectedCategory by remember { mutableStateOf("todas") }

    val categories = listOf(
        "todas" to "Todas",
        "cafe" to "Café",
        "almoco" to "Almoço",
        "jantar" to "Jantar",
        "lanche" to "Lanches",
        "sopas" to "Sopas"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receitas Saudáveis") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Categorias
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.take(4).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedCategory == key,
                        onClick = { selectedCategory = key },
                        label = { Text(label) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Receitas mockadas
                val recipes = listOf(
                    RecipeData(
                        nome = "Frango Grelhado com Quinoa",
                        descricao = "Peito de frango temperado com ervas, acompanhado de quinoa e legumes salteados",
                        calorias = 420,
                        proteinas = 38,
                        tempo = 25,
                        dificuldade = "Médio",
                        categoria = "almoco"
                    ),
                    RecipeData(
                        nome = "Omelete de Claras com Aveia",
                        descricao = "Omelete leve com claras, aveia e queijo minas. Perfeito para o café",
                        calorias = 280,
                        proteinas = 28,
                        tempo = 10,
                        dificuldade = "Fácil",
                        categoria = "cafe"
                    ),
                    RecipeData(
                        nome = "Salmão com Legumes Assados",
                        descricao = "Filé de salmão ao forno com batata doce, brócolis e cenoura",
                        calorias = 480,
                        proteinas = 35,
                        tempo = 35,
                        dificuldade = "Médio",
                        categoria = "jantar"
                    ),
                    RecipeData(
                        nome = "Sopa Detox de Legumes",
                        descricao = "Sopa nutritiva com abóbora, cenoura, batata doce e gengibre",
                        calorias = 280,
                        proteinas = 8,
                        tempo = 30,
                        dificuldade = "Fácil",
                        categoria = "sopas"
                    ),
                    RecipeData(
                        nome = "Wrap Integral de Frango",
                        descricao = "Wrap recheado com frango desfiado, alface, tomate e cream cheese light",
                        calorias = 350,
                        proteinas = 32,
                        tempo = 15,
                        dificuldade = "Fácil",
                        categoria = "lanche"
                    ),
                    RecipeData(
                        nome = "Panqueca de Banana Integral",
                        descricao = "Panqueca saudável de banana com aveia e canela",
                        calorias = 320,
                        proteinas = 18,
                        tempo = 15,
                        dificuldade = "Fácil",
                        categoria = "cafe"
                    ),
                    RecipeData(
                        nome = "Strogonoff de Frango Fit",
                        descricao = "Versão light do strogonoff com creme de leite light e cogumelos",
                        calorias = 420,
                        proteinas = 38,
                        tempo = 20,
                        dificuldade = "Fácil",
                        categoria = "almoco"
                    ),
                    RecipeData(
                        nome = "Salada Completa com Abacate",
                        descricao = "Salada nutritiva com mix de folhas, frango grelhado, abacate e castanhas",
                        calorias = 380,
                        proteinas = 35,
                        tempo = 15,
                        dificuldade = "Fácil",
                        categoria = "jantar"
                    ),
                    RecipeData(
                        nome = "Smoothie Verde Energético",
                        descricao = "Smoothie com couve, maçã, gengibre e whey protein",
                        calorias = 200,
                        proteinas = 25,
                        tempo = 5,
                        dificuldade = "Fácil",
                        categoria = "lanche"
                    )
                )

                val filteredRecipes = if (selectedCategory == "todas") recipes
                else recipes.filter { it.categoria == selectedCategory }

                items(filteredRecipes) { recipe ->
                    RecipeCard(recipe = recipe)
                }

                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

data class RecipeData(
    val nome: String,
    val descricao: String,
    val calorias: Int,
    val proteinas: Int,
    val tempo: Int,
    val dificuldade: String,
    val categoria: String
)

@Composable
fun RecipeCard(recipe: RecipeData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = recipe.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${recipe.calorias} kcal",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recipe.descricao,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.tempo} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${recipe.proteinas}g proteinas",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (recipe.dificuldade == "Fácil") Icons.Default.SentimentSatisfied 
                            else Icons.Default.AutoFixHigh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = recipe.dificuldade,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                FilledTonalButton(
                    onClick = { /* Ver receita completa */ },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ver Receita", style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}
