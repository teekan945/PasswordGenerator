# Macrobenchmarking Troubleshooting Guide

Common issues and solutions when implementing macrobenchmarking manually.

## 🔧 Build Configuration Issues

### Issue: Plugin not found errors
```
Plugin [id: 'androidx.baselineprofile'] was not found
```

**Solutions:**
1. **Check version catalog**: Ensure `gradle/libs.versions.toml` has correct plugin definition
2. **Use alias correctly**: `alias(libs.plugins.androidx.baselineprofile)` not `id("androidx.baselineprofile")`
3. **Update AGP version**: Requires Android Gradle Plugin 8.0+

### Issue: No matching variant errors
```
No matching variant of project :macrobenchmark was found
```

**Solutions:**
1. **Add baselineProfile plugin** to macrobenchmark module
2. **Check build types** match between app and macrobenchmark modules
3. **Verify targetProjectPath** in macrobenchmark build.gradle.kts

### Issue: Dependency resolution failures
```
Could not resolve project :macrobenchmark
```

**Solutions:**
1. **Add to settings.gradle.kts**: `include(":macrobenchmark")`
2. **Check module structure**: Ensure macrobenchmark directory exists
3. **Verify build.gradle.kts** syntax in macrobenchmark module

## 📱 Device & Runtime Issues

### Issue: No devices found
```
adb devices` shows empty or unauthorized
```

**Solutions:**
1. **Enable USB debugging** in Developer Options
2. **Install device drivers** (Windows) or verify USB connection
3. **Accept USB debugging prompt** on device
4. **Try different USB cable/port**

### Issue: App installation failures
```
Installation failed with message INSTALL_FAILED_UPDATE_INCOMPATIBLE
```

**Solutions:**
1. **Uninstall existing app**: `adb uninstall com.yourpackage.app`
2. **Clean build**: `./gradlew clean`
3. **Check signing config** matches between variants
4. **Verify package name** consistency

### Issue: Permission errors during testing
```
SecurityException: Permission denied
```

**Solutions:**
1. **Add profileable to manifest**: `<profileable android:shell="true" />`
2. **Install benchmark variant**: Use benchmark build type, not debug
3. **Check device API level**: Requires API 24+

## 🎯 UI Automation Issues

### Issue: UI elements not found
```
UiObjectNotFoundException
```

**Solutions:**
1. **Check actual UI text**: Use `By.text("Exact Text")`
2. **Wait for elements**: `device.wait(Until.hasObject(...), 10000)`
3. **Use UI Automator Viewer**: Debug actual UI element properties
4. **Try alternative selectors**: contentDescription, className, resourceId

### Issue: Tests timing out
```
Test timed out after 120 seconds
```

**Solutions:**
1. **Increase wait times**: Use longer timeouts for slow operations
2. **Reduce test complexity**: Simplify interactions in single test
3. **Check device performance**: Use physical device over emulator
4. **Add debug logging**: Verify test progression

### Issue: Inconsistent test results
```
Tests pass sometimes, fail other times
```

**Solutions:**
1. **Close other apps**: Ensure consistent device state
2. **Disable animations**: Set animation scales to 0.5x or off
3. **Use `device.waitForIdle()`**: After each interaction
4. **Add longer sleeps**: Between rapid interactions

## 📊 Performance Measurement Issues

### Issue: No performance data captured
```
Observed no expect/actual slices in trace
```

**Solutions:**
1. **Increase interaction complexity**: Simple clicks may not generate measurable frames
2. **Add longer interactions**: Use scrolling, animations, complex UI updates
3. **Check frame generation**: Ensure UI actually renders during test
4. **Use appropriate metrics**: StartupTimingMetric for startup, FrameTimingMetric for UI

### Issue: Baseline profile not improving performance
```
No measurable startup improvement
```

**Solutions:**
1. **Verify profile location**: Check `app/src/main/baseline-prof.txt` exists
2. **Rebuild app**: Profile changes require rebuild
3. **Test cold startup**: Clear app from recent apps before testing
4. **Check profile content**: Ensure it contains your app's classes

### Issue: High frame times / jank
```
frameDurationCpuMs consistently > 16.67ms
```

**Solutions:**
1. **Optimize interactions**: Test during actual performance bottlenecks
2. **Check device**: Use mid-range device for realistic results
3. **Profile UI rendering**: Focus on complex screens, scrolling, animations
4. **Reduce test speed**: Slower interactions may show better performance

## 🔍 Debugging Techniques

### UI Automator Debugging
```bash
# Enable UI Automator logging
adb shell settings put global ui_automator_verbose_logs 1

# Capture UI hierarchy
adb shell uiautomator dump
adb pull /sdcard/window_dump.xml
```

### Performance Trace Analysis
```bash
# Pull traces for manual analysis
adb pull /sdcard/Android/media/com.yourpackage.macrobenchmark/additional_test_output/

# Traces can be opened in Android Studio or Perfetto UI
```

### Build Debugging
```bash
# Check available tasks
./gradlew tasks --group=benchmark

# Verbose build output
./gradlew :macrobenchmark:connectedBenchmarkAndroidTest --info

# Check configuration
./gradlew :app:dependencies --configuration benchmarkCompileClasspath
```

## ⚡ Common Performance Patterns

### Expected Results by App Type

**Simple Apps (like Password Generator):**
- Startup: 200-400ms
- Frame timing: 3-8ms typical
- Jank: <3%

**Medium Complexity Apps:**
- Startup: 400-800ms
- Frame timing: 5-12ms typical
- Jank: 3-7%

**Complex Apps (social, games, heavy UI):**
- Startup: 800-1500ms
- Frame timing: 8-15ms typical
- Jank: 5-15%

### Red Flags
- Startup >2000ms
- Frame timing >20ms regularly
- Jank >20%
- High memory usage during tests

## 📋 Pre-Flight Checklist

Before running benchmarks:

**Device Setup:**
- [ ] USB debugging enabled
- [ ] Animation scales set to 0.5x or off
- [ ] Device plugged in (not on battery)
- [ ] Other apps closed
- [ ] Device unlocked

**Code Setup:**
- [ ] Package name updated in all benchmark files
- [ ] UI selectors match actual app elements
- [ ] Build variants configured correctly
- [ ] Profileable added to manifest

**Test Setup:**
- [ ] Interactions test actual app functionality
- [ ] Wait conditions have reasonable timeouts
- [ ] Tests focus on critical user journeys
- [ ] Baseline profile tests most common flows

## 🆘 Last Resort Solutions

### Complete Reset
1. `./gradlew clean`
2. Delete `.gradle` and `build` directories
3. `./gradlew build`
4. Reinstall app on device

### Alternative Testing
If macrobenchmarks fail, try:
1. **Manual testing**: Time app startup with stopwatch
2. **Systrace**: Use Android Studio's built-in profiler
3. **Simple metrics**: Log timestamps in app code
4. **Firebase Performance**: Add performance monitoring SDK

### Simplified Setup
Start with minimal configuration:
1. Only startup benchmark initially
2. Simplest possible UI interactions
3. No baseline profile generation first
4. Add complexity gradually

Remember: Macrobenchmarking can be finicky. Start simple, verify each step works, then add complexity!