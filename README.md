# 🥗 NutriFit App

Aplicativo mobile Android para emagrecimento saudável com inteligência nutricional.

## 🏗️ Arquitetura

```
App Android (Kotlin + Jetpack Compose)
    ↕ Ponte via Chaquopy
Motor Python (Cálculos Nutricionais, IA)
    ↕
SQLite Local (Room Database)
```

## 🛠️ Stack Tecnológica

| Componente | Tecnologia |
|-----------|------------|
| **Linguagem** | Kotlin + Python |
| **UI** | Jetpack Compose + Material Design 3 |
| **Banco Local** | Room (SQLite) |
| **Motor Lógica** | Python via Chaquopy |
| **Injeção Dependência** | Hilt |
| **Navegação** | Navigation Compose |

## 📱 Funcionalidades

- ✅ **Perfil do Usuário** - Cadastro completo com cálculo de IMC
- ✅ **Metas Inteligentes** - Cálculo automático de TMB e metas calóricas
- ✅ **Diário Alimentar** - Registro e acompanhamento diário
- ✅ **Progresso** - Gráficos e evolução de peso
- ✅ **Receitas Saudáveis** - Sugestões inteligentes de refeições
- ✅ **Controle de Água** - Meta diária de hidratação
- ✅ **Gamificação** - Streaks e conquistas
- ✅ **100% Offline** - Todos os dados salvos localmente

## 🐍 Motor Python

O Python roda nativamente no Android via Chaquopy, fornecendo:

- `nutrition_engine.py` - Cálculos de IMC, TMB, macros
- `meal_suggester.py` - Sugestão inteligente de refeições
- `database_manager.py` - Análises e relatórios do banco

## 📁 Estrutura do Projeto

```
NutriFitApp/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/nutrifit/app/
│       │   ├── NutriFitApp.kt          # Application class
│       │   ├── MainActivity.kt         # Navigation
│       │   ├── data/
│       │   │   ├── local/              # Room DB + DAOs + Entities
│       │   │   └── repository/         # Repository pattern
│       │   ├── python/
│       │   │   └── PythonBridge.kt     # Kotlin ↔ Python
│       │   └── ui/
│       │       ├── screens/            # Telas Compose
│       │       └── theme/              # Tema Material 3
│       └── python/                     # Código Python
│           ├── nutrition_engine.py
│           ├── meal_suggester.py
│           └── database_manager.py
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 🚀 Como usar

1. Abra no Android Studio
2. Sincronize o Gradle
3. Execute em um dispositivo Android (API 26+)

## 📊 Funcionalidades Inovadoras

- **Scanner de código de barras** para alimentos
- **Gamificação** com conquistas e streaks
- **Sugestão inteligente** de refeições via Python
- **Relatório semanal** automático
- **Previsão de peso** baseada no ritmo atual
- **Substituições saudáveis** inteligentes
