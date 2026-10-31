package com.example.repnote.data.repository

import com.example.repnote.data.local.dao.WorkoutTemplateDao
import com.example.repnote.data.local.entity.ExerciseEntity
import com.example.repnote.model.WorkoutPlanImport
import com.example.repnote.model.Exercise
import com.example.repnote.model.WorkoutTemplate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class WorkoutTemplateRepository(
    private val dao: WorkoutTemplateDao
) {
    fun observeTemplates(): Flow<List<WorkoutTemplate>> {
        return flow {

            emitAll(
                dao.observeTemplateRows().map { rows ->
                    val groupedRows = rows.groupBy { row ->
                        row.templateId
                    }

                    val templates = groupedRows.values.map { templateRows ->
                        val firstRow = templateRows.first()

                        val exercises = templateRows
                            .sortedBy { row ->
                                row.position
                            }
                            .map { row ->
                                Exercise(
                                    id = row.exerciseId,
                                    name = row.exerciseName,
                                    note = row.exerciseNote
                                )
                            }

                        WorkoutTemplate(
                            firstRow.templateId,
                            firstRow.title,
                            exercises,
                            lastWorkoutText = "",
                            sourcePlanKey = firstRow.sourcePlanKey,
                            scheduledDayOfWeek = firstRow.scheduledDayOfWeek,
                            planPosition = firstRow.planPosition,
                            initialSetCounts = templateRows.associate { row ->
                                row.exerciseId to row.initialSetCount
                            }
                        )
                    }
                    val newestByPlan = templates.filter { it.sourcePlanKey != null }
                        .groupBy { it.sourcePlanKey }.mapValues { (_, group) -> group.maxOf { it.id } }
                    templates.sortedWith(
                        compareByDescending<WorkoutTemplate> { newestByPlan[it.sourcePlanKey] ?: it.id }
                            .thenBy { it.planPosition }
                    )
                }
            )
        }
    }

    fun observeExercises(): Flow<List<Exercise>> {
        return flow {

            emitAll(
                dao.observeExercises().map { entities ->
                    entities.map { entity ->
                        Exercise(
                            id = entity.id,
                            name = entity.name,
                            note = entity.note
                        )
                    }
                }
            )
        }
    }

    suspend fun importPlan(plan: WorkoutPlanImport): Boolean {
        require(plan.key in setOf("full_body", "upper_lower", "push_pull_legs"))
        val expectedCount = if (plan.key == "upper_lower") 4 else 3
        require(plan.workouts.size == expectedCount)
        val days = plan.workouts.map { it.dayOfWeek }
        require(days.all { it in 1..7 } && days.distinct().size == days.size && days == days.sorted())
        require(plan.workouts.all { workout ->
            workout.title.isNotBlank() && workout.exercises.isNotEmpty() &&
                workout.exercises.map { it.key }.distinct().size == workout.exercises.size &&
                workout.exercises.all { it.key.isNotBlank() && it.name.isNotBlank() && it.setCount in 1..10 }
        })
        return dao.importPlan(plan)
    }

    suspend fun createTemplate(
        title: String,
        exerciseIds: List<Long>,
        initialSetCounts: Map<Long, Int>
    ): Long {
        val trimmedTitle = title.trim()

        require(trimmedTitle.isNotEmpty()) {
            "Название тренировки не должно быть пустым"
        }

        require(exerciseIds.isNotEmpty()) {
            "Добавьте хотя бы одно упражнение"
        }

        require(exerciseIds.distinct().size == exerciseIds.size) {
            "Упражнения не должны повторяться"
        }

        require(
            exerciseIds.all { exerciseId ->
                val count = initialSetCounts[exerciseId]
                count != null && count in 1..10
            }
        ) {
            "Для каждого упражнения укажите от 1 до 10 начальных подходов"
        }

        return dao.createTemplate(
            title = trimmedTitle,
            exerciseIds = exerciseIds,
            initialSetCounts = initialSetCounts
        )
    }

    suspend fun addExercise(
        name: String,
        note: String = ""
    ): Long {
        val trimmedName = name.trim()

        require(trimmedName.isNotEmpty()) {
            "Название упражнения не должно быть пустым"
        }

        return dao.insertExercise(
            ExerciseEntity(
                name = trimmedName,
                note = note.trim()
            )
        )
    }

    suspend fun renameTemplate(
        templateId: Long,
        title: String
    ) {
        val trimmedTitle = title.trim()

        require(trimmedTitle.isNotEmpty()) {
            "Название тренировки не должно быть пустым"
        }

        val updatedRows =  dao.renameTemplate(
            templateId = templateId,
            title = trimmedTitle
        )

        check(updatedRows == 1) {
            "Тренировка не найдена"
        }

    }

    suspend fun getTemplateById(
        templateId: Long
    ): WorkoutTemplate? {
        return observeTemplates()
            .first()
            .firstOrNull { template ->
                template.id == templateId
            }
    }

    suspend fun updateTemplate(
        templateId: Long,
        title: String,
        exerciseIds: List<Long>,
        initialSetCounts: Map<Long, Int>
    ) {
        val trimmedTitle = title.trim()

        require(trimmedTitle.isNotEmpty()) {
            "Название тренировки не должно быть пустым"
        }

        require(exerciseIds.isNotEmpty()) {
            "Добавьте хотя бы одно упражнение"
        }

        require(exerciseIds.distinct().size == exerciseIds.size) {
            "Упражнения не должны повторяться"
        }

        require(
            exerciseIds.all { exerciseId ->
                val count = initialSetCounts[exerciseId]
                count != null && count in 1..10
            }
        ) {
            "Для каждого упражнения укажите от 1 до 10 начальных подходов"
        }

        dao.updateTemplate(
            templateId = templateId,
            title = trimmedTitle,
            exerciseIds = exerciseIds,
            initialSetCounts = initialSetCounts
        )
    }

    suspend fun deleteTemplate(templateId: Long) {
        dao.deleteTemplate(templateId)
    }

    suspend fun updateExercise(
        exerciseId: Long,
        name: String,
        note: String
    ) {
        val trimmedName = name.trim()

        require(trimmedName.isNotEmpty()) {
            "Название упражнения не должно быть пустым"
        }

        val updatedRows = dao.updateExercise(
            exerciseId = exerciseId,
            name = trimmedName,
            note = note.trim()
        )

        check(updatedRows == 1) {
            "Упражнение не найдено"
        }
    }

    suspend fun deleteUnusedExercise(exerciseId: Long): Boolean {
        return dao.deleteUnusedExercise(exerciseId)
    }
}