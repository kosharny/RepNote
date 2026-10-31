package com.example.repnote.data.repository

import com.example.repnote.data.local.dao.WorkoutSessionDao
import com.example.repnote.data.local.entity.WorkoutDraftsEntity
import com.example.repnote.data.local.mapper.toCompletedExercise
import com.example.repnote.data.local.mapper.toExerciseEntities
import com.example.repnote.data.local.mapper.toSessionEntity
import com.example.repnote.data.local.mapper.toSetEntities
import com.example.repnote.data.local.mapper.toWorkoutSession
import com.example.repnote.model.PreviousExerciseResult
import com.example.repnote.model.WorkoutSession
import com.example.repnote.model.WorkoutSessionSummary
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class WorkoutSessionRepository(
    private val sessionDao: WorkoutSessionDao
) {
    suspend fun saveSession(
        session: WorkoutSession,
        draftsJson: String
    ) {
        sessionDao.insertWorkoutAndDrafts(
            session = toSessionEntity(session),
            exercises = toExerciseEntities(session),
            sets = toSetEntities(session),
            drafts = WorkoutDraftsEntity(json = draftsJson)
        )
    }

    suspend fun getSessionById(
        sessionId: String
    ): WorkoutSession? {
        val data = sessionDao.getWorkoutDataById(sessionId) ?: return null


        val exercises = data.exercises.map { exercise ->
            val exerciseSets = data.sets.filter { set ->
                set.exerciseId == exercise.exerciseId
            }
            toCompletedExercise(
                entity = exercise,
                sets = exerciseSets
            )
        }

        return toWorkoutSession(data.session, exercises)
    }

    suspend fun deleteSession(sessionId: String) {
        sessionDao.deleteSession(sessionId = sessionId)
    }

    suspend fun getPreviousExerciseResult(
        exerciseId: Long
    ): PreviousExerciseResult? {

        val data = sessionDao.getLatestWorkoutDataForExercise(exerciseId)
            ?: return null

        val exercise = data.exercises.firstOrNull {
            it.exerciseId == exerciseId
        } ?: return null

        val sets = data.sets.filter {
            it.exerciseId == exercise.exerciseId
        }

        val completedExercise = toCompletedExercise(
            exercise,
            sets
        )

        return PreviousExerciseResult(
            sessionId = data.session.id,
            completedAtEpochMillis = data.session.completedAtEpochMillis,
            timeZoneId = data.session.timeZoneId,
            exercise = completedExercise
        )
    }

    suspend fun resetAllData() = sessionDao.resetAllData()

    suspend fun loadDraftsJson(): String? {
        return sessionDao.getDrafts()?.json
    }

    suspend fun saveDraftsJson(json: String) {
        sessionDao.upsertDrafts(
            WorkoutDraftsEntity(json = json)
        )
    }

    fun observeSessions(): Flow<List<WorkoutSessionSummary>> {

        return sessionDao.observeSessions().map { entities ->
            entities.map { entity ->
                WorkoutSessionSummary(
                    id = entity.id,
                    workoutTitle = entity.workoutTitle,
                    completedAtEpochMillis = entity.completedAtEpochMillis,
                    timeZoneId = entity.timeZoneId,
                    templateId = entity.templateId,
                )
            }
        }
    }
}
