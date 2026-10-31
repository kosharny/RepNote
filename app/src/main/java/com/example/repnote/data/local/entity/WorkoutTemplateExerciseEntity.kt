package com.example.repnote.data.local.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.ColumnInfo

@Entity(
    tableName = "workout_template_exercises",
    primaryKeys = ["templateId", "exerciseId"],
    foreignKeys = [
        ForeignKey(
            entity = WorkoutTemplateEntity::class,
            parentColumns = ["id"],
            childColumns = ["templateId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ExerciseEntity::class,
            parentColumns = ["id"],
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [
        Index(value = ["exerciseId"])
    ]
)
data class WorkoutTemplateExerciseEntity(
    val templateId: Long,
    val exerciseId: Long,
    val position: Int,
    @ColumnInfo(defaultValue = "3")
    val initialSetCount: Int = 3,
)