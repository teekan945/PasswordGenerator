package com.tolulonge.passwordgenerator.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import com.tolulonge.passwordgenerator.data.PasswordState
import com.tolulonge.passwordgenerator.utils.PasswordGenerator
import javax.inject.Inject

@HiltViewModel
class PasswordGeneratorViewModel @Inject constructor() : ViewModel() {
    private val _passwordState = MutableStateFlow(PasswordState())
    val passwordState: StateFlow<PasswordState> = _passwordState.asStateFlow()

    fun updateLength(length: Int) {
        _passwordState.value = _passwordState.value.copy(length = length)
    }

    fun updateIncludeLowercase(include: Boolean) {
        _passwordState.value = _passwordState.value.copy(includeLowercase = include)
    }

    fun updateIncludeUppercase(include: Boolean) {
        _passwordState.value = _passwordState.value.copy(includeUppercase = include)
    }

    fun updateIncludeNumbers(include: Boolean) {
        _passwordState.value = _passwordState.value.copy(includeNumbers = include)
    }

    fun updateIncludeSymbols(include: Boolean) {
        _passwordState.value = _passwordState.value.copy(includeSymbols = include)
    }

    fun generatePassword() {
        val currentState = _passwordState.value
        val newPassword = PasswordGenerator.generatePassword(
            currentState.length,
            currentState.includeUppercase,
            currentState.includeLowercase,
            currentState.includeNumbers,
            currentState.includeSymbols
        )
        _passwordState.value = currentState.copy(password = newPassword)
    }
}