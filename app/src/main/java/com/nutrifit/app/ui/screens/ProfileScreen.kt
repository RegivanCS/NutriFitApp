package com.nutrifit.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.nutrifit.app.ui.viewmodel.ProfileViewModel
import com.nutrifit.app.ui.viewmodel.ProfileUiState
import org.json.JSONObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.mensagemSucesso) {
        if (uiState.mensagemSucesso != null) {
            kotlinx.coroutines.delay(3000)
            viewModel.limparMensagens()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meu Perfil") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum usuário cadastrado. Faça o onboarding primeiro!")
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
                val user = uiState.user!!

                // Mensagens de feedback
                uiState.mensagemSucesso?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(it, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (uiState.mensagemErro != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(uiState.mensagemErro!!, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
                    }
                }

                // Avatar e nome
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier
                                .padding(20.dp)
                                .fillMaxSize(),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = user.nome,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    val objetivoLabel = when (user.objetivo) {
                        "emagrecer" -> "Emagrecimento Saudável"
                        "manter" -> "Manutenção do Peso"
                        "ganhar_massa" -> "Ganho de Massa Muscular"
                        else -> ""
                    }
                    Text(
                        text = objetivoLabel,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (!uiState.isEditing) {
                    // Modo VISUALIZAÇÃO
                    // Métricas do usuário
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
                                text = "Suas Métricas",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(12.dp))

                            ProfileMetric("Idade", "${user.idade} anos", Icons.Default.Cake)
                            ProfileMetric("Altura", String.format("%.1f cm", user.alturaCm), Icons.Default.Height)
                            ProfileMetric("Peso Atual", String.format("%.1f kg", user.pesoAtualKg), Icons.Default.Scale)
                            ProfileMetric("Peso Inicial", String.format("%.1f kg", user.pesoInicialKg), Icons.Default.ArrowBack)
                            ProfileMetric("Peso Meta", String.format("%.1f kg", user.pesoMetaKg), Icons.Default.Tour)

                            // IMC do Python se disponível
                            val imcInfo = uiState.metasCalculadas?.optJSONObject("imc")
                            if (imcInfo != null) {
                                val imc = imcInfo.optDouble("imc", 0.0)
                                val classificacao = imcInfo.optString("classificacao", "")
                                ProfileMetric("IMC", String.format("%.1f - %s", imc, classificacao), Icons.Default.MonitorHeart)
                            }
                        }
                    }

                    // Metas calculadas pelo Python
                    uiState.metasCalculadas?.let { metas ->
                        val metasObj = metas.optJSONObject("metas")
                        if (metasObj != null) {
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
                                        text = "Metas Diárias",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))

                                    ProfileMetric("Calorias", "${metasObj.optInt("calorias_meta", 0)} kcal", Icons.Default.LocalFireDepartment)

                                    val macros = metasObj.optJSONObject("macronutrientes")
                                    if (macros != null) {
                                        ProfileMetric("Proteínas", "${macros.optJSONObject("proteinas")?.optInt("gramas", 0)}g", Icons.Default.FitnessCenter)
                                        ProfileMetric("Carboidratos", "${macros.optJSONObject("carboidratos")?.optInt("gramas", 0)}g", Icons.Default.BakeryDining)
                                        ProfileMetric("Gorduras", "${macros.optJSONObject("gorduras")?.optInt("gramas", 0)}g", Icons.Default.OilBarrel)
                                    }

                                    val agua = metas.optJSONObject("agua")
                                    if (agua != null) {
                                        val litros = agua.optDouble("agua_litros", 0.0)
                                        ProfileMetric("Água", String.format("%.1fL", litros), Icons.Default.WaterDrop)
                                    }
                                }
                            }
                        }
                    }

                    // Botões
                    Button(
                        onClick = { viewModel.toggleEditing() },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Editar Perfil")
                    }

                    OutlinedButton(
                        onClick = { viewModel.resetarDados() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.DeleteForever, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Resetar Peso ao Inicial")
                    }

                } else {
                    // Modo EDIÇÃO
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
                                text = "Editar Perfil",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            OutlinedTextField(
                                value = uiState.editNome,
                                onValueChange = { viewModel.updateNome(it) },
                                label = { Text("Nome") },
                                leadingIcon = { Icon(Icons.Default.Person, null) },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = uiState.editIdade,
                                onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 3) viewModel.updateIdade(it) },
                                label = { Text("Idade") },
                                leadingIcon = { Icon(Icons.Default.Cake, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = uiState.editAltura,
                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 5) viewModel.updateAltura(it) },
                                label = { Text("Altura (cm)") },
                                leadingIcon = { Icon(Icons.Default.Height, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                suffix = { Text("cm") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = uiState.editPesoAtual,
                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 6) viewModel.updatePesoAtual(it) },
                                label = { Text("Peso Atual (kg)") },
                                leadingIcon = { Icon(Icons.Default.Scale, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                suffix = { Text("kg") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = uiState.editPesoMeta,
                                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 6) viewModel.updatePesoMeta(it) },
                                label = { Text("Peso Meta (kg)") },
                                leadingIcon = { Icon(Icons.Default.Tour, null) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                suffix = { Text("kg") }
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = { viewModel.toggleEditing() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Close, null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = { viewModel.salvarAlteracoes() },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Check, null)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Salvar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileMetric(
    label: String,
    valor: String,
    icone: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icone,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
    }
}