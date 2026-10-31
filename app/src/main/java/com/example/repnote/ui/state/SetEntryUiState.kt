package com.example.repnote.ui.state

import kotlinx.serialization.Serializable

@Serializable
data class SetEntryUiState(
    val id: Long,
    val weightText: String = "",
    val repsText: String = "",
    val isCompleted: Boolean = false
)