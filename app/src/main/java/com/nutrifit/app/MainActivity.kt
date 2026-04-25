package com.nutrifit.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.nutrifit.app.ui.screens.*
import com.nutrifit.app.ui.theme.NutriFitTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NutriFitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NutriFitNavigation()
                }
            }
        }
    }
}

@Composable
fun NutriFitNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "onboarding"
    ) {
        composable("onboarding") {
            OnboardingScreen(
                onComplete = {
                    navController.navigate("home") {
                        popUpTo("onboarding") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            HomeScreen(
                onNavigateToDiary = { navController.navigate("diary") },
                onNavigateToProgress = { navController.navigate("progress") },
                onNavigateToProfile = { navController.navigate("profile") },
                onNavigateToRecipes = { navController.navigate("recipes") },
                onNavigateToSchedule = { navController.navigate("schedule") }
            )
        }

        composable("diary") {
            DiaryScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("progress") {
            ProgressScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("recipes") {
            RecipesScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable("schedule") {
            ScheduleScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}