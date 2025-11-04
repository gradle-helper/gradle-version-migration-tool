# 📖 User Guide

Complete guide to using the Gradle 9 Migration Helper.

---

## Table of Contents

- [Using the Web UI](#using-the-web-ui)
- [Using the API](#using-the-api)
- [Understanding Issues](#understanding-issues)
- [Applying Fixes](#applying-fixes)
- [Working with Backups](#working-with-backups)
- [Multi-Module Projects](#multi-module-projects)
- [Best Practices](#best-practices)
- [FAQ](#faq)

---

## Using the Web UI

### Starting the Server

```bash
cd /path/to/gradle-migration-helper
./gradlew libertyRun
```

Wait for: `The server is ready to run a smarter planet`

### Accessing the Interface

Open your browser to: **http://localhost:9080**

### Analyzing a Project

1. **Enter Project Path**
   - Must be an absolute path
   - Example: `/Users/you/my-project`
   - Not: `../my-project` ❌

2. **Click "Analyze Project"**
   - Tool scans all `.gradle` and `.gradle.kts` files
   - Takes 1-5 seconds for most projects
   - Progress indicator shows status

3. **Review Results**
   - Total issues found
   - Critical vs. medium vs. low
   - Auto-fixable count
   - Detailed issue list

### Understanding the Results

**Project Summary Card:**
```
┌───────────────────────────────────┐
│  📦 my-gradle-project             │
│  📍 /Users/you/my-project         │
│  🏷️  Gradle 7.6                   │
│  📚 Multi-Module: Yes (3 modules) │
├───────────────────────────────────┤
│  ⚠️  15 issues found               │
│  🔴 5 Critical                     │
│  🟡 10 Medium                      │
│  ✅ 12 auto-fixable                │
└───────────────────────────────────┘
```

**Issue List:**
- Grouped by severity
- Shows file and line number
- Preview of current code
- Suggested fix
- "Fix" button for auto-fixable issues

### Applying Fixes

**Individual Fix:**
1. Click "Fix" button on any auto-fixable issue
2. Confirmation dialog appears
3. Click "Confirm"
4. File is updated, backup created

**Batch Fix:**
1. Select multiple issues with checkboxes
2. Click "Fix Selected" at the top
3. All selected issues are fixed together

### Stopping the Server

Press `Ctrl+C` in the terminal where the server is running.

---

## Using the API

### Endpoints

| Endpoint | Method | Purpose |
|----------|--------|---------|
| `/api/analyze` | POST | Analyze a project |
| `/api/analyze` | GET | Get cached analysis |
| `/api/fix` | POST | Apply fixes |

### Analyzing a Project

**Request:**
```bash
curl -X POST http://localhost:9080/api/analyze \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "projectPath=/absolute/path/to/project"
```

**Response:**
```json
{
  "projectName": "my-project",
  "projectPath": "/absolute/path/to/project",
  "currentGradleVersion": "7.6",
  "multiModule": true,
  "modules": ["core", "api", "web"],
  "totalIssues": 15,
  "criticalIssues": 5,
  "autoFixableIssues": 12,
  "issues": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "type": "DEPRECATED_CONFIGURATIONS",
      "severity": "CRITICAL",
      "title": "Deprecated Configuration Usage",
      "filePath": "/path/to/build.gradle",
      "lineNumber": 25,
      "currentCode": "compile 'com.google.guava:guava:30.0-jre'",
      "suggestedFix": "implementation 'com.google.guava:guava:30.0-jre'",
      "autoFixable": true,
      "explanation": "The 'compile' configuration is removed in Gradle 9...",
      "module": "core"
    }
  ]
}
```

### Retrieving Cached Analysis

**Request:**
```bash
curl http://localhost:9080/api/analyze
```

Returns the same format as POST, but from session cache.

### Applying Fixes

**Request:**
```bash
curl -X POST http://localhost:9080/api/fix \
  -H "Content-Type: application/json" \
  -d '{
    "issueIds": [
      "550e8400-e29b-41d4-a716-446655440000",
      "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
    ]
  }'
```

**Response:**
```json
{
  "totalProcessed": 2,
  "successCount": 2,
  "failureCount": 0,
  "results": [
    {
      "issueId": "550e8400-e29b-41d4-a716-446655440000",
      "filePath": "/path/to/build.gradle",
      "success": true,
      "message": "Successfully applied fix to build.gradle",
      "backupPath": "/path/to/build.gradle.backup.1699234567890"
    }
  ]
}
```

### Error Responses

**400 Bad Request:**
```json
{
  "error": "Project path is required"
}
```

**404 Not Found:**
```json
{
  "error": "No project analysis found in session"
}
```

**500 Internal Server Error:**
```json
{
  "error": "Error analyzing project: [details]"
}
```

---

## Understanding Issues

### Severity Levels

**🔴 CRITICAL** - Will break builds in Gradle 9
- Must be fixed before upgrading
- Examples: `compile` → `implementation`, deprecated APIs

**🟡 HIGH** - Will cause failures during execution
- Should be fixed before upgrading
- Examples: Archive properties, deprecated methods

**🟠 MEDIUM** - May cause problems
- Recommended to fix
- Examples: Dynamic properties, buildscript blocks

**🔵 LOW** - Best practices
- Nice to fix
- Examples: Performance optimizations

### Auto-Fixable vs Manual

**✅ Auto-Fixable (9 patterns)**
- Simple string replacements
- Safe transformations
- Click "Fix" button to apply

**❌ Manual Fix Required (3 patterns)**
- Requires understanding context
- Multiple possible solutions
- Need developer decision
- Tool provides guidance only

See [Detection Patterns](patterns.md) for complete list.

---

## Applying Fixes

### Before Applying Fixes

1. **Commit your changes**
   ```bash
   git add .
   git commit -m "Before Gradle 9 migration"
   ```

2. **Review the suggestions**
   - Read the explanation for each issue
   - Understand why the change is needed
   - Check if it makes sense for your code

3. **Start with critical issues**
   - Fix critical issues first
   - Test after each fix
   - Move to lower severity issues

### Applying Fixes Safely

**Step 1: Fix one issue at a time**
```bash
# Analyze
curl -X POST http://localhost:9080/api/analyze \
  -d "projectPath=/path/to/project"

# Fix single issue
curl -X POST http://localhost:9080/api/fix \
  -H "Content-Type: application/json" \
  -d '{"issueIds": ["<single-uuid>"]}'
```

**Step 2: Test immediately**
```bash
cd /path/to/project
./gradlew build
```

**Step 3: If successful, continue**
- Fix next issue
- Test again
- Repeat

**Step 4: If fails, rollback**
```bash
mv build.gradle.backup.1699234567890 build.gradle
```

### Batch Fixing

For projects with many issues:

```bash
# Get all auto-fixable issue IDs
curl http://localhost:9080/api/analyze | \
  jq -r '.issues[] | select(.autoFixable == true) | .id' > issue-ids.txt

# Create JSON array
jq -R -s 'split("\n") | map(select(length > 0))' issue-ids.txt > fix-request.json

# Apply all fixes
curl -X POST http://localhost:9080/api/fix \
  -H "Content-Type: application/json" \
  -d @fix-request.json
```

**⚠️ Warning**: Always test after batch fixing!

---

## Working with Backups

### Backup File Format

```
<original-filename>.backup.<timestamp>
```

**Example:**
```
build.gradle                      # Current
build.gradle.backup.1699234567890 # Backup
```

### Listing Backups

```bash
# In project directory
ls -la *.backup.*

# Recursive
find . -name "*.backup.*" -type f
```

### Restoring a Backup

**Single file:**
```bash
mv build.gradle.backup.1699234567890 build.gradle
```

**Multiple files:**
```bash
# Restore all backups from today
find . -name "*.backup.*" -type f -newermt "today" | while read backup; do
  original="${backup%.backup.*}"
  mv "$backup" "$original"
done
```

### Cleaning Up Backups

**Remove all backups:**
```bash
find . -name "*.backup.*" -type f -delete
```

**Remove old backups (7+ days):**
```bash
find . -name "*.backup.*" -type f -mtime +7 -delete
```

**Keep latest backup only:**
```bash
find . -name "build.gradle.backup.*" | sort -r | tail -n +2 | xargs rm
```

---

## Multi-Module Projects

### How It Works

The tool automatically:
1. Detects `settings.gradle` or `settings.gradle.kts`
2. Extracts module names
3. Scans each module's `build.gradle`
4. Tags issues with module name

### Example Structure

```
my-project/
├── settings.gradle
├── build.gradle           # Root
├── core/
│   └── build.gradle       # Module 1
├── api/
│   └── build.gradle       # Module 2
└── web/
    └── build.gradle       # Module 3
```

### Analysis Output

Issues show which module they're from:

```json
{
  "module": "core",
  "filePath": "/path/to/project/core/build.gradle",
  "currentCode": "compile 'lib:1.0'"
}
```

### Fixing Multi-Module Projects

**Option 1: Fix all modules at once**
- Analyze entire project
- Apply all fixes
- Test all modules

**Option 2: Fix module by module**
1. Fix root `build.gradle` first
2. Test: `./gradlew build`
3. Fix module 1, test
4. Fix module 2, test
5. Continue...

---

## Best Practices

### Before Migration

1. ✅ **Backup everything**
   ```bash
   git add .
   git commit -m "Pre-migration snapshot"
   ```

2. ✅ **Review current build**
   ```bash
   ./gradlew clean build
   # Should pass before migration
   ```

3. ✅ **Check dependencies**
   - Update plugins to latest versions
   - Check for Gradle 9 compatibility

### During Migration

1. ✅ **Fix critical issues first**
   - Sort by severity
   - Start with CRITICAL
   - Move down severity levels

2. ✅ **Test frequently**
   ```bash
   ./gradlew build
   # After each fix or small batch
   ```

3. ✅ **Read the explanations**
   - Understand why each change is needed
   - Don't blindly apply fixes

4. ✅ **Keep backups until confirmed**
   - Don't delete `.backup.*` files yet
   - Wait until everything works

### After Migration

1. ✅ **Run full test suite**
   ```bash
   ./gradlew clean build test
   ```

2. ✅ **Update Gradle wrapper**
   ```bash
   ./gradlew wrapper --gradle-version 9.0
   ```

3. ✅ **Commit the changes**
   ```bash
   git add .
   git commit -m "Migrated to Gradle 9"
   ```

4. ✅ **Clean up backups**
   ```bash
   find . -name "*.backup.*" -delete
   ```

---

## FAQ

### Q: Can I use relative paths?
**A:** No, you must use absolute paths like `/Users/you/project`.

### Q: Does it modify my code automatically?
**A:** No, it only modifies when you click "Fix" or call `/api/fix`. Analysis is read-only.

### Q: What if I don't like a fix?
**A:** Restore from backup: `mv file.backup.* file`

### Q: Can multiple people use it at once?
**A:** No, it's designed for single-user use. Each user should run their own instance.

### Q: Does it work with Kotlin DSL?
**A:** Basic support for `.gradle.kts` files, but not extensively tested.

### Q: What if the build fails after fixing?
**A:** Restore from backups and review the fix manually. Some issues may need custom solutions.

### Q: Can I run this in CI/CD?
**A:** Yes! Use the REST API in your pipeline scripts.

### Q: Does it handle custom configurations?
**A:** It detects standard patterns. Custom configurations may need manual review.

### Q: What about plugins?
**A:** Checks for deprecated plugin syntax. Plugin-specific issues may need manual fixes.

### Q: How do I know if all issues are fixed?
**A:** Re-run analysis. It should show 0 critical issues.

---

## Next Steps

- [API Reference](api-reference.md) - Detailed API documentation
- [Detection Patterns](patterns.md) - What each pattern detects
- [Examples](examples.md) - Real-world migration examples
- [Troubleshooting](troubleshooting.md) - Common problems

---

**Need help?** Check [Troubleshooting](troubleshooting.md) or review [Examples](examples.md).
