package com.example.repnote.model

data class WorkoutTemplate(
    val id: Long,
    val title: String,
    val exercises: List<Exercise>,
    val lastWorkoutText: String,
    val lastCompletedAtEpochMillis: Long? = null,
    val lastTimeZoneId: String = "UTC",
    val sourcePlanKey: String? = null,
    val scheduledDayOfWeek: Int? = null,
    val planPosition: Int = 0,
    val initialSetCounts: Map<Long, Int> = emptyMap()
) {
    val exerciseCount: Int
        get() = exercises.size
}
