package com.example.repnote.data.local.mapper

import com.example.repnote.data.local.entity.SessionExerciseEntity
import com.example.repnote.data.local.entity.SessionSetEntity
import com.example.repnote.data.local.entity.WorkoutSessionEntity
import com.example.repnote.model.CompletedSet
import com.example.repnote.model.CompletedExercise
import com.example.repnote.model.WorkoutSession

fun toSessionEntity(
    session: WorkoutSession
): WorkoutSessionEntity {
   return  WorkoutSessionEntity(
        id = session.id,
        templateId = session.templateId,
        workoutTitle = session.workoutTitle,
        completedAtEpochMillis = session.completedAtEpochMillis,
        timeZoneId = session.timeZoneId,
    )
}

fun toExerciseEntities(
    session: WorkoutSession
): List<SessionExerciseEntity> {
    return session.exercises.mapIndexed { index, exercise ->
        SessionExerciseEntity(
            sessionId = session.id,
            exerciseId = exercise.exerciseId,
            exerciseName = exercise.exerciseName,
            position = index
        )
    }
}

fun toSetEntities(
    session: WorkoutSession
): List<SessionSetEntity> {
   return session.exercises.flatMap { exercise ->
        exercise.sets.mapIndexed { index, completedSet ->
            SessionSetEntity(
                sessionId = session.id,
                exerciseId = exercise.exerciseId,
                position = index,
                weightKg = completedSet.weightKg,
                reps = completedSet.reps
            )
        }
    }
}

fun toCompletedSet(
    entity: SessionSetEntity
): CompletedSet {
    return CompletedSet(
        entity.weightKg,
        entity.reps
    )
}

fun toCompletedExercise(
    entity: SessionExerciseEntity,
    sets: List<SessionSetEntity>
): CompletedExercise {
    return CompletedExercise(
        entity.exerciseId,
        entity.exerciseName,
        sets.map { setEntity ->
            toCompletedSet(setEntity)
        }
    )
}

fun toWorkoutSession(
    entity: WorkoutSessionEntity,
    exercises: List<CompletedExercise>
): WorkoutSession {
    return WorkoutSession(
        entity.id,
        entity.templateId,
        entity.workoutTitle,
        entity.completedAtEpochMillis,
        entity.timeZoneId,
        exercises
    )
}
