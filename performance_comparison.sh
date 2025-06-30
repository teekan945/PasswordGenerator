#!/bin/bash

# Performance Comparison Script for Android Macrobenchmarking
# This script automates before/after baseline profile testing

set -e

PROJECT_ROOT="$(pwd)"
BASELINE_PROFILE="app/src/main/baseline-prof.txt"
BACKUP_PROFILE="app/src/main/baseline-prof.txt.backup"
RESULTS_DIR="performance_results"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

echo -e "${BLUE}🚀 Android Performance Comparison Script${NC}"
echo "======================================"

# Check if we're in the right directory
if [ ! -f "settings.gradle.kts" ]; then
    echo -e "${RED}❌ Error: Run this script from your Android project root directory${NC}"
    exit 1
fi

# Check if baseline profile exists
if [ ! -f "$BASELINE_PROFILE" ]; then
    echo -e "${RED}❌ Error: No baseline profile found at $BASELINE_PROFILE${NC}"
    echo "Run baseline profile generation first!"
    exit 1
fi

# Create results directory
mkdir -p "$RESULTS_DIR"

echo -e "${YELLOW}📋 Performance Comparison Test Plan:${NC}"
echo "1. Test WITHOUT baseline profile (backup current profile)"
echo "2. Test WITH baseline profile (restore profile)"
echo "3. Compare results and generate report"
echo ""

read -p "Continue? (y/N): " -n 1 -r
echo
if [[ ! $REPLY =~ ^[Yy]$ ]]; then
    echo "Cancelled."
    exit 0
fi

# Function to run benchmark and capture results
run_benchmark() {
    local test_name="$1"
    local output_file="$2"
    
    echo -e "${BLUE}🔄 Running $test_name benchmark...${NC}"
    
    # Run startup benchmark
    ./gradlew :macrobenchmark:connectedBenchmarkAndroidTest \
        -Pandroid.testInstrumentationRunnerArguments.class=com.tolulonge.passwordgenerator.macrobenchmark.StartupBenchmark \
        > "$output_file" 2>&1
    
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ $test_name benchmark completed${NC}"
    else
        echo -e "${RED}❌ $test_name benchmark failed${NC}"
        echo "Check $output_file for details"
        return 1
    fi
}

# Function to extract performance metrics from log
extract_metrics() {
    local log_file="$1"
    local result_file="$2"
    
    echo "=== Performance Metrics ===" > "$result_file"
    
    # Extract startup metrics
    if grep -q "timeToInitialDisplayMs" "$log_file"; then
        echo "Startup Performance:" >> "$result_file"
        grep "timeToInitialDisplayMs" "$log_file" | head -1 >> "$result_file"
        echo "" >> "$result_file"
    fi
    
    # Extract build time
    if grep -q "BUILD SUCCESSFUL" "$log_file"; then
        grep "BUILD SUCCESSFUL" "$log_file" >> "$result_file"
        echo "" >> "$result_file"
    fi
    
    # Extract test duration
    if grep -q "Time:" "$log_file"; then
        grep "Time:" "$log_file" | tail -1 >> "$result_file"
        echo "" >> "$result_file"
    fi
}

# Step 1: Backup baseline profile and test WITHOUT it
echo -e "${YELLOW}📊 Step 1: Testing WITHOUT baseline profile${NC}"

if [ -f "$BASELINE_PROFILE" ]; then
    cp "$BASELINE_PROFILE" "$BACKUP_PROFILE"
    rm "$BASELINE_PROFILE"
    echo "✓ Baseline profile backed up and removed"
fi

run_benchmark "WITHOUT baseline profile" "$RESULTS_DIR/without_baseline_raw.log"
extract_metrics "$RESULTS_DIR/without_baseline_raw.log" "$RESULTS_DIR/without_baseline_metrics.txt"

# Step 2: Restore baseline profile and test WITH it
echo -e "${YELLOW}📊 Step 2: Testing WITH baseline profile${NC}"

if [ -f "$BACKUP_PROFILE" ]; then
    cp "$BACKUP_PROFILE" "$BASELINE_PROFILE"
    echo "✓ Baseline profile restored"
fi

run_benchmark "WITH baseline profile" "$RESULTS_DIR/with_baseline_raw.log"
extract_metrics "$RESULTS_DIR/with_baseline_raw.log" "$RESULTS_DIR/with_baseline_metrics.txt"

# Step 3: Generate comparison report
echo -e "${YELLOW}📊 Step 3: Generating comparison report${NC}"

REPORT_FILE="$RESULTS_DIR/performance_comparison_report.md"

cat > "$REPORT_FILE" << EOF
# Performance Comparison Report

Generated on: $(date)
Project: $(basename "$PROJECT_ROOT")

## Test Configuration
- Device: $(adb devices | grep -v "List" | awk '{print $1}' | head -1)
- Android Version: $(adb shell getprop ro.build.version.release)
- Test Iterations: 5 per scenario

## Results Summary

### WITHOUT Baseline Profile
\`\`\`
$(cat "$RESULTS_DIR/without_baseline_metrics.txt")
\`\`\`

### WITH Baseline Profile
\`\`\`
$(cat "$RESULTS_DIR/with_baseline_metrics.txt")
\`\`\`

## Analysis

EOF

# Try to extract and compare startup times
WITHOUT_STARTUP=$(grep -o "median [0-9.]*" "$RESULTS_DIR/without_baseline_metrics.txt" 2>/dev/null | grep -o "[0-9.]*" | head -1)
WITH_STARTUP=$(grep -o "median [0-9.]*" "$RESULTS_DIR/with_baseline_metrics.txt" 2>/dev/null | grep -o "[0-9.]*" | head -1)

if [ ! -z "$WITHOUT_STARTUP" ] && [ ! -z "$WITH_STARTUP" ]; then
    # Calculate improvement using bc if available, otherwise use awk
    if command -v bc >/dev/null 2>&1; then
        IMPROVEMENT=$(echo "scale=1; ($WITHOUT_STARTUP - $WITH_STARTUP) * 100 / $WITHOUT_STARTUP" | bc)
        SPEEDUP=$(echo "scale=1; $WITHOUT_STARTUP - $WITH_STARTUP" | bc)
    else
        IMPROVEMENT=$(awk "BEGIN {printf \"%.1f\", ($WITHOUT_STARTUP - $WITH_STARTUP) * 100 / $WITHOUT_STARTUP}")
        SPEEDUP=$(awk "BEGIN {printf \"%.1f\", $WITHOUT_STARTUP - $WITH_STARTUP}")
    fi
    
    cat >> "$REPORT_FILE" << EOF
### Startup Performance Comparison
- **Without baseline profile**: ${WITHOUT_STARTUP}ms
- **With baseline profile**: ${WITH_STARTUP}ms
- **Improvement**: ${SPEEDUP}ms faster (${IMPROVEMENT}% improvement)

EOF
fi

cat >> "$REPORT_FILE" << EOF
## Files Generated
- Raw logs: \`$RESULTS_DIR/*_raw.log\`
- Extracted metrics: \`$RESULTS_DIR/*_metrics.txt\`
- This report: \`$REPORT_FILE\`

## Next Steps
1. Review detailed logs for additional insights
2. Run UI performance benchmarks: \`./gradlew :macrobenchmark:connectedBenchmarkAndroidTest\`
3. Test on different devices/Android versions
4. Integrate into CI/CD pipeline for continuous monitoring

---
*Generated by Android Performance Comparison Script*
EOF

# Display results
echo -e "${GREEN}🎉 Performance comparison complete!${NC}"
echo ""
echo -e "${BLUE}📋 Results Summary:${NC}"

if [ ! -z "$WITHOUT_STARTUP" ] && [ ! -z "$WITH_STARTUP" ]; then
    echo "Without baseline profile: ${WITHOUT_STARTUP}ms"
    echo "With baseline profile: ${WITH_STARTUP}ms"
    echo -e "${GREEN}Improvement: ${SPEEDUP}ms faster (${IMPROVEMENT}% improvement)${NC}"
else
    echo "Could not extract numeric results - check detailed report"
fi

echo ""
echo -e "${BLUE}📁 Generated files:${NC}"
echo "- Comparison report: $REPORT_FILE"
echo "- Results directory: $RESULTS_DIR/"
echo ""
echo -e "${YELLOW}💡 Tip: Open $REPORT_FILE to see the complete analysis${NC}"

# Optional: Open report if on macOS
if [[ "$OSTYPE" == "darwin"* ]]; then
    read -p "Open report in default viewer? (y/N): " -n 1 -r
    echo
    if [[ $REPLY =~ ^[Yy]$ ]]; then
        open "$REPORT_FILE"
    fi
fi