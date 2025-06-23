package com.tolulonge.passwordgenerator.viewmodel

import app.cash.turbine.test
import com.tolulonge.passwordgenerator.data.PasswordState
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@HiltAndroidTest
@RunWith(RobolectricTestRunner::class)
@Config(application = HiltTestApplication::class)
@OptIn(ExperimentalCoroutinesApi::class)
class PasswordGeneratorViewModelTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: PasswordGeneratorViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        hiltRule.inject()
        Dispatchers.setMain(testDispatcher)
        viewModel = PasswordGeneratorViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state should have default values`() = runTest {
        viewModel.passwordState.test {
            val initialState = awaitItem()
            assertEquals(12, initialState.length)
            assertTrue(initialState.includeUppercase)
            assertTrue(initialState.includeLowercase)
            assertTrue(initialState.includeNumbers)
            assertFalse(initialState.includeSymbols)
            assertEquals("", initialState.password)
        }
    }

    @Test
    fun `updateLength should update password length`() = runTest {
        viewModel.passwordState.test {
            awaitItem() // Skip initial state
            
            viewModel.updateLength(16)
            
            val updatedState = awaitItem()
            assertEquals(16, updatedState.length)
        }
    }

    @Test
    fun `updateIncludeLowercase should update lowercase flag`() = runTest {
        viewModel.passwordState.test {
            awaitItem() // Skip initial state
            
            viewModel.updateIncludeLowercase(false)
            
            val updatedState = awaitItem()
            assertFalse(updatedState.includeLowercase)
        }
    }

    @Test
    fun `updateIncludeUppercase should update uppercase flag`() = runTest {
        viewModel.passwordState.test {
            awaitItem() // Skip initial state
            
            viewModel.updateIncludeUppercase(false)
            
            val updatedState = awaitItem()
            assertFalse(updatedState.includeUppercase)
        }
    }

    @Test
    fun `updateIncludeNumbers should update numbers flag`() = runTest {
        viewModel.passwordState.test {
            awaitItem() // Skip initial state
            
            viewModel.updateIncludeNumbers(false)
            
            val updatedState = awaitItem()
            assertFalse(updatedState.includeNumbers)
        }
    }

    @Test
    fun `updateIncludeSymbols should update symbols flag`() = runTest {
        viewModel.passwordState.test {
            awaitItem() // Skip initial state
            
            viewModel.updateIncludeSymbols(true)
            
            val updatedState = awaitItem()
            assertTrue(updatedState.includeSymbols)
        }
    }

    @Test
    fun `generatePassword should create password with correct length`() = runTest {
        viewModel.passwordState.test {
            awaitItem() // Skip initial state
            
            viewModel.generatePassword()
            
            val updatedState = awaitItem()
            assertEquals(12, updatedState.password.length)
            assertTrue(updatedState.password.isNotEmpty())
        }
    }

    @Test
    fun `generatePassword with custom length should create password with correct length`() = runTest {
        viewModel.passwordState.test {
            awaitItem() // Skip initial state
            
            viewModel.updateLength(20)
            awaitItem() // Skip length update
            
            viewModel.generatePassword()
            
            val updatedState = awaitItem()
            assertEquals(20, updatedState.password.length)
        }
    }

    @Test
    fun `generatePassword should include only selected character types`() = runTest {
        // Set to only lowercase
        viewModel.updateIncludeUppercase(false)
        viewModel.updateIncludeNumbers(false)
        viewModel.updateIncludeSymbols(false)
        viewModel.generatePassword()
        
        // Check final state directly
        val finalState = viewModel.passwordState.value
        assertTrue(finalState.password.all { it.isLowerCase() && it.isLetter() })
    }

    @Test
    fun `generatePassword should create different passwords on multiple calls`() = runTest {
        viewModel.passwordState.test {
            awaitItem() // Skip initial state
            
            viewModel.generatePassword()
            val firstPassword = awaitItem().password
            
            viewModel.generatePassword()
            val secondPassword = awaitItem().password
            
            assertNotEquals(firstPassword, secondPassword)
        }
    }

    @Test
    fun `multiple state updates should maintain correct state`() = runTest {
        viewModel.passwordState.test {
            awaitItem() // Skip initial state
            
            viewModel.updateLength(16)
            awaitItem()
            
            viewModel.updateIncludeSymbols(true)
            awaitItem()
            
            viewModel.updateIncludeLowercase(false)
            val finalState = awaitItem()
            
            assertEquals(16, finalState.length)
            assertTrue(finalState.includeSymbols)
            assertFalse(finalState.includeLowercase)
            assertTrue(finalState.includeUppercase) // Should remain unchanged
            assertTrue(finalState.includeNumbers) // Should remain unchanged
        }
    }
}