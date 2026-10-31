package com.example.repnote.data.local.model

data class WorkoutTemplateRow(
    val templateId: Long,
    val title: String,
    val exerciseId: Long,
    val exerciseName: String,
    val position: Int,
    val exerciseNote: String = "",
    val sourcePlanKey: String? = null,
    val scheduledDayOfWeek: Int? = null,
    val planPosition: Int = 0,
    val initialSetCount: Int = 3,
)