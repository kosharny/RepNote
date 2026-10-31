package com.example.repnote.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable


@Serializable
data class WorkoutResultDestination(
    val sessionId: String
) : NavKey

@Serializable
data class ExerciseEntryDestination(
    val workoutId: Long,
    val exerciseId: Long
) : NavKey

@Serializable
data object WorkoutsDestination : NavKey

@Serializable
data object WorkoutPlansDestination : NavKey

@Serializable
data class WorkoutPlanDetailsDestination(val planKey: String) : NavKey

@Serializable
data class WorkoutDetailsDestination(
    val workoutId: Long
) : NavKey


@Serializable
data object WorkoutHistoryDestination : NavKey