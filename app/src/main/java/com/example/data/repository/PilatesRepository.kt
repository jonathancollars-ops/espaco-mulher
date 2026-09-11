package com.example.data.repository

import com.example.data.local.Assessment
import com.example.data.local.CustomExercise
import com.example.data.local.Patient
import com.example.data.local.PilatesDao
import com.example.data.local.Workout
import com.example.data.local.WorkoutExercise
import kotlinx.coroutines.flow.Flow

class PilatesRepository(private val dao: PilatesDao) {

    val allPatients: Flow<List<Patient>> = dao.getAllPatients()
    
    fun searchPatients(query: String): Flow<List<Patient>> = dao.searchPatients(query)
    
    fun getPatientById(id: Long): Flow<Patient?> = dao.getPatientById(id)

    suspend fun insertPatient(patient: Patient): Long = dao.insertPatient(patient)
    suspend fun updatePatient(patient: Patient) = dao.updatePatient(patient)
    suspend fun deletePatient(patient: Patient) = dao.deletePatient(patient)

    fun getAssessmentsForPatient(patientId: Long): Flow<List<Assessment>> = dao.getAssessmentsForPatient(patientId)
    suspend fun insertAssessment(assessment: Assessment): Long = dao.insertAssessment(assessment)
    suspend fun updateAssessment(assessment: Assessment) = dao.updateAssessment(assessment)

    fun getWorkoutsForPatient(patientId: Long): Flow<List<Workout>> = dao.getWorkoutsForPatient(patientId)
    suspend fun insertWorkout(workout: Workout): Long = dao.insertWorkout(workout)
    suspend fun updateWorkout(workout: Workout) = dao.updateWorkout(workout)
    suspend fun deleteWorkout(workout: Workout) = dao.deleteWorkout(workout)

    fun getExercisesForWorkout(workoutId: Long): Flow<List<WorkoutExercise>> = dao.getExercisesForWorkout(workoutId)
    suspend fun insertWorkoutExercises(exercises: List<WorkoutExercise>) = dao.insertWorkoutExercises(exercises)
    suspend fun deleteExercisesForWorkout(workoutId: Long) = dao.deleteExercisesForWorkout(workoutId)

    val allCustomExercises: Flow<List<CustomExercise>> = dao.getAllCustomExercises()
    suspend fun insertCustomExercise(exercise: CustomExercise) = dao.insertCustomExercise(exercise)
    suspend fun deleteCustomExercise(exercise: CustomExercise) = dao.deleteCustomExercise(exercise)
}
