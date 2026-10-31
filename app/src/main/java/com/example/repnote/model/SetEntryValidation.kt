package com.example.repnote.model

fun isWeightValid(text: String): Boolean {
    val number = text.trim().replace(',','.').toDoubleOrNull()
    val isValid = number != null && number.isFinite() && number >= 0.0
    return  isValid
}


fun isRepsValid(text: String): Boolean {
    val number = text.trim().toIntOrNull()
    val isValid = number != null && number > 0
    return  isValid
}