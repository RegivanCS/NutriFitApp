package com.nutrifit.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutrifit.app.ui.viewmodel.RecipesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    onBack: () -> Unit,
    viewModel: RecipesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    var selectedCategory by remember { mutableStateOf("todas") }
    var showRecipeDetail by remember { mutableStateOf<RecipeData?>(null) }

    val categories = listOf(
        "todas" to "Todas",
        "cafe" to "Café",
        "almoco" to "Almoço",
        "jantar" to "Jantar",
        "lanche" to "Lanches",
        "sopas" to "Sopas"
    )

    // Dialog de detalhes da receita
    showRecipeDetail?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            onDismiss = { showRecipeDetail = null }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receitas Saudáveis") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Favoritos */ }) {
                        Icon(Icons.Default.Favorite, contentDescription = "Favoritos")
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
            // Saudação e meta
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        text = "🎯 Sugestões para você",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (uiState.caloriasMeta > 0) {
                        Text(
                            text = "Baseado na sua meta de ${uiState.caloriasMeta.toInt()} kcal/dia",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Categorias
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.take(4).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedCategory == key,
                        onClick = { selectedCategory = key },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                categories.drop(4).forEach { (key, label) ->
                    FilterChip(
                        selected = selectedCategory == key,
                        onClick = { selectedCategory = key },
                        label = { Text(label, style = MaterialTheme.typography.labelSmall) }
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
                val recipes = uiState.recipes

                val filteredRecipes = if (selectedCategory == "todas") recipes
                else recipes.filter { it.categoria == selectedCategory }

                items(filteredRecipes) { recipe ->
                    RecipeCard(
                        recipe = recipe,
                        onClick = { showRecipeDetail = recipe }
                    )
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
    val ingredientes: List<String> = emptyList(),
    val modoPreparo: String = "",
    val calorias: Int,
    val proteinas: Int,
    val carboidratos: Int = 0,
    val gorduras: Int = 0,
    val tempo: Int,
    val dificuldade: String,
    val categoria: String
)

@Composable
fun RecipeCard(
    recipe: RecipeData,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        onClick = onClick
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
                        text = "${recipe.proteinas}g prot",
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
                    onClick = onClick,
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

@Composable
private fun RecipeDetailDialog(
    recipe: RecipeData,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(recipe.nome, fontWeight = FontWeight.Bold)
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Informações rápidas
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    DetailChip("${recipe.calorias} kcal", Icons.Default.LocalFireDepartment)
                    DetailChip("${recipe.proteinas}g prot", Icons.Default.FitnessCenter)
                    if (recipe.carboidratos > 0) {
                        DetailChip("${recipe.carboidratos}g carb", Icons.Default.BakeryDining)
                    }
                    if (recipe.gorduras > 0) {
                        DetailChip("${recipe.gorduras}g gord", Icons.Default.OilBarrel)
                    }
                }

                HorizontalDivider()

                // Descrição
                Text(
                    text = recipe.descricao,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Ingredientes
                if (recipe.ingredientes.isNotEmpty()) {
                    Text(
                        text = "Ingredientes:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    recipe.ingredientes.forEach { ing ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("• ", style = MaterialTheme.typography.bodySmall)
                            Text(ing, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Modo de preparo
                if (recipe.modoPreparo.isNotBlank()) {
                    Text(
                        text = "Modo de Preparo:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = recipe.modoPreparo,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Tempo e dificuldade
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    AssistChip(
                        onClick = {},
                        label = { Text("⏱ ${recipe.tempo} min") }
                    )
                    AssistChip(
                        onClick = {},
                        label = { Text("📊 ${recipe.dificuldade}") }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Fechar")
            }
        }
    )
}

@Composable
private fun DetailChip(valor: String, icone: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icone,
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = valor,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}