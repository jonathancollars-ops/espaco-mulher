package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.data.local.Patient
import com.example.utils.ReminderManager

@Composable
fun InsetTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isLast: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun PatientFormScreen(navController: NavController, viewModel: PilatesViewModel, patientId: Long?) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var neighborhood by remember { mutableStateOf("") }
    var cityState by remember { mutableStateOf("Rio das Ostras - RJ") }
    var email by remember { mutableStateOf("") }
    var healthInsurance by remember { mutableStateOf("") }
    var nextAssessmentDate by remember { mutableStateOf("") }
    
    val patientState = if (patientId != null) viewModel.getPatientById(patientId).collectAsState(initial = null) else null
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current

    LaunchedEffect(patientState?.value) {
        patientState?.value?.let { p ->
            name = p.name
            birthDate = p.birthDate
            age = p.age.toString()
            phone = p.phone
            address = p.address
            neighborhood = p.neighborhood
            cityState = p.cityState
            email = p.email
            healthInsurance = p.healthInsurance
            nextAssessmentDate = p.nextAssessmentDate
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                InsetTextField(value = name, onValueChange = { name = it }, label = "Nome Completo")
                Row {
                    InsetTextField(value = birthDate, onValueChange = { birthDate = it }, label = "Data de Nasc.", modifier = Modifier.weight(1f))
                    InsetTextField(value = age, onValueChange = { age = it }, label = "Idade", modifier = Modifier.weight(1f))
                }
                InsetTextField(value = phone, onValueChange = { phone = it }, label = "Telefone")
                InsetTextField(value = email, onValueChange = { email = it }, label = "E-mail")
            }
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                InsetTextField(value = address, onValueChange = { address = it }, label = "Endereço")
                InsetTextField(value = neighborhood, onValueChange = { neighborhood = it }, label = "Bairro")
                InsetTextField(value = cityState, onValueChange = { cityState = it }, label = "Cidade/Estado")
                InsetTextField(value = healthInsurance, onValueChange = { healthInsurance = it }, label = "Convênio (Opcional)")
                InsetTextField(value = nextAssessmentDate, onValueChange = { nextAssessmentDate = it }, label = "Agendamento / Reavaliação (dd/mm/aaaa)", isLast = true)
            }
        }

        Button(
            onClick = {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                val newPatient = Patient(
                    id = patientId ?: 0L,
                    name = name,
                    birthDate = birthDate,
                    age = age.toIntOrNull() ?: 0,
                    phone = phone,
                    address = address,
                    neighborhood = neighborhood,
                    cityState = cityState,
                    email = email,
                    healthInsurance = healthInsurance,
                    nextAssessmentDate = nextAssessmentDate
                )
                viewModel.savePatient(newPatient) { savedId ->
                    if (nextAssessmentDate.isNotBlank() && name.isNotBlank()) {
                        ReminderManager.scheduleReminder(context, savedId, name, nextAssessmentDate)
                    }
                    navController.navigateUp()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Salvar Paciente", modifier = Modifier.padding(8.dp))
        }
    }
}
