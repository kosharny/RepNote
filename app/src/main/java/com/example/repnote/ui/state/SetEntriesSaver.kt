package com.example.repnote.ui.state

import androidx.compose.runtime.saveable.listSaver

fun encodeEntries(
    entries: List<SetEntryUiState>
): List<Any> {
    return entries.flatMap { entry ->
        listOf(
            entry.id,
            entry.weightText,
            entry.repsText,
            entry.isCompleted
        )
    }
}

fun decodeEntries(
    values: List<Any>
): List<SetEntryUiState> {
    return values.chunked(4).map { fields ->
        SetEntryUiState(
            id = fields[0] as Long,
            weightText = fields[1] as String,
            repsText = fields[2] as String,
            isCompleted = fields[3] as Boolean
        )
    }
}


val SetEntriesSaver = listSaver<List<SetEntryUiState>, Any>(
    save = { entries -> encodeEntries(entries) },
    restore = { values -> decodeEntries(values) }
)