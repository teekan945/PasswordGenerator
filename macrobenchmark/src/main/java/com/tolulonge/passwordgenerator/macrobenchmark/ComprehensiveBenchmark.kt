package com.tolulonge.passwordgenerator.macrobenchmark

import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.PowerMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
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
 * Comprehensive benchmark suite for Password Generator app.
 * 
 * This benchmark class provides additional performance testing scenarios
 * beyond the basic startup and UI tests. It demonstrates advanced
 * macrobenchmarking techniques for production apps.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class ComprehensiveBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
    }

    /**
     * Test warm startup performance - when app is already in memory
     */
    @Test
    fun warmStartup() = benchmarkRule.measureRepeated(
        packageName = "com.tolulonge.passwordgenerator",
        metrics = listOf(StartupTimingMetric()),
        iterations = 10, // More iterations for warm startup
        startupMode = StartupMode.WARM,
        compilationMode = CompilationMode.DEFAULT
    ) {
        pressHome()
        startActivityAndWait()
    }

    /**
     * Test hot startup performance - when app is in foreground background
     */
    @Test
    fun hotStartup() = benchmarkRule.measureRepeated(
        packageName = "com.tolulonge.passwordgenerator",
        metrics = listOf(StartupTimingMetric()),
        iterations = 10,
        startupMode = StartupMode.HOT,
        compilationMode = CompilationMode.DEFAULT
    ) {
        pressHome()
        startActivityAndWait()
    }

    /**
     * Test complete password generation workflow performance
     */
    @Test
    fun passwordGenerationWorkflow() = benchmarkRule.measureRepeated(
        packageName = "com.tolulonge.passwordgenerator",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        // Wait for UI to be ready
        device.wait(Until.hasObject(By.text("Password Generator")), 10000)
        
        // Complete password generation workflow
        // 1. Change length
        val slider = device.findObject(By.className("android.widget.SeekBar"))
        if (slider != null) {
            // Move slider to different positions
            slider.swipeLeft(50)
            device.waitForIdle()
            slider.swipeRight(100)
            device.waitForIdle()
        }
        
        // 2. Toggle character types (creates UI updates)
        val uppercaseCheckbox = device.findObject(By.text("Uppercase Letters"))
        uppercaseCheckbox?.click()
        device.waitForIdle()
        
        val numbersCheckbox = device.findObject(By.text("Numbers"))
        numbersCheckbox?.click()
        device.waitForIdle()
        
        val symbolsCheckbox = device.findObject(By.text("Symbols"))
        symbolsCheckbox?.click()
        device.waitForIdle()
        
        // 3. Generate multiple passwords (tests actual business logic)
        val generateButton = device.findObject(By.text("Generate Password"))
        if (generateButton != null) {
            repeat(5) {
                generateButton.click()
                device.waitForIdle()
                Thread.sleep(300) // Allow for password strength calculation
            }
        }
        
        // 4. Copy password (tests clipboard interaction)
        val copyButton = device.findObject(By.text("Copy to Clipboard"))
        copyButton?.click()
        device.waitForIdle()
    }

    /**
     * Test power consumption during intensive password generation
     * Note: PowerMetric requires rooted device or specific device setup
     */
    @Test
    fun powerConsumptionTest() = benchmarkRule.measureRepeated(
        packageName = "com.tolulonge.passwordgenerator",
        metrics = listOf(PowerMetric(PowerMetric.Type.TOTAL)),
        iterations = 3,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        device.wait(Until.hasObject(By.text("Password Generator")), 10000)
        
        // Intensive password generation to measure power impact
        val generateButton = device.findObject(By.text("Generate Password"))
        if (generateButton != null) {
            repeat(20) {
                generateButton.click()
                Thread.sleep(100) // Sustained activity
            }
        }
        
        device.waitForIdle()
    }

    /**
     * Test performance with different compilation modes
     * Useful for comparing JIT vs AOT performance
     */
    @Test
    fun compilationModeComparison() = benchmarkRule.measureRepeated(
        packageName = "com.tolulonge.passwordgenerator",
        metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = CompilationMode.None() // No compilation, pure interpretation
    ) {
        pressHome()
        startActivityAndWait()
        
        // Quick interaction to test frame timing without compilation
        device.wait(Until.hasObject(By.text("Generate Password")), 10000)
        val generateButton = device.findObject(By.text("Generate Password"))
        generateButton?.click()
        device.waitForIdle()
    }

    /**
     * Memory pressure test - generate many passwords to test memory management
     */
    @Test
    fun memoryStressTest() = benchmarkRule.measureRepeated(
        packageName = "com.tolulonge.passwordgenerator",
        metrics = listOf(FrameTimingMetric()),
        iterations = 3,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        device.wait(Until.hasObject(By.text("Password Generator")), 10000)
        
        // Generate many passwords to test memory allocation/garbage collection
        val generateButton = device.findObject(By.text("Generate Password"))
        if (generateButton != null) {
            repeat(50) { // Large number to stress memory
                generateButton.click()
                if (it % 10 == 0) {
                    device.waitForIdle() // Periodic waits to allow GC
                }
            }
        }
        
        device.waitForIdle()
    }

    /**
     * Configuration change test - test performance during orientation changes
     */
    @Test
    fun configurationChangeTest() = benchmarkRule.measureRepeated(
        packageName = "com.tolulonge.passwordgenerator",
        metrics = listOf(FrameTimingMetric()),
        iterations = 3,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        device.wait(Until.hasObject(By.text("Password Generator")), 10000)
        
        // Generate a password first
        val generateButton = device.findObject(By.text("Generate Password"))
        generateButton?.click()
        device.waitForIdle()
        
        // Simulate configuration changes (screen rotation)
        repeat(3) {
            device.setOrientationNatural()
            device.waitForIdle()
            device.setOrientationLeft()
            device.waitForIdle()
        }
        
        // Return to natural orientation
        device.setOrientationNatural()
        device.waitForIdle()
    }
}