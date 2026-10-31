package com.example.repnote.ui.components

import com.example.repnote.ui.util.formatSessionDate
import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.repnote.model.Exercise
import com.example.repnote.model.WorkoutTemplate
import com.example.repnote.ui.theme.RepNoteTheme

@Composable
fun WorkoutCard(
    workout: WorkoutTemplate,
    hasDraft: Boolean,
    completedSetCount: Int,
    onClick: () -> Unit,
    onRename: () -> Unit,
    onDiscardDraft: () -> Unit,
    onEdit: () -> Unit,
    canDelete: Boolean,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    val colors = MaterialTheme.colorScheme
    val plan = workout.sourcePlanKey?.let(com.example.repnote.data.plans.WorkoutPlanCatalog::find)

    val cardColor by animateColorAsState(if (hasDraft) colors.primaryContainer else colors.surfaceContainerLow, label = "workoutCardColor")
    Card(
        onClick = onClick,
        modifier = modifier.animateContentSize(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = if (hasDraft) {
                colors.onPrimaryContainer
            } else {
                colors.onSurface
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    plan?.let { Text(stringResource(it.titleRes), style = MaterialTheme.typography.labelMedium) }
                    workout.scheduledDayOfWeek?.let { day ->
                        Text(stringResource(com.example.repnote.ui.util.weekdayNameRes(day)),
                            style = MaterialTheme.typography.labelLarge, color = colors.primary)
                    }
                    Text(text = workout.title, style = MaterialTheme.typography.titleLarge)
                }
                if (plan != null && androidx.compose.ui.platform.LocalDensity.current.fontScale <= 1.3f) {
                    androidx.compose.foundation.Image(
                        androidx.compose.ui.res.painterResource(plan.artworkRes), contentDescription = null,
                        modifier = Modifier.width(88.dp).height(76.dp),
                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                    )
                }

                Box {
                    IconButton(
                        onClick = {
                            menuExpanded = true
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription =
                                stringResource(R.string.workout_actions, workout.title)
                        )
                    }

                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = {
                            menuExpanded = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.edit))
                            },
                            enabled = !hasDraft,
                            onClick = {
                                menuExpanded = false
                                onEdit()
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(stringResource(R.string.rename))
                            },
                            onClick = {
                                menuExpanded = false
                                onRename()
                            }
                        )

                        if (hasDraft) {
                            DropdownMenuItem(
                                text = {
                                    Text(stringResource(R.string.discard_draft))
                                },
                                onClick = {
                                    menuExpanded = false
                                    onDiscardDraft()
                                }
                            )
                        }

                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = stringResource(R.string.delete_workout),
                                    color = if (canDelete) {
                                        colors.error
                                    } else {
                                        colors.onSurface.copy(alpha = 0.38f)
                                    }
                                )
                            },
                            enabled = canDelete,
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }

            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.exercise_count, workout.exerciseCount),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = workout.lastCompletedAtEpochMillis?.let {
                        stringResource(R.string.last_workout, formatSessionDate(it, workout.lastTimeZoneId))
                    } ?: stringResource(R.string.no_workouts_yet),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            if (hasDraft) {
                Text(
                    text = stringResource(R.string.unfinished_workout),
                    style = MaterialTheme.typography.titleSmall
                )

                Text(
                    text = stringResource(R.string.completed_count, completedSetCount),
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = stringResource(R.string.finish_to_edit),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    if (hasDraft) {
                        stringResource(R.string.continue_workout)
                    } else {
                        stringResource(R.string.open_workout)
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun WorkoutCardPreview() {
    RepNoteTheme() {
        WorkoutCard(
            WorkoutTemplate(
                1L,
                "Верх тела",
                listOf(
                    Exercise(id = 1L, name = "Жим лежа"),
                    Exercise(id = 2L, name = "Тяга верхнего блока")
                ),
                "Последняя тренировка: вчера",
            ),
            onClick = {},
            onRename = {},
            hasDraft = false,
            completedSetCount = 0,
            onDiscardDraft = {},
            onEdit = {},
            canDelete = false,
            onDelete = {},
        )
    }
}

@Composable
@Preview
fun WorkoutCardPreviewLegs() {
    RepNoteTheme(darkTheme = true) {
        WorkoutCard(
            WorkoutTemplate(
                2L,
                "Ноги и корпус",
                listOf(
                    Exercise(id = 1L, name = "Жим лежа"),
                    Exercise(id = 2L, name = "Тяга верхнего блока")
                ),
                "Тренировок пока нет",
            ),
            onClick = {},
            onRename = {},
            hasDraft = true,
            completedSetCount = 2,
            onDiscardDraft = {},
            onEdit = {},
            canDelete = false,
            onDelete = {},
        )
    }
}
