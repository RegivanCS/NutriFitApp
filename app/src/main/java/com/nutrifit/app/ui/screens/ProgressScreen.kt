package com.nutrifit.app.ui.screens

import androidx.compose.foundation.layout.*
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
import com.nutrifit.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Progresso") },
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
                .verticalScroll(scrollState)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Card de resumo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ProgressStatItem(
                        valor = "5.2",
                        label = "kg Perdidos",
                        icone = Icons.Default.TrendingDown,
                        cor = ProgressGreen
                    )
                    ProgressStatItem(
                        valor = "23",
                        label = "Dias",
                        icone = Icons.Default.CalendarMonth,
                        cor = MaterialTheme.colorScheme.primary
                    )
                    ProgressStatItem(
                        valor = "8",
                        label = "kg Restam",
                        icone = Icons.Default.Tour,
                        cor = ProgressYellow
                    )
                }
            }

            // Gráfico de peso (placeholder)
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
                        text = "Evolução do Peso",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Placeholder do gráfico
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                            )
                            Text(
                                text = "Gráfico será exibido aqui",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Progresso semanal
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
                        text = "Progresso Semanal",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    WeekDayProgress("Seg", 0.8f, "1.850 cal")
                    WeekDayProgress("Ter", 0.6f, "1.420 cal")
                    WeekDayProgress("Qua", 0.9f, "1.720 cal")
                    WeekDayProgress("Qui", 0.7f, "1.550 cal")
                    WeekDayProgress("Sex", 0.5f, "1.200 cal")
                    WeekDayProgress("Sáb", 0.4f, "980 cal")
                    WeekDayProgress("Dom", 0.0f, "---")
                }
            }

            // Conquistas recentes
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
                        text = "Conquistas",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    AchievementItem(
                        icone = "🔥",
                        titulo = "Primeira Semana",
                        descricao = "Completou 7 dias de registros"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    AchievementItem(
                        icone = "💪",
                        titulo = "Meta do Dia",
                        descricao = "Bateu a meta de calorias por 3 dias seguidos"
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    AchievementItem(
                        icone = "🏆",
                        titulo = "Dedicação",
                        descricao = "Streak de 5 dias"
                    )
                }
            }

            // Botão para registrar novo progresso
            Button(
                onClick = { /* Registrar novo peso */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.MonitorWeight, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar Novo Peso")
            }
        }
    }
}

@Composable
private fun ProgressStatItem(
    valor: String,
    label: String,
    icone: androidx.compose.ui.graphics.vector.ImageVector,
    cor: androidx.compose.ui.graphics.Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = cor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = valor,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = cor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun WeekDayProgress(dia: String, progresso: Float, calorias: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = dia,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.width(40.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        LinearProgressIndicator(
            progress = { progresso },
            modifier = Modifier
                .weight(1f)
                .height(12.dp),
            color = when {
                progresso >= 0.8f -> ProgressGreen
                progresso >= 0.5f -> ProgressYellow
                else -> ProgressRed
            },
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = calorias,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
    }
}

@Composable
private fun AchievementItem(
    icone: String,
    titulo: String,
    descricao: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icone, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = titulo,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = descricao,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
