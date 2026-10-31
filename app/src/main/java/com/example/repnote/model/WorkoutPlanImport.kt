package com.example.repnote.model

data class WorkoutPlanImport(val key: String, val workouts: List<PlannedWorkoutImport>)

data class PlannedWorkoutImport(
    val title: String,
    val dayOfWeek: Int,
    val exercises: List<PlannedExerciseImport>
)

data class PlannedExerciseImport(
    val key: String,
    val name: String,
    val note: String,
    val setCount: Int
)
