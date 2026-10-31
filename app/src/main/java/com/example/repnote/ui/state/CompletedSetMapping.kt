package com.example.repnote.ui.state

import com.example.repnote.model.CompletedSet

fun toCompletedSet(
    entry: SetEntryUiState
): CompletedSet? {
    if (!entry.isCompleted) return null
    val weight = entry.weightText
        .trim()
        .replace(',', '.')
        .toDoubleOrNull()
        ?: return null
    val reps = entry.repsText
        .trim()
        .toIntOrNull()
        ?: return null
    if (!weight.isFinite() || weight < 0 || reps <= 0) return null
    return CompletedSet(
        weightKg = weight,
        reps = reps
    )
}