package com.example.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
                title = {
                    AnimatedContent(
                        targetState = getScreenTitle(currentRoute),
                        transitionSpec = {
                            (fadeIn(animationSpec = tween(220, delayMillis = 80)) +
                                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(220)))
                                .togetherWith(
                                    fadeOut(animationSpec = tween(80)) +
                                        slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Down, animationSpec = tween(80))
                                )
                        },
                        label = "TopBarTitleAnimation"
                    ) { titleText ->
                        Text(titleText)
                    }
                },
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
                    animationSpec = tween(380, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(380, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(300))
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(380, easing = FastOutSlowInEasing)
                ) + fadeIn(animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(380, easing = FastOutSlowInEasing)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            composable("home") {
                HomeScreen(navController)
            }
            composable(
                route = "patient_list",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Left,
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(300))
                },
                exitTransition = {
                    if (targetState.destination.route?.startsWith("patient_form") == true) {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Start,
                            animationSpec = tween(350, easing = FastOutSlowInEasing),
                            targetOffset = { it / 4 }
                        ) + scaleOut(
                            targetScale = 0.94f,
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(220))
                    } else {
                        slideOutOfContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Left,
                            animationSpec = tween(380, easing = FastOutSlowInEasing)
                        ) + fadeOut(animationSpec = tween(300))
                    }
                },
                popEnterTransition = {
                    if (initialState.destination.route?.startsWith("patient_form") == true) {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.End,
                            animationSpec = tween(350, easing = FastOutSlowInEasing),
                            initialOffset = { -it / 4 }
                        ) + scaleIn(
                            initialScale = 0.94f,
                            animationSpec = tween(350, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(300))
                    } else {
                        slideIntoContainer(
                            towards = AnimatedContentTransitionScope.SlideDirection.Right,
                            animationSpec = tween(380, easing = FastOutSlowInEasing)
                        ) + fadeIn(animationSpec = tween(300))
                    }
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Right,
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(300))
                }
            ) {
                PatientListScreen(navController, viewModel)
            }
            composable(
                route = "patient_form?patientId={patientId}",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(280))
                },
                exitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + scaleOut(
                        targetScale = 0.95f,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(220))
                },
                popEnterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.95f,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(280))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + scaleOut(
                        targetScale = 0.92f,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(250))
                }
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId")?.toLongOrNull()
                PatientFormScreen(navController, viewModel, patientId)
            }
            composable(
                route = "assessment_form/{patientId}",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(280))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + scaleOut(
                        targetScale = 0.92f,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(250))
                }
            ) { backStackEntry ->
                val patientId = backStackEntry.arguments?.getString("patientId")?.toLongOrNull() ?: 0L
                AssessmentFormScreen(navController, viewModel, patientId)
            }
            composable(
                route = "workouts/{patientId}",
                enterTransition = {
                    slideIntoContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.Start,
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + scaleIn(
                        initialScale = 0.92f,
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeIn(animationSpec = tween(280))
                },
                popExitTransition = {
                    slideOutOfContainer(
                        towards = AnimatedContentTransitionScope.SlideDirection.End,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + scaleOut(
                        targetScale = 0.92f,
                        animationSpec = tween(350, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(250))
                }
            ) { backStackEntry ->
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
