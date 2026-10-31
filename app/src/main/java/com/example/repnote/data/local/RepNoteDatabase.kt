package com.example.repnote.data.local

import androidx.room3.AutoMigration
import androidx.room3.Database
import androidx.room3.RoomDatabase
import com.example.repnote.data.local.dao.WorkoutSessionDao
import com.example.repnote.data.local.dao.WorkoutTemplateDao
import com.example.repnote.data.local.entity.ExerciseEntity
import com.example.repnote.data.local.entity.SessionExerciseEntity
import com.example.repnote.data.local.entity.SessionSetEntity
import com.example.repnote.data.local.entity.WorkoutDraftsEntity
import com.example.repnote.data.local.entity.WorkoutSessionEntity
import com.example.repnote.data.local.entity.WorkoutTemplateEntity
import com.example.repnote.data.local.entity.WorkoutTemplateExerciseEntity

@Database(
    entities = [
        WorkoutSessionEntity::class,
        SessionExerciseEntity::class,
        SessionSetEntity::class,
        WorkoutTemplateEntity::class,
        ExerciseEntity::class,
        WorkoutTemplateExerciseEntity::class,
        WorkoutDraftsEntity::class
    ],
    version = 6,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
        AutoMigration(from = 4, to = 5),
        AutoMigration(from = 5, to = 6)
    ]
)
abstract class RepNoteDatabase : RoomDatabase() {
    abstract fun workoutSessionDao(): WorkoutSessionDao
    abstract fun workoutTemplateDao(): WorkoutTemplateDao
}

