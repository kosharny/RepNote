package com.example.repnote.data.local.dao

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import androidx.room3.Upsert
import com.example.repnote.data.local.entity.SessionExerciseEntity
import com.example.repnote.data.local.entity.SessionSetEntity
import com.example.repnote.data.local.entity.WorkoutDraftsEntity
import com.example.repnote.data.local.entity.WorkoutSessionEntity
import com.example.repnote.data.local.model.WorkoutSessionData
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutSessionDao {
    @Query("DELETE FROM workout_sessions")
    suspend fun deleteAllSessions()

    @Query("DELETE FROM workout_template_exercises")
    suspend fun deleteAllLinks()

    @Query("DELETE FROM workout_templates")
    suspend fun deleteAllTemplates()

    @Query("DELETE FROM exercises")
    suspend fun deleteAllExercises()

    @Transaction
    suspend fun resetAllData() {
        deleteAllSessions()
        deleteAllLinks()
        deleteAllTemplates()
        deleteAllExercises()
        upsertDrafts(WorkoutDraftsEntity(json = "{}"))
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSession(session: WorkoutSessionEntity)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertExercises(
        exercises: List<SessionExerciseEntity>
    )
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSets(
        sets: List<SessionSetEntity>
    )

    @Transaction
    suspend fun insertWorkout(
        session: WorkoutSessionEntity,
        exercises: List<SessionExerciseEntity>,
        sets: List<SessionSetEntity>
    ) {
        insertSession(session)
        insertExercises(exercises)
        insertSets(sets)
    }

    @Transaction
    suspend fun getWorkoutDataById(
        sessionId: String
    ): WorkoutSessionData? {

        val session = getSessionById(sessionId) ?: return null
        val exercises = getExercisesForSession(sessionId)
        val sets = exercises.flatMap { exercise ->
            getSetsForExercise(exercise.sessionId, exercise.exerciseId)
        }

        return WorkoutSessionData(
            session = session,
            exercises = exercises,
            sets = sets,
        )
    }

    @Query("SELECT * FROM workout_sessions WHERE id = :sessionId LIMIT 1")
    suspend fun getSessionById(sessionId: String): WorkoutSessionEntity?

    @Query("""
    SELECT * FROM session_exercises
    WHERE sessionId = :sessionId
    ORDER BY position ASC
    """)
    suspend fun getExercisesForSession(
        sessionId: String
    ): List<SessionExerciseEntity>

    @Query("""
    SELECT * FROM session_sets
    WHERE sessionId = :sessionId AND exerciseId = :exerciseId
    ORDER BY position ASC
    """)
    suspend fun getSetsForExercise(
        sessionId: String,
        exerciseId: Long
    ): List<SessionSetEntity>

    @Query("""
    SELECT * FROM workout_sessions
    ORDER BY completedAtEpochMillis DESC, id ASC
""")
    fun observeSessions(): Flow<List<WorkoutSessionEntity>>

    @Query("DELETE FROM workout_sessions WHERE id = :sessionId")
    suspend fun deleteSession(sessionId: String): Int

    @Query("""
    SELECT s.id
    FROM workout_sessions AS s
    INNER JOIN session_exercises AS e ON e.sessionId = s.id
    WHERE e.exerciseId = :exerciseId
    ORDER BY s.completedAtEpochMillis DESC, s.id ASC
    LIMIT 1
""")
    suspend fun findLatestSessionIdForExercise(
        exerciseId: Long
    ): String?

    @Transaction
    suspend fun getLatestWorkoutDataForExercise(
        exerciseId: Long
    ): WorkoutSessionData? {
        val latestSessionId = findLatestSessionIdForExercise(exerciseId) ?: return null
        return getWorkoutDataById(latestSessionId)
    }

    @Query("SELECT * FROM workout_drafts WHERE id = 1")
    suspend fun getDrafts(): WorkoutDraftsEntity?

    @Upsert
    suspend fun upsertDrafts(entity: WorkoutDraftsEntity)

    @Transaction
    suspend fun insertWorkoutAndDrafts(
        session: WorkoutSessionEntity,
        exercises: List<SessionExerciseEntity>,
        sets: List<SessionSetEntity>,
        drafts: WorkoutDraftsEntity
    ) {
        insertWorkout(
            session = session,
            exercises = exercises,
            sets = sets
        )

        upsertDrafts(drafts)
    }
}
