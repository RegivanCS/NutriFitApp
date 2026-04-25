package com.nutrifit.app.ui.screens

import androidx.compose.animation.*
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
import com.nutrifit.app.data.local.entities.UserEntity
import com.nutrifit.app.data.repository.NutriFitRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    onComplete: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(0) }
    val totalSteps = 4
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    // Estados dos campos
    var nome by remember { mutableStateOf("") }
    var idade by remember { mutableStateOf("") }
    var sexo by remember { mutableStateOf("") }
    var alturaCm by remember { mutableStateOf("") }
    var pesoAtualKg by remember { mutableStateOf("") }
    var pesoMetaKg by remember { mutableStateOf("") }
    var nivelAtividade by remember { mutableStateOf("sedentario") }
    var objetivo by remember { mutableStateOf("emagrecer") }

    // Estados de validação
    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            if (currentStep > 0) {
                TopAppBar(
                    title = { Text("NutriFit") },
                    navigationIcon = {
                        IconButton(onClick = { if (currentStep > 0) currentStep-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Indicador de progresso
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(totalSteps) { step ->
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(
                                width = if (step == currentStep) 24.dp else 8.dp,
                                height = 8.dp
                            )
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = MaterialTheme.shapes.small,
                            color = if (step <= currentStep) 
                                MaterialTheme.colorScheme.primary 
                            else MaterialTheme.colorScheme.surfaceVariant
                        ) {}
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Conteúdo baseado no passo
            when (currentStep) {
                0 -> StepWelcome(
                    nome = nome,
                    onNomeChange = { nome = it },
                    onContinue = {
                        if (nome.isNotBlank()) {
                            currentStep++
                            showError = false
                        } else {
                            showError = true
                            errorMessage = "Por favor, digite seu nome"
                        }
                    }
                )
                1 -> StepPersonalData(
                    idade = idade,
                    onIdadeChange = { idade = it },
                    sexo = sexo,
                    onSexoChange = { sexo = it },
                    alturaCm = alturaCm,
                    onAlturaChange = { alturaCm = it },
                    onContinue = {
                        when {
                            idade.toIntOrNull() == null || idade.toInt() < 10 || idade.toInt() > 120 -> {
                                showError = true; errorMessage = "Idade inválida"
                            }
                            sexo.isBlank() -> { showError = true; errorMessage = "Selecione o sexo" }
                            alturaCm.toDoubleOrNull() == null || alturaCm.toDouble() < 100 -> {
                                showError = true; errorMessage = "Altura inválida"
                            }
                            else -> { currentStep++; showError = false }
                        }
                    }
                )
                2 -> StepWeightGoals(
                    pesoAtualKg = pesoAtualKg,
                    onPesoAtualChange = { pesoAtualKg = it },
                    pesoMetaKg = pesoMetaKg,
                    onPesoMetaChange = { pesoMetaKg = it },
                    onContinue = {
                        when {
                            pesoAtualKg.toDoubleOrNull() == null || pesoAtualKg.toDouble() < 30 -> {
                                showError = true; errorMessage = "Peso inválido"
                            }
                            pesoMetaKg.toDoubleOrNull() == null || pesoMetaKg.toDouble() <= 0 -> {
                                showError = true; errorMessage = "Peso meta inválido"
                            }
                            else -> { currentStep++; showError = false }
                        }
                    }
                )
                3 -> StepActivityAndGoal(
                    nivelAtividade = nivelAtividade,
                    onNivelChange = { nivelAtividade = it },
                    objetivo = objetivo,
                    onObjetivoChange = { objetivo = it },
                    onFinish = {
                        // Salvar usuário
                        coroutineScope.launch {
                            val user = UserEntity(
                                nome = nome,
                                idade = idade.toInt(),
                                sexo = sexo,
                                alturaCm = alturaCm.toDouble(),
                                pesoInicialKg = pesoAtualKg.toDouble(),
                                pesoAtualKg = pesoAtualKg.toDouble(),
                                pesoMetaKg = pesoMetaKg.toDouble(),
                                nivelAtividade = nivelAtividade,
                                objetivo = objetivo
                            )
                            // Aqui deveria salvar via ViewModel/Repository
                            onComplete()
                        }
                    }
                )
            }

            // Mensagem de erro
            if (showError) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun StepWelcome(
    nome: String,
    onNomeChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Bem-vindo ao NutriFit!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Vamos ajudar você a alcançar seus objetivos de saúde e bem-estar de forma saudável e sustentável.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))
        OutlinedTextField(
            value = nome,
            onValueChange = onNomeChange,
            label = { Text("Seu nome") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
            enabled = nome.isNotBlank()
        ) {
            Text("Vamos começar!")
        }
    }
}

@Composable
private fun StepPersonalData(
    idade: String,
    onIdadeChange: (String) -> Unit,
    sexo: String,
    onSexoChange: (String) -> Unit,
    alturaCm: String,
    onAlturaChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Dados Pessoais",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = idade,
            onValueChange = { if (it.all { c -> c.isDigit() } && it.length <= 3) onIdadeChange(it) },
            label = { Text("Idade") },
            leadingIcon = { Icon(Icons.Default.Cake, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Sexo biológico", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = sexo == "M",
                onClick = { onSexoChange("M") },
                label = { Text("Masculino") },
                leadingIcon = if (sexo == "M") {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = sexo == "F",
                onClick = { onSexoChange("F") },
                label = { Text("Feminino") },
                leadingIcon = if (sexo == "F") {
                    { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                } else null,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = alturaCm,
            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 5) onAlturaChange(it) },
            label = { Text("Altura (cm)") },
            leadingIcon = { Icon(Icons.Default.Height, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            suffix = { Text("cm") }
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text("Continuar")
        }
    }
}

@Composable
private fun StepWeightGoals(
    pesoAtualKg: String,
    onPesoAtualChange: (String) -> Unit,
    pesoMetaKg: String,
    onPesoMetaChange: (String) -> Unit,
    onContinue: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Peso e Metas",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        OutlinedTextField(
            value = pesoAtualKg,
            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 6) onPesoAtualChange(it) },
            label = { Text("Peso atual (kg)") },
            leadingIcon = { Icon(Icons.Default.Scale, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            suffix = { Text("kg") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = pesoMetaKg,
            onValueChange = { if (it.all { c -> c.isDigit() || c == '.' } && it.length <= 6) onPesoMetaChange(it) },
            label = { Text("Peso meta (kg)") },
            leadingIcon = { Icon(Icons.Default.Tour, contentDescription = null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            suffix = { Text("kg") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        if (pesoAtualKg.toDoubleOrNull() != null && pesoMetaKg.toDoubleOrNull() != null) {
            val diferenca = pesoAtualKg.toDouble() - pesoMetaKg.toDouble()
            if (diferenca > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = "🎯 Você quer perder ${String.format("%.1f", diferenca)} kg",
                        modifier = Modifier.padding(16.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onContinue, modifier = Modifier.fillMaxWidth()) {
            Text("Continuar")
        }
    }
}

@Composable
private fun StepActivityAndGoal(
    nivelAtividade: String,
    onNivelChange: (String) -> Unit,
    objetivo: String,
    onObjetivoChange: (String) -> Unit,
    onFinish: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Estilo de Vida",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Nível de atividade física", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        
        val atividades = listOf(
            "sedentario" to "Sedentário",
            "levemente_ativo" to "Leve",
            "moderadamente_ativo" to "Moderado",
            "muito_ativo" to "Intenso",
            "extremamente_ativo" to "Extremo"
        )
        
        atividades.forEach { (key, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = nivelAtividade == key,
                    onClick = { onNivelChange(key) }
                )
                Text(
                    text = label,
                    modifier = Modifier.padding(start = 8.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("Seu objetivo principal", style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = objetivo == "emagrecer",
                onClick = { onObjetivoChange("emagrecer") },
                label = { Text("Emagrecer") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = objetivo == "manter",
                onClick = { onObjetivoChange("manter") },
                label = { Text("Manter") },
                modifier = Modifier.weight(1f)
            )
            FilterChip(
                selected = objetivo == "ganhar_massa",
                onClick = { onObjetivoChange("ganhar_massa") },
                label = { Text("Ganhar massa") },
                modifier = Modifier.weight(1f)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onFinish,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Finalizar Cadastro!")
        }
    }
}
