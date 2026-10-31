package com.example.repnote.data.plans

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.repnote.R
import com.example.repnote.model.PlannedExerciseImport
import com.example.repnote.model.PlannedWorkoutImport
import com.example.repnote.model.WorkoutPlanImport

data class WorkoutPlanDefinition(
    val key: String,
    @param:StringRes val titleRes: Int,
    @param:StringRes val descriptionRes: Int,
    @param:DrawableRes val artworkRes: Int,
    val defaultDays: List<Int>,
    val workouts: List<PlanWorkoutDefinition>
)

data class PlanWorkoutDefinition(
    @param:StringRes val titleRes: Int,
    val exercises: List<PlanExerciseDefinition>
)

data class PlanExerciseDefinition(
    val key: String,
    @param:StringRes val nameRes: Int,
    val sets: Int,
    @param:StringRes val repsRes: Int
)

object WorkoutPlanCatalog {
    val plans: List<WorkoutPlanDefinition> = listOf(
        WorkoutPlanDefinition("full_body", R.string.plan_full_body, R.string.plan_full_body_description,
            R.drawable.repnote_plan_full_body, listOf(1, 3, 5), listOf(
                PlanWorkoutDefinition(R.string.plan_workout_full_body_a, listOf(
                    PlanExerciseDefinition("squat", R.string.exercise_squat, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("bench_press", R.string.exercise_bench_press, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("lat_pulldown", R.string.exercise_lat_pulldown, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("romanian_deadlift", R.string.exercise_romanian_deadlift, 2, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("cable_crunch", R.string.exercise_cable_crunch, 2, R.string.plan_reps_12_15)
                )),
                PlanWorkoutDefinition(R.string.plan_workout_full_body_b, listOf(
                    PlanExerciseDefinition("leg_press", R.string.exercise_leg_press, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("incline_dumbbell_press", R.string.exercise_incline_dumbbell_press, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("seated_row", R.string.exercise_seated_row, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("seated_leg_curl", R.string.exercise_seated_leg_curl, 2, R.string.plan_reps_10_15),
                    PlanExerciseDefinition("lateral_raise", R.string.exercise_lateral_raise, 2, R.string.plan_reps_12_15)
                )),
                PlanWorkoutDefinition(R.string.plan_workout_full_body_c, listOf(
                    PlanExerciseDefinition("goblet_squat", R.string.exercise_goblet_squat, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("shoulder_press", R.string.exercise_shoulder_press, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("chest_supported_row", R.string.exercise_chest_supported_row, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("hip_thrust", R.string.exercise_hip_thrust, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("standing_calf_raise", R.string.exercise_standing_calf_raise, 2, R.string.plan_reps_12_15)
                ))
            )),
        WorkoutPlanDefinition("upper_lower", R.string.plan_upper_lower, R.string.plan_upper_lower_description,
            R.drawable.repnote_plan_upper_lower, listOf(1, 2, 4, 5), listOf(
                PlanWorkoutDefinition(R.string.plan_workout_upper_a, listOf(
                    PlanExerciseDefinition("bench_press", R.string.exercise_bench_press, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("lat_pulldown", R.string.exercise_lat_pulldown, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("shoulder_press", R.string.exercise_shoulder_press, 2, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("seated_row", R.string.exercise_seated_row, 2, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("biceps_curl", R.string.exercise_biceps_curl, 2, R.string.plan_reps_10_15),
                    PlanExerciseDefinition("triceps_pushdown", R.string.exercise_triceps_pushdown, 2, R.string.plan_reps_10_15)
                )),
                PlanWorkoutDefinition(R.string.plan_workout_lower_a, listOf(
                    PlanExerciseDefinition("squat", R.string.exercise_squat, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("romanian_deadlift", R.string.exercise_romanian_deadlift, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("seated_leg_curl", R.string.exercise_seated_leg_curl, 2, R.string.plan_reps_10_15),
                    PlanExerciseDefinition("standing_calf_raise", R.string.exercise_standing_calf_raise, 3, R.string.plan_reps_12_15),
                    PlanExerciseDefinition("cable_crunch", R.string.exercise_cable_crunch, 2, R.string.plan_reps_12_15)
                )),
                PlanWorkoutDefinition(R.string.plan_workout_upper_b, listOf(
                    PlanExerciseDefinition("incline_dumbbell_press", R.string.exercise_incline_dumbbell_press, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("chest_supported_row", R.string.exercise_chest_supported_row, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("lateral_raise", R.string.exercise_lateral_raise, 3, R.string.plan_reps_12_15),
                    PlanExerciseDefinition("face_pull", R.string.exercise_face_pull, 2, R.string.plan_reps_12_15),
                    PlanExerciseDefinition("hammer_curl", R.string.exercise_hammer_curl, 2, R.string.plan_reps_10_15),
                    PlanExerciseDefinition("triceps_pushdown", R.string.exercise_triceps_pushdown, 2, R.string.plan_reps_10_15)
                )),
                PlanWorkoutDefinition(R.string.plan_workout_lower_b, listOf(
                    PlanExerciseDefinition("leg_press", R.string.exercise_leg_press, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("hip_thrust", R.string.exercise_hip_thrust, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("leg_extension", R.string.exercise_leg_extension, 2, R.string.plan_reps_10_15),
                    PlanExerciseDefinition("lying_leg_curl", R.string.exercise_lying_leg_curl, 2, R.string.plan_reps_10_15),
                    PlanExerciseDefinition("seated_calf_raise", R.string.exercise_seated_calf_raise, 3, R.string.plan_reps_12_15)
                ))
            )),
        WorkoutPlanDefinition("push_pull_legs", R.string.plan_push_pull_legs, R.string.plan_push_pull_legs_description,
            R.drawable.repnote_plan_push_pull_legs, listOf(1, 3, 5), listOf(
                PlanWorkoutDefinition(R.string.plan_workout_push, listOf(
                    PlanExerciseDefinition("bench_press", R.string.exercise_bench_press, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("incline_dumbbell_press", R.string.exercise_incline_dumbbell_press, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("shoulder_press", R.string.exercise_shoulder_press, 2, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("lateral_raise", R.string.exercise_lateral_raise, 2, R.string.plan_reps_12_15),
                    PlanExerciseDefinition("triceps_pushdown", R.string.exercise_triceps_pushdown, 2, R.string.plan_reps_10_15)
                )),
                PlanWorkoutDefinition(R.string.plan_workout_pull, listOf(
                    PlanExerciseDefinition("lat_pulldown", R.string.exercise_lat_pulldown, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("seated_row", R.string.exercise_seated_row, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("dumbbell_row", R.string.exercise_dumbbell_row, 2, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("face_pull", R.string.exercise_face_pull, 2, R.string.plan_reps_12_15),
                    PlanExerciseDefinition("biceps_curl", R.string.exercise_biceps_curl, 2, R.string.plan_reps_10_15)
                )),
                PlanWorkoutDefinition(R.string.plan_workout_legs, listOf(
                    PlanExerciseDefinition("squat", R.string.exercise_squat, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("romanian_deadlift", R.string.exercise_romanian_deadlift, 3, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("leg_press", R.string.exercise_leg_press, 2, R.string.plan_reps_8_12),
                    PlanExerciseDefinition("seated_leg_curl", R.string.exercise_seated_leg_curl, 2, R.string.plan_reps_10_15),
                    PlanExerciseDefinition("standing_calf_raise", R.string.exercise_standing_calf_raise, 3, R.string.plan_reps_12_15)
                ))
            ))
    )

    fun find(key: String): WorkoutPlanDefinition? = plans.firstOrNull { it.key == key }
}

fun WorkoutPlanDefinition.resolve(days: List<Int>, text: (Int) -> String): WorkoutPlanImport {
    require(days.size == workouts.size && days.distinct().size == days.size)
    return WorkoutPlanImport(key, workouts.mapIndexed { index, workout ->
        PlannedWorkoutImport(text(workout.titleRes), days.sorted()[index], workout.exercises.map {
            PlannedExerciseImport(it.key, text(it.nameRes), text(R.string.plan_reps_note_label) + " " + text(it.repsRes), it.sets)
        })
    })
}
