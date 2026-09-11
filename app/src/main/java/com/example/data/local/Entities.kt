package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "patients")
data class Patient(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val birthDate: String, // format dd/MM/yyyy
    val age: Int,
    val phone: String,
    val address: String,
    val neighborhood: String,
    val cityState: String = "Rio das Ostras - RJ",
    val email: String,
    val healthInsurance: String,
    val nextAssessmentDate: String = "", // format dd/MM/yyyy
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "assessments",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class Assessment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Long,
    val evaluationDate: Long = System.currentTimeMillis(),
    
    // Anamnesis
    val labExams: String = "",
    val medications: String = "",
    val allergies: String = "",
    val surgeries: String = "",
    val hasFractures: Boolean = false,
    val fracturesDetails: String = "",
    val hasDislocations: Boolean = false,
    val dislocationsDetails: String = "",
    val hasPregnancy: Boolean = false,
    val pregnancyDetails: String = "",
    val hasAbortion: Boolean = false,
    val abortionDetails: String = "",
    val physicalActivity: String = "",
    val painComplaints: String = "",
    val history: String = "",
    val imagingExams: String = "",

    // Postural Frontal
    val frontalHead: String = "",
    val frontalShoulders: String = "",
    val frontalThalesTriangle: String = "",
    val frontalKnees: String = "",
    val frontalFeet: String = "",

    // Postural Lateral
    val lateralCervical: String = "",
    val lateralShoulders: String = "",
    val lateralAbdomen: String = "",
    val lateralDorsal: String = "",
    val lateralLumbar: String = "",
    val lateralHip: String = "",
    val lateralFeetArch: String = "",

    // Postural Posterior
    val posteriorScapula: String = "",
    val posteriorScoliosis: String = "",
    val posteriorHip: String = "",
    val posteriorGlutealLine: String = "",
    val posteriorPoplitealLine: String = "",

    // Musculature
    val hypertrophyHypotrophy: String = "",
    val muscleLocation: String = "",

    // Bioimpedance
    val weight: Float = 0f,
    val height: Float = 0f,
    val abdominalCirc: Float = 0f,
    val bmi: Float = 0f,
    val corporalAge: Int = 0,
    val metabolicAge: Int = 0,
    val bmr: Float = 0f, // Basal Metabolic Rate
    val bodyFat: Float = 0f,
    val visceralFat: Float = 0f,
    val muscleMass: Float = 0f,
    val idealWeight: Float = 0f,
    val targetWeight: Float = 0f,
    val armFat: Float = 0f,
    val trunkFat: Float = 0f,
    val legFat: Float = 0f,
    val professionalAnalysis: String = ""
)

@Entity(
    tableName = "workouts",
    foreignKeys = [
        ForeignKey(
            entity = Patient::class,
            parentColumns = ["id"],
            childColumns = ["patientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("patientId")]
)
data class Workout(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val patientId: Long,
    val title: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "workout_exercises",
    foreignKeys = [
        ForeignKey(
            entity = Workout::class,
            parentColumns = ["id"],
            childColumns = ["workoutId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("workoutId")]
)
data class WorkoutExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val workoutId: Long,
    val exerciseName: String,
    val setsReps: String,
    val orderIndex: Int
)

@Entity(tableName = "custom_exercises")
data class CustomExercise(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)
