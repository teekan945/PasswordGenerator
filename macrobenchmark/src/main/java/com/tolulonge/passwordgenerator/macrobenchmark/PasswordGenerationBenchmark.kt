package com.tolulonge.passwordgenerator.macrobenchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark for password generation interactions.
 * 
 * This measures frame timing during password generation operations
 * to ensure smooth UI performance during core app functionality.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class PasswordGenerationBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
    }

    @Test
    fun scrollPasswordGeneration() = benchmarkRule.measureRepeated(
        packageName = "com.tolulonge.passwordgenerator",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        // Wait for the main UI to load
        device.wait(Until.hasObject(By.text("Password Generator")), 10000)
        
        // Find and interact with the Generate Password button using text
        val generateButton = device.findObject(By.text("Generate Password"))
        if (generateButton != null) {
            // Generate multiple passwords to test performance
            repeat(5) {
                generateButton.click()
                device.waitForIdle()
            }
        }
        
        // Test checkbox interactions using actual text from UI
        val uppercaseCheckbox = device.findObject(By.text("Uppercase Letters"))
        if (uppercaseCheckbox != null) {
            uppercaseCheckbox.click()
            device.waitForIdle()
        }
        
        val numbersCheckbox = device.findObject(By.text("Numbers"))
        if (numbersCheckbox != null) {
            numbersCheckbox.click()
            device.waitForIdle()
        }
        
        // Generate password with new settings
        if (generateButton != null) {
            generateButton.click()
            device.waitForIdle()
        }
    }

    @Test
    fun passwordGenerationPerformance() = benchmarkRule.measureRepeated(
        packageName = "com.tolulonge.passwordgenerator",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        // Wait for the main UI to load
        device.wait(Until.hasObject(By.text("Password Generator")), 10000)
        
        // Find the Generate Password button
        val generateButton = device.findObject(By.text("Generate Password"))
        
        if (generateButton != null) {
            // Generate passwords with longer intervals to ensure frame measurement
            repeat(5) {
                generateButton.click()
                // Longer wait to allow for proper frame measurement
                Thread.sleep(500)
            }
        }
        
        device.waitForIdle()
    }
}