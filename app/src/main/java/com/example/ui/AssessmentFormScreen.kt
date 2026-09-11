package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.local.Assessment
import com.example.utils.PdfService

@Composable
fun AssessmentFormScreen(navController: NavController, viewModel: PilatesViewModel, patientId: Long) {
    val patientState = viewModel.getPatientById(patientId).collectAsState(initial = null)
    val patient = patientState.value
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Anamnese", "Postural", "Bioimpedância")

    var weight by remember { mutableStateOf("") }
    var height by remember { mutableStateOf("") }
    var bmi by remember { mutableStateOf("") }
    var professionalAnalysis by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title) }
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // Anamnesis placeholder for simplicity
                    Text("Dados de Anamnese (Simplificado para o Protótipo)")
                }
                1 -> {
                    // Postural placeholder
                    Text("Avaliação Postural (Simplificado para o Protótipo)")
                }
                2 -> {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            InsetTextField(value = weight, onValueChange = { 
                                weight = it 
                                bmi = calculateBMI(weight, height)
                            }, label = "Peso (kg)")
                            
                            InsetTextField(value = height, onValueChange = { 
                                height = it 
                                bmi = calculateBMI(weight, height)
                            }, label = "Altura (m)")
                            
                            InsetTextField(value = bmi, onValueChange = { bmi = it }, label = "IMC")
                            InsetTextField(value = professionalAnalysis, onValueChange = { professionalAnalysis = it }, label = "Análise Profissional", isLast = true)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    val newAssessment = Assessment(
                        patientId = patientId,
                        weight = weight.toFloatOrNull() ?: 0f,
                        height = height.toFloatOrNull() ?: 0f,
                        bmi = bmi.toFloatOrNull() ?: 0f,
                        professionalAnalysis = professionalAnalysis
                    )
                    viewModel.saveAssessment(newAssessment) {
                        navController.navigateUp()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Salvar Avaliação")
            }

            if (patient != null) {
                OutlinedButton(
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        val assessment = Assessment(
                            patientId = patientId,
                            weight = weight.toFloatOrNull() ?: 0f,
                            height = height.toFloatOrNull() ?: 0f,
                            bmi = bmi.toFloatOrNull() ?: 0f,
                            professionalAnalysis = professionalAnalysis
                        )
                        PdfService.generateAndSharePdf(context, patient, assessment)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("🖨️ Salvar e Compartilhar PDF")
                }
            }
        }
    }
}

private fun calculateBMI(weightStr: String, heightStr: String): String {
    val weight = weightStr.toFloatOrNull()
    val height = heightStr.toFloatOrNull()
    return if (weight != null && height != null && height > 0) {
        String.format(java.util.Locale.US, "%.1f", weight / (height * height))
    } else {
        ""
    }
}
