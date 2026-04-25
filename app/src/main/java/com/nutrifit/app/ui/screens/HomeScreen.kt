package com.nutrifit.app.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nutrifit.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDiary: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToRecipes: () -> Unit
) {
    val scrollState = rememberScrollState()
    // Dados mockados - em produção viriam do ViewModel/Repository
    val caloriasConsumidas = 1420.0
    val caloriasMeta = 1850.0
    val progresso = caloriasConsumidas / caloriasMeta
    val proteinas = 45.0
    val carboidratos = 180.0
    val gorduras = 50.0
    val aguaAtual = 1.2
    val aguaMeta = 2.5
    val streak = 5

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("NutriFit", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Início") },
                    selected = true,
                    onClick = {}
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Restaurant, contentDescription = null) },
                    label = { Text("Diário") },
                    selected = false,
                    onClick = onNavigateToDiary
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
                    label = { Text("Progresso") },
                    selected = false,
                    onClick = onNavigateToProgress
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.MenuBook, contentDescription = null) },
                    label = { Text("Receitas") },
                    selected = false,
                    onClick = onNavigateToRecipes
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // === ANEL DE CALORIAS ===
            CalorieRingCard(
                caloriasConsumidas = caloriasConsumidas,
                caloriasMeta = caloriasMeta,
                progresso = progresso.toFloat()
            )

            // === MACROS ===
            MacrosCard(
                proteinas = proteinas,
                carboidratos = carboidratos,
                gorduras = gorduras
            )

            // === PRÓXIMAS REFEIÇÕES ===
            NextMealsCard()

            // === ÁGUA ===
            WaterCard(aguaAtual = aguaAtual, aguaMeta = aguaMeta)

            // === STREAK ===
            StreakCard(streak = streak)

            // === BOTÕES DE AÇÃO RÁPIDA ===
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onNavigateToDiary,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Registrar")
                }
                OutlinedButton(
                    onClick = onNavigateToRecipes,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sugestões")
                }
            }
        }
    }
}

@Composable
fun CalorieRingCard(
    caloriasConsumidas: Double,
    caloriasMeta: Double,
    progresso: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Calorias do Dia",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Anel de progresso
            Box(
                modifier = Modifier.size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeWidth = 20f
                    val radius = (size.minDimension - strokeWidth) / 2
                    val topLeft = Offset(
                        (size.width - radius * 2) / 2,
                        (size.height - radius * 2) / 2
                    )
                    val arcSize = Size(radius * 2, radius * 2)

                    // Fundo do anel
                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

                    // Progresso
                    drawArc(
                        color = if (progresso <= 1f) ProgressGreen else ProgressRed,
                        startAngle = -90f,
                        sweepAngle = 360f * progresso.coerceIn(0f, 1.2f),
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${caloriasConsumidas.toInt()}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "/ ${caloriasMeta.toInt()} kcal",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status
            val restante = caloriasMeta - caloriasConsumidas
            Text(
                text = if (restante > 0) "Restam ${restante.toInt()} kcal" else "Meta atingida! 🎉",
                style = MaterialTheme.typography.bodyMedium,
                color = if (restante > 0) MaterialTheme.colorScheme.onSurfaceVariant else ProgressGreen
            )
        }
    }
}

@Composable
fun MacrosCard(
    proteinas: Double,
    carboidratos: Double,
    gorduras: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Macronutrientes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                MacroItem(
                    label = "Proteínas",
                    valor = "${proteinas.toInt()}g",
                    cor = ProteinColor
                )
                MacroItem(
                    label = "Carbs",
                    valor = "${carboidratos.toInt()}g",
                    cor = CarbsColor
                )
                MacroItem(
                    label = "Gorduras",
                    valor = "${gorduras.toInt()}g",
                    cor = FatColor
                )
            }
        }
    }
}

@Composable
private fun MacroItem(label: String, valor: String, cor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(cor.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = cor
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun NextMealsCard() {
    val meals = listOf(
        "Café da Manhã" to "Pulei" to "⏰",
        "Almoço" to "12:30 - Frango com arroz" to "🥗",
        "Lanche" to "16:00 - Fruta" to "🍎",
        "Jantar" to "20:00 - Peixe com legumes" to "🍝"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "Próximas Refeições",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            meals.forEach { (meal, info) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = info.third, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = meal.first,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = meal.second,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                if (meal != meals.last()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
    }
}

@Composable
fun WaterCard(aguaAtual: Double, aguaMeta: Double) {
    val progresso = (aguaAtual / aguaMeta).toFloat()

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WaterDrop,
                contentDescription = null,
                tint = WaterBlue,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Água",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progresso },
                    modifier = Modifier.fillMaxWidth(),
                    color = WaterBlue,
                    trackColor = WaterBlue.copy(alpha = 0.2f)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "${String.format("%.1f", aguaAtual)}L / ${String.format("%.1f", aguaMeta)}L",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun StreakCard(streak: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = StreakOrange.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = StreakFire,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "Streak: $streak dias 🔥",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StreakFire
            )
        }
    }
}
