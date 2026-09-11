package com.example.ui

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.local.Patient
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(navController: NavController, viewModel: PilatesViewModel) {
    val patients by viewModel.patients.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    val filterOptions = listOf("Todos", "Particular", "Convênio")
    var selectedFilter by remember { mutableStateOf(filterOptions[0]) }

    val filteredPatients = remember(patients, selectedFilter) {
        patients.filter { patient ->
            when (selectedFilter) {
                "Particular" -> patient.healthInsurance.isBlank()
                "Convênio" -> patient.healthInsurance.isNotBlank()
                else -> true
            }
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    navController.navigate("patient_form") 
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Adicionar Paciente")
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Buscar paciente...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filterOptions.forEach { option ->
                    FilterChip(
                        selected = selectedFilter == option,
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            selectedFilter = option
                        },
                        label = { Text(option) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPatients) { patient ->
                    PatientCard(
                        patient = patient,
                        onClickEdit = { navController.navigate("patient_form?patientId=\${patient.id}") },
                        onClickAssessment = { navController.navigate("assessment_form/\${patient.id}") },
                        onClickWorkouts = { navController.navigate("workouts/\${patient.id}") }
                    )
                }
            }
        }
    }
}

@Composable
fun PatientCard(
    patient: Patient,
    onClickEdit: () -> Unit,
    onClickAssessment: () -> Unit,
    onClickWorkouts: () -> Unit
) {
    val haptic = LocalHapticFeedback.current
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = patient.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text(text = "\${patient.age} anos | \${patient.phone}", style = MaterialTheme.typography.bodyMedium)
            Text(text = patient.healthInsurance.ifBlank { "Particular" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClickEdit() 
                }, modifier = Modifier.weight(1f)) {
                    Text("Editar")
                }
                Button(onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClickAssessment() 
                }, modifier = Modifier.weight(1f)) {
                    Text("Avaliar")
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { 
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onClickWorkouts() 
                }, 
                modifier = Modifier.fillMaxWidth(), 
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text("Fichas de Treino")
            }
        }
    }
}
