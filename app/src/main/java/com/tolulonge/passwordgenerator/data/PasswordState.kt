package com.tolulonge.passwordgenerator.data

data class PasswordState(
    val length: Int = 12,
    val includeUppercase: Boolean = true,
    val includeLowercase: Boolean = true,
    val includeNumbers: Boolean = true,
    val includeSymbols: Boolean = false,
    val password: String = ""
)