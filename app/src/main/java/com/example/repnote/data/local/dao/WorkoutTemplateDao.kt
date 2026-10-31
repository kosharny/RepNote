package com.example.repnote.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import com.example.repnote.model.WorkoutPlanImport
import com.example.repnote.data.local.entity.ExerciseEntity
import com.example.repnote.data.local.entity.WorkoutTemplateEntity
import com.example.repnote.data.local.entity.WorkoutTemplateExerciseEntity
import com.example.repnote.data.local.model.WorkoutTemplateRow
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutTemplateDao {
    @Insert
    suspend fun insertTemplate(
        entity: WorkoutTemplateEntity
    ): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertExercise(
        entity: ExerciseEntity
    ): Long

    @Insert
    suspend fun insertLinks(
        links: List<WorkoutTemplateExerciseEntity>
    )

    @Query("SELECT COUNT(*) FROM workout_templates WHERE sourcePlanKey = :key")
    suspend fun countPlanWorkouts(key: String): Int

    @Query("SELECT id FROM exercises WHERE builtinKey = :key ORDER BY id LIMIT 1")
    suspend fun findBuiltinExercise(key: String): Long?

    @Query("SELECT id FROM exercises WHERE name = :name COLLATE NOCASE ORDER BY id LIMIT 1")
    suspend fun findExerciseByName(name: String): Long?

    @Query("UPDATE exercises SET builtinKey = :key WHERE id = :id AND builtinKey IS NULL")
    suspend fun identifyExercise(id: Long, key: String)

    @Transaction
    suspend fun importPlan(plan: WorkoutPlanImport): Boolean {
        if (countPlanWorkouts(plan.key) > 0) return false
        plan.workouts.forEachIndexed { index, workout ->
            val templateId = insertTemplate(WorkoutTemplateEntity(
                title = workout.title, sourcePlanKey = plan.key,
                scheduledDayOfWeek = workout.dayOfWeek, planPosition = index
            ))
            val links = workout.exercises.mapIndexed { position, exercise ->
                val exerciseId = findBuiltinExercise(exercise.key)
                    ?: findExerciseByName(exercise.name)?.also { identifyExercise(it, exercise.key) }
                    ?: insertExercise(ExerciseEntity(name = exercise.name, note = exercise.note, builtinKey = exercise.key))
                check(exerciseId > 0)
                WorkoutTemplateExerciseEntity(templateId, exerciseId, position, exercise.setCount)
            }
            insertLinks(links)
        }
        return true
    }

    @Query("SELECT * FROM exercises ORDER BY name ASC, id ASC")
    fun observeExercises(): Flow<List<ExerciseEntity>>


    @Transaction
    suspend fun createTemplate(
        title: String,
        exerciseIds: List<Long>,
        initialSetCounts: Map<Long, Int>
    ): Long {
        val templateId = insertTemplate(
            WorkoutTemplateEntity(title = title)
        )

        val links = exerciseIds.mapIndexed { index, exerciseId ->
            WorkoutTemplateExerciseEntity(
                templateId = templateId,
                exerciseId = exerciseId,
                position = index,
                initialSetCount = initialSetCounts.getValue(exerciseId)
            )
        }

        insertLinks(links)
        return templateId
    }

    @Transaction
    suspend fun updateTemplate(
        templateId: Long,
        title: String,
        exerciseIds: List<Long>,
        initialSetCounts: Map<Long, Int>
    ) {
        val updatedRows = renameTemplate(
            templateId = templateId,
            title = title
        )

        check(updatedRows == 1) {
            "Тренировка не найдена"
        }

        deleteTemplateLinks(templateId)

        val links = exerciseIds.mapIndexed { index, exerciseId ->
            WorkoutTemplateExerciseEntity(
                templateId = templateId,
                exerciseId = exerciseId,
                position = index,
                initialSetCount = initialSetCounts.getValue(exerciseId)
            )
        }

        insertLinks(links)
    }

    @Query("""
    SELECT t.id AS templateId, t.title, t.sourcePlanKey, t.scheduledDayOfWeek, t.planPosition,
       e.id AS exerciseId, e.name AS exerciseName,
       e.note AS exerciseNote,
       link.position,
        link.initialSetCount
    FROM workout_templates AS t
    INNER JOIN workout_template_exercises AS link
        ON link.templateId = t.id
    INNER JOIN exercises AS e
        ON e.id = link.exerciseId
    ORDER BY t.id DESC, link.position ASC
""")
    fun observeTemplateRows(): Flow<List<WorkoutTemplateRow>>

    @Query("""
    UPDATE workout_templates
    SET title = :title
    WHERE id = :templateId
""")
    suspend fun renameTemplate(
        templateId: Long,
        title: String
    ): Int

    @Query(
        """
    DELETE FROM workout_template_exercises
    WHERE templateId = :templateId
    """
    )
    suspend fun deleteTemplateLinks(templateId: Long)

    @Query(
        """
    DELETE FROM workout_templates
    WHERE id = :templateId
    """
    )
    suspend fun deleteTemplate(templateId: Long): Int

    @Query(
        """
    UPDATE exercises
    SET name = :name, note = :note
    WHERE id = :exerciseId
    """
    )
    suspend fun updateExercise(
        exerciseId: Long,
        name: String,
        note: String
    ): Int

    @Query(
        """
    SELECT COUNT(*)
    FROM workout_template_exercises
    WHERE exerciseId = :exerciseId
    """
    )
    suspend fun getExerciseUsageCount(exerciseId: Long): Int

    @Query("DELETE FROM exercises WHERE id = :exerciseId")
    suspend fun deleteExerciseById(exerciseId: Long): Int

    @Transaction
    suspend fun deleteUnusedExercise(exerciseId: Long): Boolean {
        if (getExerciseUsageCount(exerciseId) > 0) {
            return false
        }

        deleteExerciseById(exerciseId)
        return true
    }
}