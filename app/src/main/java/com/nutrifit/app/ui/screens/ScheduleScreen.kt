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
import com.nutrifit.app.ui.viewmodel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    onBack: () -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar Refeições") },
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
            // Card explicativo do sistema
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
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "💡 Recomendações Inteligentes",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Baseado no seu perfil, objetivo e hora do dia, " +
                            "o app sugere o que comer e explica os benefícios. " +
                            "O celular vai tocar na hora certa ⏰🔔",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.schedules, key = { it.id }) { schedule ->
                        val recomendacao = uiState.recomendacoes[schedule.tipo]

                        ScheduleCard(
                            schedule = schedule,
                            recomendacao = recomendacao,
                            onToggleActive = { viewModel.alternarAtivo(schedule) },
                            onEditTime = { novoHorario ->
                                viewModel.atualizarHorario(schedule, novoHorario)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(8.dp))

                        // Card de exercício
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "🏋️ Lembrete de Exercício Físico",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "18:00 - Todo dia 🔔 Alarme programado!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "🏃 30 min de atividade já fazem diferença!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleCard(
    schedule: com.nutrifit.app.data.local.entities.MealScheduleEntity,
    recomendacao: com.nutrifit.app.nutrition.RefeicaoRecomendada?,
    onToggleActive: () -> Unit,
    onEditTime: (String) -> Unit
) {
    var showRecomendacao by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var selectedHour by remember { mutableStateOf(schedule.horario.split(":").getOrElse(0) { "" }) }
    var selectedMinute by remember { mutableStateOf(schedule.horario.split(":").getOrElse(1) { "" }) }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Alterar horário") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = selectedHour,
                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) selectedHour = it },
                        label = { Text("Hora (0-23)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = selectedMinute,
                        onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 2) selectedMinute = it },
                        label = { Text("Minuto (0-59)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val hora = selectedHour.toIntOrNull()
                        val minuto = selectedMinute.toIntOrNull()
                        if (hora != null && minuto != null && hora in 0..23 && minuto in 0..59) {
                            onEditTime(String.format("%02d:%02d", hora, minuto))
                            showTimePicker = false
                        }
                    },
                    enabled = selectedHour.isNotBlank() && selectedMinute.isNotBlank()
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    // Dialog de recomendação detalhada
    if (showRecomendacao && recomendacao != null) {
        RecomendacaoDetalhadaDialog(
            recomendacao = recomendacao,
            onDismiss = { showRecomendacao = false }
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(if (schedule.ativo) 2.dp else 0.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (schedule.ativo) MaterialTheme.colorScheme.surface
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            // Linha principal: ícone + nome + horário + switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val emoji = when (schedule.tipo) {
                    "cafe_da_manha" -> "☕"
                    "lanche_manha" -> "🍎"
                    "almoco" -> "🍗"
                    "lanche_tarde" -> "🥜"
                    "jantar" -> "🥗"
                    "ceia" -> "🌙"
                    else -> "🍽️"
                }
                Text(
                    text = emoji,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.width(8.dp))

                Column(modifier = Modifier.weight(1f)) {
                    val nomeTipo = when (schedule.tipo) {
                        "cafe_da_manha" -> "Café da Manhã"
                        "lanche_manha" -> "Lanche da Manhã"
                        "almoco" -> "Almoço"
                        "lanche_tarde" -> "Lanche da Tarde"
                        "jantar" -> "Jantar"
                        "ceia" -> "Ceia"
                        else -> schedule.tipo
                    }
                    Text(
                        text = nomeTipo,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = schedule.horario,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (schedule.ativo) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (schedule.ativo) {
                    IconButton(onClick = {
                        selectedHour = schedule.horario.split(":").getOrElse(0) { "" }
                        selectedMinute = schedule.horario.split(":").getOrElse(1) { "" }
                        showTimePicker = true
                    }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar horário", modifier = Modifier.size(20.dp))
                    }
                }

                Switch(
                    checked = schedule.ativo,
                    onCheckedChange = { onToggleActive() }
                )
            }

            // Se ativo, mostrar a sugestão recomendada
            if (schedule.ativo && recomendacao != null) {
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                // Card com a sugestão
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.3f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                                tint = MaterialTheme.colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "🥗 Sugerido: ${recomendacao.nome}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = recomendacao.descricao,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        // Macros da sugestão
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            MacroMini("${recomendacao.calorias}", "kcal", MaterialTheme.colorScheme.primary)
                            MacroMini("${recomendacao.proteinas}", "prot", MaterialTheme.colorScheme.tertiary)
                            MacroMini("${recomendacao.carboidratos}", "carb", MaterialTheme.colorScheme.secondary)
                            MacroMini("${recomendacao.gorduras}", "gord", MaterialTheme.colorScheme.error)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Botão para ver explicação detalhada
                        FilledTonalButton(
                            onClick = { showRecomendacao = true },
                            modifier = Modifier.fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                Icons.Default.Info,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "📖 Entender por que comer isso",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MacroMini(valor: String, label: String, cor: androidx.compose.ui.graphics.Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = valor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = cor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * Dialog com a explicação detalhada da recomendação nutricional.
 */
@Composable
fun RecomendacaoDetalhadaDialog(
    recomendacao: com.nutrifit.app.nutrition.RefeicaoRecomendada,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                recomendacao.nome,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Descrição do prato
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Restaurant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🍽️ ${recomendacao.descricao}",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                // Tabela nutricional
                Surface(
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "📊 Informação Nutricional",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        NutricionalRow("Calorias", "${recomendacao.calorias} kcal", "🔥")
                        NutricionalRow("Proteínas", "${recomendacao.proteinas}g", "🥩")
                        NutricionalRow("Carboidratos", "${recomendacao.carboidratos}g", "🍚")
                        NutricionalRow("Gorduras", "${recomendacao.gorduras}g", "🥑")
                        if (recomendacao.fibras > 0) {
                            NutricionalRow("Fibras", "${recomendacao.fibras}g", "🥦")
                        }
                    }
                }

                HorizontalDivider()

                // EXPLICAÇÃO DETALHADA
                Text(
                    text = recomendacao.explicacao,
                    style = MaterialTheme.typography.bodySmall,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight
                )

                HorizontalDivider()

                // Variações
                if (recomendacao.sugestoesVariacao.isNotEmpty()) {
                    Text(
                        text = "🔄 Variações sugeridas:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    recomendacao.sugestoesVariacao.forEach { variacao ->
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = MaterialTheme.shapes.small,
                            color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "• ",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = variacao,
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Entendi! 👌")
            }
        }
    )
}

@Composable
private fun NutricionalRow(label: String, valor: String, emoji: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$emoji $label",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}