# Macrobenchmark Test Templates

Complete copy-paste templates for implementing macrobenchmarking in your Android project.

## 1. Startup Benchmark Template

**File**: `macrobenchmark/src/main/java/com/yourpackage/macrobenchmark/StartupBenchmark.kt`

```kotlin
package com.yourpackage.macrobenchmark

import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class StartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startup() = benchmarkRule.measureRepeated(
        packageName = "com.yourpackage.yourapp",  // Change this to your package
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
    }
}
```

## 2. UI Performance Benchmark Template

**File**: `macrobenchmark/src/main/java/com/yourpackage/macrobenchmark/UIPerformanceBenchmark.kt`

```kotlin
package com.yourpackage.macrobenchmark

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

@RunWith(AndroidJUnit4::class)
@LargeTest
class UIPerformanceBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    private lateinit var device: UiDevice

    @Before
    fun setUp() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        device = UiDevice.getInstance(instrumentation)
    }

    @Test
    fun userInteractionPerformance() = benchmarkRule.measureRepeated(
        packageName = "com.yourpackage.yourapp",  // Change this to your package
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.WARM,
        setupBlock = {
            pressHome()
            startActivityAndWait()
        }
    ) {
        // Wait for your main UI element to appear
        device.wait(Until.hasObject(By.text("Your Main Title")), 10000)
        
        // CUSTOMIZE THESE INTERACTIONS FOR YOUR APP:
        
        // Example: Find and click a button
        val mainButton = device.findObject(By.text("Your Button Text"))
        if (mainButton != null) {
            repeat(5) {
                mainButton.click()
                device.waitForIdle()
            }
        }
        
        // Example: Interact with list/scroll view
        val scrollableArea = device.findObject(By.scrollable(true))
        if (scrollableArea != null) {
            repeat(3) {
                scrollableArea.scroll(UiDirection.DOWN, 1.0f)
                device.waitForIdle()
            }
        }
        
        // Example: Text input
        val textField = device.findObject(By.className("android.widget.EditText"))
        if (textField != null) {
            textField.click()
            textField.text = "Test input"
            device.waitForIdle()
        }
    }
}
```

## 3. Baseline Profile Generator Template

**File**: `macrobenchmark/src/main/java/com/yourpackage/macrobenchmark/BaselineProfileGenerator.kt`

```kotlin
package com.yourpackage.macrobenchmark

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
        packageName = "com.yourpackage.yourapp"  // Change this to your package
    ) {
        // This defines your app's critical user journey for optimization
        
        // 1. Start the app
        pressHome()
        startActivityAndWait()

        // 2. Wait for main screen
        device.wait(Until.hasObject(By.text("Your Main Title")), 10000)

        // 3. CUSTOMIZE: Add your app's most common user actions
        
        // Example: Navigation to main features
        val menuButton = device.findObject(By.text("Menu"))
        menuButton?.click()
        device.waitForIdle()

        // Example: Core functionality usage
        val primaryFeature = device.findObject(By.text("Primary Feature"))
        primaryFeature?.click()
        device.waitForIdle()

        // Example: Form interaction
        val inputField = device.findObject(By.className("android.widget.EditText"))
        if (inputField != null) {
            inputField.click()
            inputField.text = "Sample text"
            device.waitForIdle()
        }

        // Example: List scrolling (if your app has lists)
        val scrollableList = device.findObject(By.scrollable(true))
        if (scrollableList != null) {
            repeat(3) {
                scrollableList.scroll(UiDirection.DOWN, 1.0f)
                device.waitForIdle()
            }
        }

        // Example: Settings/configuration
        val settingsButton = device.findObject(By.text("Settings"))
        settingsButton?.click()
        device.waitForIdle()
    }
}
```

## 4. Common UI Selectors

### Finding UI Elements

```kotlin
// By text content
device.findObject(By.text("Button Text"))
device.findObject(By.textContains("Partial Text"))

// By content description
device.findObject(By.desc("Content Description"))

// By class name
device.findObject(By.className("android.widget.Button"))
device.findObject(By.className("android.widget.EditText"))

// By resource ID (if available)
device.findObject(By.res("com.yourpackage:id/button_id"))

// Complex selectors
device.findObject(By.text("Text").clickable(true))
device.findObject(By.scrollable(true))
```

### Common Interactions

```kotlin
// Click interactions
element.click()
element.longClick()

// Text input
element.text = "Your text here"
element.clear()

// Scrolling
element.scroll(UiDirection.DOWN, 1.0f)
element.scroll(UiDirection.UP, 0.5f)

// Waiting
device.waitForIdle()
device.wait(Until.hasObject(By.text("Element")), 5000)
```

## 5. Customization Guide

### For Your Main Project:

1. **Replace package name**: Change `com.yourpackage.yourapp` to your actual package
2. **Update UI selectors**: Use your actual button text, UI elements
3. **Map user journeys**: Replace example interactions with your app's core flows
4. **Test critical paths**: Focus on most-used features for baseline profiles

### Common User Journeys to Test:

- **App launch** → **Main screen** → **Primary feature**
- **Login flow** (if applicable)
- **Data loading** → **List scrolling** → **Item selection**
- **Form filling** → **Submission**
- **Settings** → **Configuration changes**

### Performance Targets:

- **Startup**: <500ms excellent, <1000ms acceptable
- **Frame timing**: <12ms excellent, <16.67ms acceptable
- **Jank**: <5% excellent, <10% acceptable