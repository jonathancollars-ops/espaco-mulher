package com.example.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.BuildConfig
import com.example.data.local.PilatesDatabase
import com.example.data.repository.PilatesRepository
import com.example.utils.UpdateChecker
import com.example.ui.theme.AppThemeColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PilatesApp(
    isDarkTheme: Boolean, 
    onThemeToggle: () -> Unit,
    currentThemeColor: AppThemeColor,
    onThemeColorChange: (AppThemeColor) -> Unit
) {
    val context = LocalContext.current
    val database = remember { PilatesDatabase.getDatabase(context) }
    val repository = remember { PilatesRepository(database.pilatesDao()) }
    val viewModel: PilatesViewModel = viewModel(factory = PilatesViewModelFactory(repository))
    
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // GitHub Update Checker
    LaunchedEffect(Unit) {
        UpdateChecker.checkForUpdates(context, BuildConfig.VERSION_NAME)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(getScreenTitle(currentRoute)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                navigationIcon = {
                    if (currentRoute != "home") {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                        }
                    }
                },
                actions = {
                    if (currentRoute == "home") {
                        IconButton(onClick = { navController.navigate("settings") }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Configurações"
                            )
                        }
                    }
                    IconButton(onClick = onThemeToggle) {
                        Icon(
                            imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Alternar Tema Escuro/Claro"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(400))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(400))
            }
        ) {
            composable("home") {
                HomeScreen(navController)
            }
            composable("patient_list") {
                PatientListScreen(navController, viewModel)
            }
            composable("patient_form?patientId={patientId}") { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId")?.toLongOrNull()
                PatientFormScreen(navController, viewModel, patientId)
            }
            composable("assessment_form/{patientId}") { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId")?.toLongOrNull() ?: 0L
                AssessmentFormScreen(navController, viewModel, patientId)
            }
            composable("workouts/{patientId}") { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId")?.toLongOrNull() ?: 0L
                WorkoutsScreen(navController, viewModel, patientId)
            }
            composable("workout_form?patientId={patientId}&workoutId={workoutId}") { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId")?.toLongOrNull() ?: 0L
                val workoutId = backStackEntry.arguments?.getString("workoutId")?.toLongOrNull()
                WorkoutFormScreen(navController, viewModel, patientId, workoutId)
            }
            composable("exercise_list") {
                ExerciseListScreen(navController, viewModel)
            }
            composable("settings") {
                SettingsScreen(
                    navController = navController,
                    currentTheme = currentThemeColor,
                    onThemeChange = onThemeColorChange
                )
            }
        }
    }
}

private fun getScreenTitle(route: String?): String {
    return when {
        route == null -> "Pilates Espaço Mulher"
        route.startsWith("home") -> "Início"
        route.startsWith("patient_list") -> "Pacientes"
        route.startsWith("patient_form") -> "Ficha do Paciente"
        route.startsWith("assessment_form") -> "Avaliação Clínica"
        route.startsWith("workouts") -> "Fichas de Treino"
        route.startsWith("workout_form") -> "Editar Treino"
        route.startsWith("exercise_list") -> "Biblioteca de Exercícios"
        route.startsWith("settings") -> "Configurações"
        else -> "Pilates Espaço Mulher"
    }
}
