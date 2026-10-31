package com.example.repnote

import android.app.Application
import androidx.room3.Room
import androidx.sqlite.driver.AndroidSQLiteDriver
import com.example.repnote.data.local.RepNoteDatabase
import com.example.repnote.data.repository.SettingsRepository
import com.example.repnote.data.repository.WorkoutSessionRepository
import com.example.repnote.data.repository.WorkoutTemplateRepository

class RepNoteApplication : Application() {
    val database: RepNoteDatabase by lazy {
        Room.databaseBuilder<RepNoteDatabase>(
            this,
            "repnote.db"
        )
            .setDriver(AndroidSQLiteDriver())
            .build()
    }

    val settingsRepository: SettingsRepository by lazy {
        SettingsRepository(
            context = applicationContext
        )
    }

    val sessionRepository: WorkoutSessionRepository by lazy {
        WorkoutSessionRepository(
            sessionDao = database.workoutSessionDao()
        )
    }

    val workoutTemplateRepository: WorkoutTemplateRepository by lazy {
        WorkoutTemplateRepository(
            dao = database.workoutTemplateDao()
        )
    }
}