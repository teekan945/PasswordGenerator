package com.tolulonge.passwordgenerator.utils

import org.junit.Assert.*
import org.junit.Test

class PasswordGeneratorTest {

    @Test
    fun `generatePassword should return empty string when no character types selected`() {
        val password = PasswordGenerator.generatePassword(
            length = 12,
            includeUppercase = false,
            includeLowercase = false,
            includeNumbers = false,
            includeSymbols = false
        )
        
        assertEquals("", password)
    }

    @Test
    fun `generatePassword should return password with correct length`() {
        val password = PasswordGenerator.generatePassword(
            length = 15,
            includeUppercase = true,
            includeLowercase = true,
            includeNumbers = true,
            includeSymbols = false
        )
        
        assertEquals(15, password.length)
    }

    @Test
    fun `generatePassword should contain only lowercase when only lowercase selected`() {
        val password = PasswordGenerator.generatePassword(
            length = 20,
            includeUppercase = false,
            includeLowercase = true,
            includeNumbers = false,
            includeSymbols = false
        )
        
        assertTrue(password.all { it.isLowerCase() && it.isLetter() })
        assertEquals(20, password.length)
    }

    @Test
    fun `generatePassword should contain only uppercase when only uppercase selected`() {
        val password = PasswordGenerator.generatePassword(
            length = 20,
            includeUppercase = true,
            includeLowercase = false,
            includeNumbers = false,
            includeSymbols = false
        )
        
        assertTrue(password.all { it.isUpperCase() && it.isLetter() })
        assertEquals(20, password.length)
    }

    @Test
    fun `generatePassword should contain only numbers when only numbers selected`() {
        val password = PasswordGenerator.generatePassword(
            length = 20,
            includeUppercase = false,
            includeLowercase = false,
            includeNumbers = true,
            includeSymbols = false
        )
        
        assertTrue(password.all { it.isDigit() })
        assertEquals(20, password.length)
    }

    @Test
    fun `generatePassword should contain only symbols when only symbols selected`() {
        val password = PasswordGenerator.generatePassword(
            length = 20,
            includeUppercase = false,
            includeLowercase = false,
            includeNumbers = false,
            includeSymbols = true
        )
        
        val symbolChars = "!@#$%^&*()_-+=<>?/[]{}|"
        assertTrue(password.all { it in symbolChars })
        assertEquals(20, password.length)
    }

    @Test
    fun `generatePassword should contain mixed characters when multiple types selected`() {
        val password = PasswordGenerator.generatePassword(
            length = 100, // Large length to ensure we get variety
            includeUppercase = true,
            includeLowercase = true,
            includeNumbers = true,
            includeSymbols = true
        )
        
        val hasUppercase = password.any { it.isUpperCase() && it.isLetter() }
        val hasLowercase = password.any { it.isLowerCase() && it.isLetter() }
        val hasNumbers = password.any { it.isDigit() }
        val hasSymbols = password.any { it in "!@#$%^&*()_-+=<>?/[]{}|" }
        
        // With a length of 100, we should statistically have all character types
        assertTrue("Should contain uppercase letters", hasUppercase)
        assertTrue("Should contain lowercase letters", hasLowercase)
        assertTrue("Should contain numbers", hasNumbers)
        assertTrue("Should contain symbols", hasSymbols)
        assertEquals(100, password.length)
    }

    @Test
    fun `generatePassword should create different passwords on multiple calls`() {
        val password1 = PasswordGenerator.generatePassword(
            length = 12,
            includeUppercase = true,
            includeLowercase = true,
            includeNumbers = true,
            includeSymbols = false
        )
        
        val password2 = PasswordGenerator.generatePassword(
            length = 12,
            includeUppercase = true,
            includeLowercase = true,
            includeNumbers = true,
            includeSymbols = false
        )
        
        assertNotEquals(password1, password2)
    }

    @Test
    fun `generatePassword should handle minimum length`() {
        val password = PasswordGenerator.generatePassword(
            length = 1,
            includeUppercase = false,
            includeLowercase = true,
            includeNumbers = false,
            includeSymbols = false
        )
        
        assertEquals(1, password.length)
        assertTrue(password.first().isLowerCase() && password.first().isLetter())
    }

    @Test
    fun `generatePassword should handle maximum typical length`() {
        val password = PasswordGenerator.generatePassword(
            length = 32,
            includeUppercase = true,
            includeLowercase = true,
            includeNumbers = true,
            includeSymbols = true
        )
        
        assertEquals(32, password.length)
        assertTrue(password.isNotEmpty())
    }

    @Test
    fun `generatePassword should not contain invalid characters`() {
        val password = PasswordGenerator.generatePassword(
            length = 50,
            includeUppercase = true,
            includeLowercase = true,
            includeNumbers = true,
            includeSymbols = true
        )
        
        val validChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*()_-+=<>?/[]{}|"
        assertTrue(password.all { it in validChars })
    }

    @Test
    fun `generatePassword should work with all combinations of character types`() {
        // Test all 15 possible combinations (2^4 - 1, excluding all false)
        val combinations = listOf(
            listOf(true, false, false, false),   // uppercase only
            listOf(false, true, false, false),   // lowercase only
            listOf(false, false, true, false),   // numbers only
            listOf(false, false, false, true),   // symbols only
            listOf(true, true, false, false),    // upper + lower
            listOf(true, false, true, false),    // upper + numbers
            listOf(true, false, false, true),    // upper + symbols
            listOf(false, true, true, false),    // lower + numbers
            listOf(false, true, false, true),    // lower + symbols
            listOf(false, false, true, true),    // numbers + symbols
            listOf(true, true, true, false),     // upper + lower + numbers
            listOf(true, true, false, true),     // upper + lower + symbols
            listOf(true, false, true, true),     // upper + numbers + symbols
            listOf(false, true, true, true),     // lower + numbers + symbols
            listOf(true, true, true, true)       // all types
        )
        
        combinations.forEach { (upper, lower, numbers, symbols) ->
            val password = PasswordGenerator.generatePassword(
                length = 12,
                includeUppercase = upper,
                includeLowercase = lower,
                includeNumbers = numbers,
                includeSymbols = symbols
            )
            
            assertEquals(12, password.length)
            assertTrue(password.isNotEmpty())
        }
    }
}