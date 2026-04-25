package com.nutrifit.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutrifit.app.ui.theme.*
import com.nutrifit.app.ui.viewmodel.ProgressViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    onBack: () -> Unit,
    viewModel: ProgressViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    // Dialog para registrar novo peso
    if (uiState.showWeightDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideWeightInput() },
            title = { Text("Registrar Novo Peso") },
            text = {
                OutlinedTextField(
                    value = uiState.novoPeso,
                    onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 6) viewModel.updateNovoPeso(it) },
                    label = { Text("Peso (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    suffix = { Text("kg") }
                )
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.salvarNovoPeso() },
                    enabled = uiState.novoPeso.toDoubleOrNull() != null
                ) {
                    Text("Salvar")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideWeightInput() }) {
                    Text("Cancelar")
                }
            }
        )
    }

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
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
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
                // Mensagem de sucesso
                uiState.mensagemSucesso?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(it, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Card de resumo com dados reais
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
                        if (uiState.kgPerdidos > 0) {
                            ProgressStatItem(
                                valor = String.format("%.1f", uiState.kgPerdidos),
                                label = "kg Perdidos",
                                icone = Icons.Default.TrendingDown,
                                cor = ProgressGreen
                            )
                        } else {
                            ProgressStatItem(
                                valor = String.format("%.1f", uiState.pesoAtual),
                                label = "kg Atual",
                                icone = Icons.Default.Scale,
                                cor = MaterialTheme.colorScheme.primary
                            )
                        }
                        ProgressStatItem(
                            valor = "${uiState.diasRegistrando}",
                            label = "Dias",
                            icone = Icons.Default.CalendarMonth,
                            cor = MaterialTheme.colorScheme.primary
                        )
                        ProgressStatItem(
                            valor = String.format("%.1f", uiState.kgRestantes),
                            label = "kg Restam",
                            icone = Icons.Default.Tour,
                            cor = ProgressYellow
                        )
                    }
                }

                // Mensagem motivacional
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(
                        text = uiState.mensagemMotivacional,
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Progresso percentual
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
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Progresso da Meta",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${String.format("%.1f", uiState.progressoPercentual)}%",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    uiState.progressoPercentual >= 75 -> ProgressGreen
                                    uiState.progressoPercentual >= 50 -> ProgressYellow
                                    else -> MaterialTheme.colorScheme.primary
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        LinearProgressIndicator(
                            progress = (uiState.progressoPercentual / 100).toFloat(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(16.dp),
                            color = when {
                                uiState.progressoPercentual >= 75 -> ProgressGreen
                                uiState.progressoPercentual >= 50 -> ProgressYellow
                                else -> MaterialTheme.colorScheme.primary
                            },
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "De ${String.format("%.1f", uiState.pesoInicial)} kg para ${String.format("%.1f", uiState.pesoMeta)} kg",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Card de calorias da semana
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
                            text = "Calorias dos Últimos 7 Dias",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        uiState.semanaCalorias.forEach { (dia, calorias) ->
                            val maxCal = uiState.semanaCalorias.maxOfOrNull { it.second } ?: 1.0
                            val progresso = (calorias / maxCal).toFloat().coerceIn(0f, 1f)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = dia,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.width(45.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                LinearProgressIndicator(
                                    progress = progresso,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(12.dp),
                                    color = when {
                                        calorias == 0.0 -> MaterialTheme.colorScheme.surfaceVariant
                                        progresso >= 0.8f -> ProgressGreen
                                        progresso >= 0.5f -> ProgressYellow
                                        else -> ProgressRed
                                    },
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (calorias > 0) "${calorias.toInt()} cal" else "---",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.width(70.dp)
                                )
                            }
                        }
                    }
                }

                // Botão para registrar novo peso
                Button(
                    onClick = { viewModel.showWeightInput() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.MonitorWeight, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Registrar Novo Peso")
                }
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