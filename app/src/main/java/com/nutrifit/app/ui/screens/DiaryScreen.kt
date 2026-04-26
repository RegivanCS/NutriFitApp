package com.nutrifit.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutrifit.app.data.local.entities.MealEntity
import com.nutrifit.app.ui.viewmodel.DiaryViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    onBack: () -> Unit,
    viewModel: DiaryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // Dialog para adicionar/editar refeição
    if (uiState.showAddDialog) {
        AddMealDialog(
            editMeal = uiState.editMeal,
            onDismiss = { viewModel.hideAddMealDialog() },
            onSave = { tipo, nome, cal, prot, carb, gord, fibras ->
                viewModel.saveMeal(tipo, nome, cal, prot, carb, gord, fibras)
            },
            onSearchFood = { viewModel.showFoodSearch() }
        )
    }

    // Dialog de busca de alimentos
    if (uiState.showFoodSearch) {
        FoodSearchDialog(
            query = uiState.searchQuery,
            results = uiState.searchResults,
            onQueryChange = { viewModel.searchFood(it) },
            onSelectFood = { _ ->
                viewModel.hideFoodSearch()
                // Preencher dados do alimento no dialog
            },
            onDismiss = { viewModel.hideFoodSearch() }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diário Alimentar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.showAddMealDialog() }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar")
                    }
                }
            )
        }
    ) { padding ->
        // Mensagens de feedback
        LaunchedEffect(uiState.mensagemSucesso) {
            if (uiState.mensagemSucesso != null) {
                kotlinx.coroutines.delay(3000)
                viewModel.limparMensagens()
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Mensagens
            uiState.mensagemSucesso?.let {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(it, modifier = Modifier.padding(8.dp), style = MaterialTheme.typography.bodySmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Seletor de data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.changeDate(-1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Dia anterior")
                }
                Text(
                    text = uiState.selectedDate.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { viewModel.changeDate(1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Próximo dia")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Resumo do dia (dados reais)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${uiState.totalCalorias.toInt()}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Calorias", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${uiState.totalProteinas.toInt()}g",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Proteínas", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "${uiState.totalCarboidratos.toInt()}g",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text("Carboidratos", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista de refeições do dia
            Text(
                text = "Refeições do dia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.meals.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Nenhuma refeição registrada hoje",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Toque no + para adicionar",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.meals, key = { it.id }) { meal ->
                        MealCard(
                            meal = meal,
                            onClick = { viewModel.showEditMealDialog(meal) },
                            onDelete = { viewModel.deleteMeal(meal) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MealCard(
    meal: MealEntity,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    val tipoIcon = when (meal.tipo) {
        "cafe_da_manha" -> Icons.Default.WbSunny
        "lanche_manha" -> Icons.Default.Fastfood
        "almoco" -> Icons.Default.Restaurant
        "lanche_tarde" -> Icons.Default.Fastfood
        "jantar" -> Icons.Default.NightsStay
        else -> Icons.Default.Restaurant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = tipoIcon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.nome,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${meal.horario} - ${meal.proteinas.toInt()}g prot | ${meal.carboidratos.toInt()}g carb",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${meal.calorias.toInt()} kcal",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Excluir",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddMealDialog(
    editMeal: MealEntity?,
    onDismiss: () -> Unit,
    onSave: (String, String, Double, Double, Double, Double, Double) -> Unit,
    onSearchFood: () -> Unit
) {
    var tipo by remember { mutableStateOf(editMeal?.tipo ?: "almoco") }
    var nome by remember { mutableStateOf(editMeal?.nome ?: "") }
    var calorias by remember { mutableStateOf(editMeal?.calorias?.toString() ?: "") }
    var proteinas by remember { mutableStateOf(editMeal?.proteinas?.toString() ?: "") }
    var carboidratos by remember { mutableStateOf(editMeal?.carboidratos?.toString() ?: "") }
    var gorduras by remember { mutableStateOf(editMeal?.gorduras?.toString() ?: "") }

    val tipos = listOf(
        "cafe_da_manha" to "Café da Manhã",
        "lanche_manha" to "Lanche Manhã",
        "almoco" to "Almoço",
        "lanche_tarde" to "Lanche Tarde",
        "jantar" to "Jantar"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (editMeal != null) "Editar Refeição" else "Nova Refeição") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Tipo de refeição
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tipos.forEach { (key, label) ->
                        FilterChip(
                            selected = tipo == key,
                            onClick = { tipo = key },
                            label = { Text(label.substringBefore(" "), style = MaterialTheme.typography.labelSmall) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                OutlinedTextField(
                    value = nome,
                    onValueChange = { nome = it },
                    label = { Text("Nome da refeição") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    trailingIcon = {
                        IconButton(onClick = onSearchFood) {
                            Icon(Icons.Default.Search, "Buscar alimento")
                        }
                    }
                )

                OutlinedTextField(
                    value = calorias,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) calorias = it },
                    label = { Text("Calorias (kcal)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = proteinas,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) proteinas = it },
                        label = { Text("Proteínas (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = carboidratos,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) carboidratos = it },
                        label = { Text("Carboidratos (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = gorduras,
                        onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) gorduras = it },
                        label = { Text("Gorduras (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val cal = calorias.toDoubleOrNull() ?: 0.0
                    val prot = proteinas.toDoubleOrNull() ?: 0.0
                    val carb = carboidratos.toDoubleOrNull() ?: 0.0
                    val gord = gorduras.toDoubleOrNull() ?: 0.0
                    onSave(tipo, nome, cal, prot, carb, gord, 0.0)
                },
                enabled = nome.isNotBlank()
            ) {
                Text(if (editMeal != null) "Atualizar" else "Adicionar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodSearchDialog(
    query: String,
    results: List<com.nutrifit.app.data.local.entities.FoodEntity>,
    onQueryChange: (String) -> Unit,
    onSelectFood: (com.nutrifit.app.data.local.entities.FoodEntity) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buscar Alimento") },
        text = {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    label = { Text("Digite o nome do alimento") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Search, null) }
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (results.isEmpty() && query.length >= 2) {
                    Text(
                        "Nenhum alimento encontrado",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    results.take(10).forEach { food ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectFood(food) }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(food.nome, style = MaterialTheme.typography.bodyMedium)
                                Text(
                                    "${food.calorias.toInt()} kcal | ${food.proteinas.toInt()}g prot",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Icon(Icons.Default.Add, null, tint = MaterialTheme.colorScheme.primary)
                        }
                        HorizontalDivider()
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Fechar") }
        }
    )
}