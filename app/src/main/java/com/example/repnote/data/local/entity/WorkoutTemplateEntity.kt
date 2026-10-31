package com.example.repnote.data.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey


@Entity(
    tableName = "workout_templates",
)
data class WorkoutTemplateEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    @ColumnInfo(defaultValue = "NULL")
    val sourcePlanKey: String? = null,
    @ColumnInfo(defaultValue = "NULL")
    val scheduledDayOfWeek: Int? = null,
    @ColumnInfo(defaultValue = "0")
    val planPosition: Int = 0
)