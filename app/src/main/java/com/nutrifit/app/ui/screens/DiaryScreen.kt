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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiaryScreen(
    onBack: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diário Alimentar") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { /* Adicionar refeição */ }) {
                        Icon(Icons.Default.Add, contentDescription = "Adicionar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Seletor de data
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedDate = selectedDate.minusDays(1) }) {
                    Icon(Icons.Default.ChevronLeft, contentDescription = "Dia anterior")
                }
                Text(
                    text = selectedDate.format(dateFormatter),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = { selectedDate = selectedDate.plusDays(1) }) {
                    Icon(Icons.Default.ChevronRight, contentDescription = "Próximo dia")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Resumo do dia
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
                        Text("1.420", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Calorias", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("45g", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Proteínas", style = MaterialTheme.typography.bodySmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("1.2L", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text("Água", style = MaterialTheme.typography.bodySmall)
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

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Refeições mockadas
                val meals = listOf(
                    MealItemData("Café da Manhã", "Pulei", 0.0, Icons.Default.Warning),
                    MealItemData("Lanche da Manhã", "Banana com castanhas", 180.0, Icons.Default.Fastfood),
                    MealItemData("Almoço", "Frango grelhado, arroz integral e brócolis", 450.0, Icons.Default.Restaurant),
                    MealItemData("Lanche da Tarde", "Smoothie verde", 200.0, Icons.Default.Fastfood),
                    MealItemData("Jantar", "Salmão com quinoa e legumes", 480.0, Icons.Default.Restaurant),
                    MealItemData("Ceia", "Chá de camomila", 10.0, Icons.Default.LocalCafe)
                )

                items(meals) { meal ->
                    MealCard(meal = meal)
                }
            }
        }
    }
}

data class MealItemData(
    val tipo: String,
    val nome: String,
    val calorias: Double,
    val icone: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun MealCard(meal: MealItemData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = meal.icone,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = meal.tipo,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = meal.nome,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${meal.calorias.toInt()} kcal",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
