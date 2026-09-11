package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.local.Assessment
import com.example.data.local.CustomExercise
import com.example.data.local.Patient
import com.example.data.local.Workout
import com.example.data.local.WorkoutExercise
import com.example.data.repository.PilatesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PilatesViewModel(private val repository: PilatesRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val patients: StateFlow<List<Patient>> = _searchQuery.flatMapLatest { query ->
        if (query.isBlank()) {
            repository.allPatients
        } else {
            repository.searchPatients(query)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    // Patient
    fun getPatientById(id: Long): StateFlow<Patient?> {
        return repository.getPatientById(id).stateIn(viewModelScope, SharingStarted.Lazily, null)
    }

    fun savePatient(patient: Patient, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val savedId = if (patient.id == 0L) {
                repository.insertPatient(patient)
            } else {
                repository.updatePatient(patient)
                patient.id
            }
            onComplete(savedId)
        }
    }
    
    fun deletePatient(patient: Patient) {
        viewModelScope.launch {
            repository.deletePatient(patient)
        }
    }

    // Assessment
    fun getAssessmentsForPatient(patientId: Long): StateFlow<List<Assessment>> {
        return repository.getAssessmentsForPatient(patientId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun saveAssessment(assessment: Assessment, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            if (assessment.id == 0L) {
                repository.insertAssessment(assessment)
            } else {
                repository.updateAssessment(assessment)
            }
            onComplete()
        }
    }

    // Workouts
    fun getWorkoutsForPatient(patientId: Long): StateFlow<List<Workout>> {
        return repository.getWorkoutsForPatient(patientId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }
    
    fun getExercisesForWorkout(workoutId: Long): StateFlow<List<WorkoutExercise>> {
        return repository.getExercisesForWorkout(workoutId)
            .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    }

    fun saveWorkoutWithExercises(workout: Workout, exercises: List<WorkoutExercise>, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            val workoutId = if (workout.id == 0L) {
                repository.insertWorkout(workout)
            } else {
                repository.updateWorkout(workout)
                repository.deleteExercisesForWorkout(workout.id)
                workout.id
            }
            
            val exercisesWithId = exercises.map { it.copy(workoutId = workoutId, id = 0) }
            repository.insertWorkoutExercises(exercisesWithId)
            onComplete()
        }
    }
    
    // Custom Exercises
    val customExercises = repository.allCustomExercises
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
        
    fun addCustomExercise(name: String) {
        viewModelScope.launch {
            repository.insertCustomExercise(CustomExercise(name = name))
        }
    }

    fun deleteCustomExercise(exercise: CustomExercise) {
        viewModelScope.launch {
            repository.deleteCustomExercise(exercise)
        }
    }
}

class PilatesViewModelFactory(private val repository: PilatesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PilatesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PilatesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
