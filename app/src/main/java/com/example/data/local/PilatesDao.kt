package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface PilatesDao {
    // Patients
    @Query("SELECT * FROM patients ORDER BY name ASC")
    fun getAllPatients(): Flow<List<Patient>>

    @Query("SELECT * FROM patients WHERE id = :id")
    fun getPatientById(id: Long): Flow<Patient?>

    @Query("SELECT * FROM patients WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchPatients(query: String): Flow<List<Patient>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: Patient): Long

    @Update
    suspend fun updatePatient(patient: Patient)

    @Delete
    suspend fun deletePatient(patient: Patient)

    // Assessments
    @Query("SELECT * FROM assessments WHERE patientId = :patientId ORDER BY evaluationDate DESC")
    fun getAssessmentsForPatient(patientId: Long): Flow<List<Assessment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssessment(assessment: Assessment): Long

    @Update
    suspend fun updateAssessment(assessment: Assessment)

    // Workouts
    @Query("SELECT * FROM workouts WHERE patientId = :patientId ORDER BY createdAt DESC")
    fun getWorkoutsForPatient(patientId: Long): Flow<List<Workout>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: Workout): Long

    @Update
    suspend fun updateWorkout(workout: Workout)
    
    @Delete
    suspend fun deleteWorkout(workout: Workout)

    // Workout Exercises
    @Query("SELECT * FROM workout_exercises WHERE workoutId = :workoutId ORDER BY orderIndex ASC")
    fun getExercisesForWorkout(workoutId: Long): Flow<List<WorkoutExercise>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercise(exercise: WorkoutExercise)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutExercises(exercises: List<WorkoutExercise>)

    @Query("DELETE FROM workout_exercises WHERE workoutId = :workoutId")
    suspend fun deleteExercisesForWorkout(workoutId: Long)

    // Custom Exercises
    @Query("SELECT * FROM custom_exercises ORDER BY name ASC")
    fun getAllCustomExercises(): Flow<List<CustomExercise>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCustomExercise(exercise: CustomExercise)

    @Delete
    suspend fun deleteCustomExercise(exercise: CustomExercise)
}
