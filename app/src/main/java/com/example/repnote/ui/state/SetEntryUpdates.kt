package com.example.repnote.ui.state

import com.example.repnote.model.isRepsValid
import com.example.repnote.model.isWeightValid

fun updateStateWeight(
    entries: List<SetEntryUiState>,
    entryId: Long,
    newWeight: String,
): List<SetEntryUiState> {
    return entries.map { entry ->
        if (entry.id == entryId) {
            entry.copy(weightText = newWeight, isCompleted = false)
        } else entry
    }
}

fun updateStateReps(
    entries: List<SetEntryUiState>,
    entryId: Long,
    newReps: String
): List<SetEntryUiState> {
    return entries.map {entry ->
        if (entry.id == entryId) {
            entry.copy(repsText = newReps, isCompleted = false)
        } else entry
    }
}

fun updateStateCompletion(
    entries: List<SetEntryUiState>,
    entryId: Long,
    isCompleted: Boolean
): List<SetEntryUiState> {
    return entries.map { entry ->
        if (entry.id == entryId) {
            val canComplete =
                isWeightValid(entry.weightText) &&
                        isRepsValid(entry.repsText)
            if (!isCompleted || canComplete) {
                entry.copy(isCompleted = isCompleted)
            } else {
                entry
            }
        } else entry
    }
}

fun addSetEntry(
    entries: List<SetEntryUiState>
): List<SetEntryUiState> {
    val nextId = (entries.maxOfOrNull { entry -> entry.id } ?: 0L) + 1L
    val newEntry = SetEntryUiState(nextId)
    return  entries + newEntry
}

fun removeSetEntry(
    entries: List<SetEntryUiState>,
    entryId: Long
): List<SetEntryUiState> {
    return  entries.filterNot { entry -> entry.id == entryId }
}