# 🧪 Test Projects Directory

This directory contains comprehensive test projects for validating the Gradle 9 Migration Helper.

## 📂 Directory Structure

```
test/
├── README.md (this file)
├── TEST_GUIDE.md (detailed testing instructions)
├── QUICK_TEST_PATHS.txt (copy-paste ready paths)
├── simple-project/ (basic issues)
├── multi-module-project/ (3 modules)
├── advanced-issues-project/ (complex issues)
├── clean-project/ (no issues)
└── mixed-scenarios-project/ (partial migration)
```

## 🎯 Quick Start

### 1. Ensure Server is Running
```bash
cd /Users/zeji/Documents/hobby/gradle-version-migration-tool
./gradlew libertyRun
```

### 2. Open Application
Navigate to: **http://localhost:9080**

### 3. Test Projects (Copy These Paths)

#### Simple Project
```
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/simple-project
```
**Expected**: 8-10 issues

#### Multi-Module Project
```
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/multi-module-project
```
**Expected**: 15-20 issues, 3 modules

#### Advanced Issues Project
```
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/advanced-issues-project
```
**Expected**: 12-15 issues

#### Clean Project
```
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/clean-project
```
**Expected**: 0 issues

#### Mixed Scenarios Project
```
/Users/zeji/Documents/hobby/gradle-version-migration-tool/development/test/mixed-scenarios-project
```
**Expected**: 6-8 issues

## 📊 Test Projects Summary

| Project | Complexity | Issues | Modules | Purpose |
|---------|-----------|--------|---------|---------|
| simple-project | Basic | 8-10 | 0 | Basic functionality |
| multi-module-project | Intermediate | 15-20 | 3 | Multi-module support |
| advanced-issues-project | Advanced | 12-15 | 0 | Complex patterns |
| clean-project | Basic | 0 | 0 | False positive check |
| mixed-scenarios-project | Intermediate | 6-8 | 0 | Partial migration |

## 🔍 What Each Project Tests

### simple-project
- ✅ Deprecated configurations (compile, runtime, etc.)
- ✅ Task left-shift operator
- ✅ Archive properties
- ✅ Deprecated properties
- ✅ Old Gradle version

### multi-module-project
- ✅ Module detection
- ✅ Cross-module issues
- ✅ Module-specific tracking
- ✅ Batch fixing across modules
- ✅ Module filtering

### advanced-issues-project
- ✅ Convention API usage
- ✅ Dynamic properties
- ✅ Deprecated methods
- ✅ Legacy buildscript
- ✅ SourceSet output issues
- ✅ Complex patterns

### clean-project
- ✅ No false positives
- ✅ Modern syntax recognition
- ✅ Gradle 8.5+ compatibility
- ✅ Proper clean project handling

### mixed-scenarios-project
- ✅ Mixed old/new syntax
- ✅ Partial migration detection
- ✅ Precision in issue identification
- ✅ No false positives on correct code

## 📖 Documentation

- **TEST_GUIDE.md** - Comprehensive testing instructions
- **QUICK_TEST_PATHS.txt** - Quick reference for copy-paste
- Each project has its own README.md with details

## ✅ Testing Checklist

Use this checklist when testing:

### Functional Tests
- [ ] All 5 projects analyzed successfully
- [ ] Issue counts match expectations
- [ ] Module detection works (multi-module project)
- [ ] Auto-fix functionality works
- [ ] Backups created before fixes
- [ ] Clean project shows 0 issues
- [ ] Mixed scenarios handled correctly

### UI/UX Tests
- [ ] Project path validation
- [ ] Loading indicators
- [ ] Statistics display
- [ ] Issue filtering
- [ ] Code comparison view
- [ ] Fix results display
- [ ] Error handling

### Edge Cases
- [ ] Invalid paths handled
- [ ] Permission errors handled
- [ ] Large projects (multi-module)
- [ ] Empty results (clean project)
- [ ] Session management

## 🐛 Reporting Issues

If you find issues during testing:

1. Note the test project name
2. Document the expected vs actual behavior
3. Include any error messages
4. Check the server logs:
   ```bash
   tail -f build/wlp/usr/servers/gradleMigrationServer/logs/messages.log
   ```

## 🔄 Resetting Test Projects

After testing, you can reset projects to original state:

```bash
cd development/test

# Reset individual project
git checkout simple-project/build.gradle

# Or reset all
git checkout */build.gradle
git checkout */*/build.gradle
```

## 📝 Test Report

After completing tests, document your findings:

```markdown
## Test Session - [Date]

### Environment
- Java: [version]
- Gradle: [version]
- OS: [operating system]

### Results
- simple-project: ✅ Pass / ❌ Fail
- multi-module-project: ✅ Pass / ❌ Fail
- advanced-issues-project: ✅ Pass / ❌ Fail
- clean-project: ✅ Pass / ❌ Fail
- mixed-scenarios-project: ✅ Pass / ❌ Fail

### Issues Found
[List any issues]

### Notes
[Additional observations]
```

## 🚀 Next Steps

1. Start with **simple-project** for basic validation
2. Test **clean-project** to verify no false positives
3. Try **multi-module-project** for complex scenarios
4. Test **mixed-scenarios-project** for real-world cases
5. Finally, test **advanced-issues-project** for edge cases

## 💡 Tips

- Read each project's README.md before testing
- Test one project at a time
- Verify backups are created
- Check that fixes are applied correctly
- Test both individual and batch fixing
- Try different filter combinations

---

**Happy Testing!** 🧪✨

For detailed instructions, see **TEST_GUIDE.md**
