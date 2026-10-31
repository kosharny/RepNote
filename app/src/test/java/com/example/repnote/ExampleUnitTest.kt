package com.example.repnote

import com.example.repnote.model.isWeightValid
import com.example.repnote.ui.state.SetEntryUiState
import com.example.repnote.ui.state.updateStateWeight
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun zeroWeightIsValid() {
        val result = isWeightValid("0")

        assertEquals(true, result)
    }

    @Test
    fun negativeWeightIsInvalid() {
        val result = isWeightValid("-5")

        assertEquals(false, result)
    }

}