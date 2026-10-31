package com.example.repnote.data.local.entity

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey

@Entity(tableName = "exercises")
data class ExerciseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    @ColumnInfo(defaultValue = "''")
    val note: String = "",
    @ColumnInfo(defaultValue = "NULL")
    val builtinKey: String? = null
)