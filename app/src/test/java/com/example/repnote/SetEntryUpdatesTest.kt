package com.example.repnote

import com.example.repnote.ui.state.SetEntryUiState
import com.example.repnote.ui.state.updateStateReps
import com.example.repnote.ui.state.updateStateWeight

import org.junit.Test

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class SetEntryUpdatesTest {
    @Test
    fun changingWeightUpdatesOnlySelectedEntry() {
        val entries = listOf(
            SetEntryUiState(
                id = 1L,
                weightText = "60",
                isCompleted = true
            ),
            SetEntryUiState(
                id = 2L,
                weightText = "40",
                isCompleted = true
            )
        )

        val result = updateStateWeight(
            entries = entries,
            entryId = 1L,
            newWeight = "62,5"
        )
        assertEquals("62,5",result[0].weightText )
        assertEquals(false,result[0].isCompleted )
        assertEquals(entries[1],result[1] )
        assertEquals("60",entries[0].weightText )
    }

    @Test
    fun changingRepsUpdatesOnlySelectedEntry() {
        val entries = listOf(
            SetEntryUiState(
                id = 1L,
                weightText = "60",
                repsText = "8",
                isCompleted = true
            ),
            SetEntryUiState(
                id = 2L,
                weightText = "40",
                repsText = "8",
                isCompleted = true
            )
        )

        val result = updateStateReps(
            entries = entries,
            entryId = 2L,
            newReps = "10"
        )
        assertEquals("10",result[1].repsText )
        assertEquals(false,result[1].isCompleted )
        assertEquals("40",result[1].weightText )
        assertEquals(entries[0],result[0] )
        assertEquals("8",entries[1].repsText )
    }
}