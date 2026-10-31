package com.example.repnote.ui.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class EditWorkoutDestination(
    val workoutId: Long
) : NavKey