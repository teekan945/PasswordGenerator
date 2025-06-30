package com.tolulonge.passwordgenerator.macrobenchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
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
 * Generates a baseline profile which can be used to improve app startup.
 * 
 * This baseline profile lists the classes and methods that should be compiled ahead of time,
 * improving cold startup performance by avoiding interpretation and JIT compilation on the
 * critical path.
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class BaselineProfileGenerator {
    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
    }

    @Test
    fun generate() = baselineProfileRule.collect(
        packageName = "com.tolulonge.passwordgenerator"
    ) {
        // This block defines the app's critical user journey that you want to optimize.
        // Here we include the primary user flows:
        
        // 1. Start the app
        pressHome()
        startActivityAndWait()

        // Wait for the main screen to load
        device.wait(Until.hasObject(By.desc("Generate Password")), 10000)

        // 2. Generate initial password (most common user action)
        val generateButton = device.findObject(By.desc("Generate Password"))
        generateButton?.click()
        device.waitForIdle()

        // 3. Interact with password customization options
        // Change password length
        val slider = device.findObject(By.desc("Password Length Slider"))
        slider?.click()
        device.waitForIdle()

        // 4. Toggle character type options (common user interactions)
        val uppercaseCheckbox = device.findObject(By.text("Uppercase Letters"))
        uppercaseCheckbox?.click()
        device.waitForIdle()

        val numbersCheckbox = device.findObject(By.text("Numbers"))
        numbersCheckbox?.click()
        device.waitForIdle()

        val symbolsCheckbox = device.findObject(By.text("Symbols"))
        symbolsCheckbox?.click()
        device.waitForIdle()

        // 5. Generate password with custom settings
        generateButton?.click()
        device.waitForIdle()

        // 6. Copy password to clipboard (if available)
        val copyButton = device.findObject(By.desc("Copy Password"))
        copyButton?.click()
        device.waitForIdle()

        // 7. Generate multiple passwords (simulate typical usage)
        repeat(3) {
            generateButton?.click()
            device.waitForIdle()
        }
    }
}