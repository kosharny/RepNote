package com.example.repnote.ui.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.example.repnote.ui.state.SetEntryUiState
import com.example.repnote.ui.state.addSetEntry
import com.example.repnote.ui.state.decodeEntries
import com.example.repnote.ui.state.encodeEntries
import com.example.repnote.ui.state.removeSetEntry
import com.example.repnote.ui.state.updateStateCompletion
import com.example.repnote.ui.state.updateStateReps
import com.example.repnote.ui.state.updateStateWeight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


private const val ENTRIES_KEY = "exercise_entries"
class ExerciseEntryViewModel(
    private val savedStateHandle: SavedStateHandle
): ViewModel() {
    private val _entries = MutableStateFlow(restoreEntries())

    val entries: StateFlow<List<SetEntryUiState>> =
        _entries.asStateFlow()

    fun changeWeight(entryId: Long, newWeight: String) {
        replaceEntries(updateStateWeight(_entries.value, entryId, newWeight))
    }

    fun changeReps(entryId: Long, newReps: String) {
        replaceEntries(updateStateReps(_entries.value, entryId, newReps))
    }

    fun changeCompletion(entryId: Long, isCompleted: Boolean) {
        replaceEntries(updateStateCompletion(_entries.value, entryId, isCompleted))
    }

    fun addEntry() {
        replaceEntries(addSetEntry(_entries.value))
    }

    fun removeEntry(entryId: Long) {
        replaceEntries(removeSetEntry(_entries.value, entryId))
    }

    private fun restoreEntries(): List<SetEntryUiState> {
        val saved = savedStateHandle.get<ArrayList<Any>>(ENTRIES_KEY)

        return if (saved != null) {
            decodeEntries(saved)
        } else {
            listOf(
                SetEntryUiState(id = 1L),
                SetEntryUiState(id = 2L),
                SetEntryUiState(id = 3L)
            )
        }
    }

    private fun replaceEntries(newEntries: List<SetEntryUiState>) {
        savedStateHandle[ENTRIES_KEY] = ArrayList(encodeEntries(newEntries))
        _entries.value = newEntries
    }


}