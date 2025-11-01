# 🧪 Test Projects Guide

This directory contains test projects to validate the Gradle 9 Migration Helper functionality.

## 📁 Test Projects Overview

### 1. **simple-project/** 
**Complexity**: Basic  
**Purpose**: Test common deprecated configurations and basic issues

**Path to use**: 
```
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/simple-project
```

**Expected Issues**: 8-10
- Deprecated configurations (compile, runtime, testCompile, testRuntime)
- Task left-shift operator
- Archive properties
- Deprecated properties
- Old Gradle version

**Test Scenarios**:
- ✅ Basic issue detection
- ✅ Auto-fix for deprecated configurations
- ✅ Archive property migration
- ✅ Task syntax updates

---

### 2. **multi-module-project/**
**Complexity**: Intermediate  
**Purpose**: Test multi-module project support and module-specific issues

**Path to use**:
```
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/multi-module-project
```

**Modules**: 3 (core, web, api)  
**Expected Issues**: 15-20

**Test Scenarios**:
- ✅ Multi-module detection
- ✅ Module-specific issue tracking
- ✅ Cross-module dependency handling
- ✅ Batch fixing across modules
- ✅ Module filtering

---

### 3. **advanced-issues-project/**
**Complexity**: Advanced  
**Purpose**: Test complex issues including Convention API, dynamic properties, and deprecated methods

**Path to use**:
```
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/advanced-issues-project
```

**Expected Issues**: 12-15

**Test Scenarios**:
- ✅ Convention API detection
- ✅ Dynamic properties detection
- ✅ Deprecated method detection
- ✅ Legacy buildscript detection
- ✅ Complex archive property patterns
- ✅ SourceSet output issues

---

### 4. **clean-project/**
**Complexity**: Basic  
**Purpose**: Test that clean projects produce no false positives

**Path to use**:
```
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/clean-project
```

**Expected Issues**: 0  
**Expected Message**: "No issues found! Your project is ready for Gradle 9."

**Test Scenarios**:
- ✅ No false positives
- ✅ Correct identification of clean code
- ✅ Proper Gradle 8.5+ recognition

---

## 🧪 Testing Workflow

### Step 1: Start the Application
```bash
cd /Users/zeji/Documents/hobby/gradle-version-migration-tool
./gradlew libertyRun
```

### Step 2: Open Browser
Navigate to: http://localhost:9080

### Step 3: Test Each Project

#### Test 1: Simple Project
1. Enter path: `/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/simple-project`
2. Click "Analyze Project"
3. Verify 8-10 issues detected
4. Check that critical issues are highlighted
5. Click "Fix All Auto-Fixable Issues"
6. Verify success messages
7. Check backup files created

#### Test 2: Multi-Module Project
1. Enter path: `/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/multi-module-project`
2. Click "Analyze Project"
3. Verify modules detected: core, web, api
4. Verify 15-20 issues across all modules
5. Test filtering by module
6. Fix selected issues from different modules
7. Verify module-specific fixes

#### Test 3: Advanced Issues Project
1. Enter path: `/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/advanced-issues-project`
2. Click "Analyze Project"
3. Verify complex issues detected:
   - Convention API
   - Dynamic properties
   - Deprecated methods
4. Note which issues are NOT auto-fixable
5. Fix auto-fixable issues
6. Verify manual review suggestions for others

#### Test 4: Clean Project
1. Enter path: `/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/clean-project`
2. Click "Analyze Project"
3. Verify NO issues detected
4. Verify success message displayed
5. Confirm no false positives

---

## 🎯 Test Checklist

### Functional Tests

#### Issue Detection
- [ ] Detects deprecated configurations
- [ ] Detects deprecated API usage
- [ ] Detects archive property issues
- [ ] Detects task left-shift operator
- [ ] Detects deprecated methods
- [ ] Detects SourceSet output issues
- [ ] Detects dynamic properties
- [ ] Detects legacy buildscript
- [ ] Detects old Gradle version
- [ ] Correctly identifies clean projects

#### Multi-Module Support
- [ ] Detects all modules
- [ ] Shows module count
- [ ] Lists module names
- [ ] Tracks issues per module
- [ ] Allows module filtering
- [ ] Fixes issues across modules

#### Auto-Fix Functionality
- [ ] Creates backups before fixing
- [ ] Fixes deprecated configurations
- [ ] Fixes archive properties
- [ ] Fixes task syntax
- [ ] Fixes deprecated methods
- [ ] Fixes SourceSet output
- [ ] Handles batch fixes
- [ ] Reports success/failure correctly

#### UI/UX
- [ ] Project path validation works
- [ ] Loading indicator shows during analysis
- [ ] Statistics display correctly
- [ ] Issue cards render properly
- [ ] Filters work correctly
- [ ] Code comparison displays
- [ ] Fix buttons are enabled/disabled correctly
- [ ] Results display properly
- [ ] Error messages are clear

#### Session Management
- [ ] Maintains project state
- [ ] Handles multiple analyses
- [ ] Clears old data correctly
- [ ] Session timeout works

---

## 🐛 Known Test Scenarios

### Edge Cases to Test

1. **Invalid Path**
   - Enter: `/invalid/path/does/not/exist`
   - Expected: Error message

2. **Non-Gradle Project**
   - Enter path to non-Gradle directory
   - Expected: Error or no issues

3. **Empty Project**
   - Create empty directory
   - Expected: No build.gradle found error

4. **Permission Issues**
   - Test with read-only directory
   - Expected: Appropriate error message

5. **Very Large Project**
   - Test with project having 100+ files
   - Expected: Completes within reasonable time

---

## 📊 Expected Results Summary

| Project | Issues | Critical | Auto-Fix | Modules |
|---------|--------|----------|----------|---------|
| simple-project | 8-10 | 5 | 8-10 | 0 |
| multi-module-project | 15-20 | 10+ | 15+ | 3 |
| advanced-issues-project | 12-15 | 4-5 | 10-12 | 0 |
| clean-project | 0 | 0 | 0 | 0 |

---

## 🔄 After Testing

### Verify Fixes Applied

For each test project, after fixing:

```bash
cd development/test/simple-project
cat build.gradle
# Verify changes were made

# Check backups exist
ls -la *.backup.*

# Restore if needed
cp build.gradle.backup.* build.gradle
```

### Reset Test Projects

To reset all test projects to original state:

```bash
cd development/test
git checkout simple-project/build.gradle
git checkout multi-module-project/*/build.gradle
git checkout advanced-issues-project/build.gradle
```

---

## 📝 Test Report Template

After testing, document results:

```markdown
## Test Report - [Date]

### Test Environment
- OS: macOS
- Java Version: 17
- Gradle Version: 8.5
- Server: OpenLiberty

### Test Results

#### Simple Project
- Issues Detected: X
- Auto-Fixed: X
- Status: ✅ Pass / ❌ Fail
- Notes: 

#### Multi-Module Project
- Issues Detected: X
- Modules Found: X
- Auto-Fixed: X
- Status: ✅ Pass / ❌ Fail
- Notes:

#### Advanced Issues Project
- Issues Detected: X
- Auto-Fixed: X
- Manual Review: X
- Status: ✅ Pass / ❌ Fail
- Notes:

#### Clean Project
- Issues Detected: X (should be 0)
- Status: ✅ Pass / ❌ Fail
- Notes:

### Issues Found
1. [Issue description]
2. [Issue description]

### Recommendations
1. [Recommendation]
2. [Recommendation]
```

---

## 🚀 Quick Test Commands

```bash
# Start server
./gradlew libertyRun

# In browser, test paths:
# Simple:
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/simple-project

# Multi-module:
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/multi-module-project

# Advanced:
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/advanced-issues-project

# Clean:
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/clean-project
```

---

**Happy Testing!** 🧪✨
