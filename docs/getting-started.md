# 🚀 Getting Started

Get up and running with Gradle 9 Migration Helper in 5 minutes.

---

## System Requirements

### Minimum Requirements
- **Java**: 17 or higher
- **Gradle**: 8.5+ (included via wrapper)
- **Memory**: 2GB RAM
- **Disk Space**: 500MB

### Operating System
- ✅ macOS
- ✅ Linux
- ✅ Windows

### Browser (for Web UI)
- Chrome/Edge 90+
- Firefox 88+
- Safari 14+

---

## Installation

### 1. Download or Clone

```bash
git clone https://github.com/your-repo/gradle-migration-helper.git
cd gradle-migration-helper
```

### 2. Verify Java Version

```bash
java -version
# Should show Java 17 or higher
```

### 3. Build the Project

```bash
./gradlew build
```

---

## Quick Start

### Option 1: Web UI (Recommended)

**Step 1: Start the server**
```bash
./gradlew libertyRun
```

Wait for the message: `The server is ready to run a smarter planet`

**Step 2: Open your browser**
```
http://localhost:9080
```

**Step 3: Analyze your project**
1. Enter the **absolute path** to your Gradle project
   - Example: `/Users/you/my-gradle-project`
2. Click **"Analyze Project"**
3. Review detected issues
4. Click **"Fix"** buttons to apply automatic fixes

**Step 4: Verify the changes**
- Check the modified files
- Backup files are created automatically (`.backup.*`)
- Run your build: `./gradlew build`

**Step 5: Stop the server**
- Press `Ctrl+C` in the terminal

---

### Option 2: REST API

**Step 1: Start the server**
```bash
./gradlew libertyRun
```

**Step 2: Analyze a project**
```bash
curl -X POST http://localhost:9080/api/analyze \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "projectPath=/absolute/path/to/your/project"
```

**Step 3: Review the response**
The response includes:
- List of detected issues
- Issue IDs (needed for fixing)
- Severity levels
- Auto-fix availability

**Step 4: Fix issues**
```bash
curl -X POST http://localhost:9080/api/fix \
  -H "Content-Type: application/json" \
  -d '{
    "issueIds": ["<uuid-from-step-2>", "<uuid-from-step-2>"]
  }'
```

---

## Your First Analysis

Let's analyze a sample project step-by-step:

### 1. Create a Test Project

```bash
mkdir ~/test-gradle-project
cd ~/test-gradle-project

# Create a simple build.gradle with deprecated syntax
cat > build.gradle << 'EOF'
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0.0'

dependencies {
    compile 'com.google.guava:guava:30.0-jre'
    testCompile 'junit:junit:4.13.2'
}
EOF

# Create gradle wrapper
gradle wrapper --gradle-version 7.6
```

### 2. Analyze the Test Project

**Using Web UI:**
1. Open http://localhost:9080
2. Enter: `/Users/<your-username>/test-gradle-project`
3. Click "Analyze Project"

**Using API:**
```bash
curl -X POST http://localhost:9080/api/analyze \
  -d "projectPath=$HOME/test-gradle-project"
```

### 3. Expected Results

You should see:
- ✅ 2 critical issues detected
- ✅ `compile` → should be `implementation`
- ✅ `testCompile` → should be `testImplementation`
- ✅ Gradle version 7.6 → should be 9.0+

### 4. Apply Fixes

Click the "Fix" buttons or use the API with issue IDs.

### 5. Verify

```bash
cd ~/test-gradle-project
cat build.gradle
# Should now show 'implementation' and 'testImplementation'

# Check backups
ls -la *.backup.*
```

---

## Understanding the Output

### Issue Card (Web UI)

```
┌─────────────────────────────────────────────┐
│ ⚠️ Deprecated Configuration Usage            │
│ Severity: CRITICAL | Auto-Fix: Yes         │
├─────────────────────────────────────────────┤
│ File: build.gradle (line 10)               │
│                                             │
│ Current:  compile 'lib:1.0'                │
│ Suggested: implementation 'lib:1.0'        │
│                                             │
│ [Fix Automatically]                         │
└─────────────────────────────────────────────┘
```

### JSON Response (API)

```json
{
  "id": "uuid-1234",
  "type": "DEPRECATED_CONFIGURATIONS",
  "severity": "CRITICAL",
  "title": "Deprecated Configuration Usage",
  "filePath": "/path/to/build.gradle",
  "lineNumber": 10,
  "currentCode": "compile 'lib:1.0'",
  "suggestedFix": "implementation 'lib:1.0'",
  "autoFixable": true
}
```

---

## Common Issues

### "Project path is required"
❌ You didn't enter a path
✅ Enter the absolute path to your project

### "Not a valid Gradle project"
❌ Missing `build.gradle` or `settings.gradle`
✅ Make sure your project has Gradle build files

### "Project path must be absolute"
❌ Used relative path like `../my-project`
✅ Use absolute path like `/Users/you/my-project`

### Server won't start
❌ Port 9080 already in use
✅ Stop other applications using that port

---

## Next Steps

✅ **Analyze your real project** - Try it on your actual Gradle project

✅ **Learn more patterns** - See [Detection Patterns](patterns.md)

✅ **Explore the API** - Read [API Reference](api-reference.md)

✅ **Check examples** - Browse [Examples](examples.md)

---

## Backup Safety

**Important**: The tool creates backups before making changes!

### Backup Format
```
build.gradle                      # Original
build.gradle.backup.1699234567890 # Backup with timestamp
```

### Restore a Backup
```bash
# List backups
ls -la *.backup.*

# Restore
mv build.gradle.backup.1699234567890 build.gradle
```

### Clean Up Backups
```bash
# Remove all backups in current directory
find . -name "*.backup.*" -type f -delete
```

---

**Ready to dive deeper?** → [User Guide](user-guide.md)

**Need help?** → [Troubleshooting](troubleshooting.md)
