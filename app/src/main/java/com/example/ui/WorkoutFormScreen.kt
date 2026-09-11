package com.example.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.local.CustomExercise
import com.example.data.local.Workout
import com.example.data.local.WorkoutExercise

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutFormScreen(navController: NavController, viewModel: PilatesViewModel, patientId: Long, workoutId: Long?) {
    val haptic = LocalHapticFeedback.current
    val customExercises by viewModel.customExercises.collectAsState()

    var title by remember { mutableStateOf("") }
    var currentExercises by remember { mutableStateOf<List<WorkoutExercise>>(emptyList()) }

    LaunchedEffect(workoutId) {
        if (workoutId != null) {
            viewModel.getWorkoutsForPatient(patientId).collect { workouts ->
                workouts.find { it.id == workoutId }?.let { title = it.title }
            }
            viewModel.getExercisesForWorkout(workoutId).collect { exercises ->
                currentExercises = exercises.sortedBy { it.orderIndex }
            }
        }
    }

    var showAddExerciseDialog by remember { mutableStateOf(false) }
    var selectedExerciseName by remember { mutableStateOf("") }
    var setsReps by remember { mutableStateOf("") }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    showAddExerciseDialog = true 
                },
                containerColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Exercício")
            }
        },
        bottomBar = {
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    val workout = Workout(id = workoutId ?: 0L, patientId = patientId, title = title.ifBlank { "Ficha de Treino" })
                    viewModel.saveWorkoutWithExercises(workout, currentExercises) {
                        navController.navigateUp()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salvar Ficha", modifier = Modifier.padding(8.dp))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                InsetTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = "Nome da Ficha",
                    isLast = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Exercícios da Ficha", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (currentExercises.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        "Nenhum exercício adicionado.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    LazyColumn {
                        items(currentExercises) { exercise ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = exercise.exerciseName, style = MaterialTheme.typography.bodyLarge)
                                    Text(text = exercise.setsReps, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                IconButton(onClick = {
                                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    currentExercises = currentExercises.filter { it != exercise }
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remover", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                            if (exercise != currentExercises.last()) {
                                HorizontalDivider(modifier = Modifier.padding(start = 16.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddExerciseDialog) {
        var isDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddExerciseDialog = false },
            title = { Text("Adicionar Exercício") },
            text = {
                Column {
                    ExposedDropdownMenuBox(
                        expanded = isDropdownExpanded,
                        onExpandedChange = { isDropdownExpanded = it },
                    ) {
                        OutlinedTextField(
                            value = selectedExerciseName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Selecione o Exercício") },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false }
                        ) {
                            if (customExercises.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Nenhum exercício cadastrado.") },
                                    onClick = { isDropdownExpanded = false }
                                )
                            } else {
                                customExercises.forEach { exercise ->
                                    DropdownMenuItem(
                                        text = { Text(exercise.name) },
                                        onClick = {
                                            selectedExerciseName = exercise.name
                                            isDropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                        value = setsReps,
                        onValueChange = { setsReps = it },
                        label = { Text("Séries e Repetições (ex: 3x10)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (selectedExerciseName.isNotBlank() && setsReps.isNotBlank()) {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            val newExercise = WorkoutExercise(
                                workoutId = workoutId ?: 0L,
                                exerciseName = selectedExerciseName,
                                setsReps = setsReps,
                                orderIndex = currentExercises.size
                            )
                            currentExercises = currentExercises + newExercise
                            selectedExerciseName = ""
                            setsReps = ""
                            showAddExerciseDialog = false
                        }
                    }
                ) {
                    Text("Adicionar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddExerciseDialog = false }) {
                    Text("Cancelar")
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}
