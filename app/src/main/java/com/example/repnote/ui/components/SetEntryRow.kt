package com.example.repnote.ui.components

import androidx.compose.ui.res.stringResource
import com.example.repnote.R
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.animation.animateContentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.repnote.ui.state.SetEntryUiState
import com.example.repnote.ui.theme.RepNoteTheme

@Composable
fun SetEntryRow(
    setNumber: Int,
    entry: SetEntryUiState,
    onCompletedChange: (Boolean) -> Unit,
    onWeightChange: (String) -> Unit,
    onRepsChange: (String) -> Unit,
    canComplete: Boolean,
    weightError: String?,
    repsError: String?,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val repsFocusRequester = remember {
        FocusRequester()
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val completionEnabled = canComplete || entry.isCompleted

    OutlinedCard(
        modifier = modifier.animateContentSize().fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.set_number, setNumber),
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleMedium
                )

                TextButton(
                    onClick = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onDelete()
                    }
                ) {
                    Text(
                        text = stringResource(R.string.delete),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            OutlinedTextField(
                value = entry.weightText,
                onValueChange = onWeightChange,
                modifier = Modifier.fillMaxWidth(),
                label = {
                    Text(stringResource(R.string.weight_kg))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = {
                        repsFocusRequester.requestFocus()
                    }
                ),
                isError = weightError != null,
                supportingText = {
                    Text(
                        weightError ?: stringResource(R.string.weight_hint)
                    )
                }
            )

            OutlinedTextField(
                value = entry.repsText,
                onValueChange = onRepsChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(repsFocusRequester),
                label = {
                    Text(stringResource(R.string.reps))
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        focusManager.clearFocus()
                        keyboardController?.hide()
                    }
                ),
                isError = repsError != null,
                supportingText = {
                    Text(
                        repsError ?: stringResource(R.string.reps_error)
                    )
                }
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .toggleable(
                        value = entry.isCompleted,
                        enabled = completionEnabled,
                        role = Role.Checkbox,
                        onValueChange = { checked ->
                            if (checked) {
                                focusManager.clearFocus()
                                keyboardController?.hide()
                            }

                            onCompletedChange(checked)
                        }
                    ),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Checkbox(
                    checked = entry.isCompleted,
                    onCheckedChange = null,
                    enabled = completionEnabled
                )

                Text(
                    text = if (entry.isCompleted) {
                        stringResource(R.string.set_completed)
                    } else {
                        stringResource(R.string.mark_completed)
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            if (!canComplete && !entry.isCompleted) {
                Text(
                    text = stringResource(R.string.set_validation_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true, widthDp = 360, heightDp = 640)
fun SetEntryRowPreview() {
    RepNoteTheme() {
        SetEntryRow(
            setNumber = 1,
            entry = SetEntryUiState(id = 1L),
            onCompletedChange = { },
            onWeightChange = { },
            onRepsChange = {  },
            canComplete = false,
            weightError = null,
            repsError = null,
            onDelete = {},
        )
    }
}
