package com.example.repnote.ui.preview

import com.example.repnote.model.Exercise
import com.example.repnote.model.WorkoutTemplate

internal object PreviewData {
    val exercises = listOf(
        Exercise(id = 1L, name = "Жим лежа"),
        Exercise(id = 2L, name = "Тяга верхнего блока"),
        Exercise(id = 3L, name = "Приседания"),
        Exercise(id = 4L, name = "Сгибания рук")
    )

    val workouts = listOf(
        WorkoutTemplate(
            id = 0L,
            title = "Верх тела",
            exercises = exercises.take(2),
            lastWorkoutText = "Последняя тренировка: 27.10.2026 18:30"
        ),
        WorkoutTemplate(
            id = 1L,
            title = "Ноги и корпус",
            exercises = listOf(exercises[2]),
            lastWorkoutText = "Тренировок пока нет"
        ),
        WorkoutTemplate(
            id = 2L,
            title = "Руки",
            exercises = listOf(exercises[3]),
            lastWorkoutText = "Последняя тренировка: 26.10.2026 19:00"
        )
    )
}
