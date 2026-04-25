package com.nutrifit.app.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutrifit.app.ui.theme.*
import com.nutrifit.app.ui.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDiary: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToRecipes: () -> Unit,
    onNavigateToSchedule: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val progresso = if (uiState.caloriasMeta > 0)
        (uiState.caloriasConsumidas / uiState.caloriasMeta).toFloat().coerceIn(0f, 1.2f) else 0f

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
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        "Carregando...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(scrollState)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Saudação personalizada
                Text(
                    text = "Olá, ${uiState.nomeUsuario}! 👋",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                // === PRÓXIMA REFEIÇÃO EM DESTAQUE ===
                uiState.proximaRefeicao?.let { (nome, horario, tempoRestante) ->
                    NextMealHighlight(
                        nome = nome,
                        horario = horario,
                        tempoRestante = tempoRestante,
                        onNavigateToDiary = onNavigateToDiary
                    )
                }

                // === ANEL DE CALORIAS ===
                CalorieRingCard(
                    caloriasConsumidas = uiState.caloriasConsumidas,
                    caloriasMeta = uiState.caloriasMeta,
                    progresso = progresso
                )

                // === MACROS ===
                MacrosCard(
                    proteinas = uiState.proteinas,
                    carboidratos = uiState.carboidratos,
                    gorduras = uiState.gorduras
                )

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
                        Text("Receitas")
                    }
                    OutlinedButton(
                        onClick = onNavigateToSchedule,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.Alarm, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Agenda")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun NextMealHighlight(
    nome: String,
    horario: String,
    tempoRestante: String,
    onNavigateToDiary: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.AlarmOn,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "PRÓXIMA REFEIÇÃO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                    Text(
                        text = nome,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = horario,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "Horário",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = tempoRestante,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Text(
                        text = "para começar",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            FilledTonalButton(
                onClick = onNavigateToDiary,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f),
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Icon(Icons.Default.Restaurant, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver no Diário Alimentar")
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

                    drawArc(
                        color = Color.LightGray.copy(alpha = 0.3f),
                        startAngle = -90f,
                        sweepAngle = 360f,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                    )

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