# 🔧 Troubleshooting

Common problems and solutions.

---

## Installation Issues

### Java Version Error

**Problem:**
```
Error: A JNI error has occurred
Unsupported major.minor version
```

**Solution:**
```bash
# Check Java version
java -version

# Should be 17 or higher
# If not, install Java 17+
```

### Build Fails

**Problem:**
```
BUILD FAILED
Could not resolve dependencies
```

**Solution:**
```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

---

## Server Issues

### Port Already in Use

**Problem:**
```
Port 9080 is already in use
```

**Solution:**
```bash
# Find process using port 9080
lsof -i :9080

# Kill the process
kill -9 <PID>

# Or use different port (edit server.xml)
```

### Server Won't Start

**Problem:**
Server hangs or crashes on startup

**Solution:**
```bash
# Check logs
tail -f build/wlp/usr/servers/defaultServer/logs/console.log

# Clean and restart
./gradlew clean libertyStop libertyRun
```

### Cannot Access UI

**Problem:**
Browser shows "Cannot connect" at localhost:9080

**Solution:**
1. Check server is running: Look for "server is ready"
2. Try `http://127.0.0.1:9080` instead
3. Check firewall settings
4. Wait longer (first start takes ~30 seconds)

---

## Analysis Issues

### "Project path is required"

**Problem:**
Empty or null path provided

**Solution:**
```bash
# Make sure to provide path
curl -X POST http://localhost:9080/api/analyze \
  -d "projectPath=/full/path/to/project"  # Don't forget this
```

### "Project path must be absolute"

**Problem:**
```
curl ... -d "projectPath=../my-project"  # ❌ Relative path
```

**Solution:**
```bash
# Use absolute path
curl ... -d "projectPath=/Users/you/my-project"  # ✅ Absolute
```

### "Not a valid Gradle project"

**Problem:**
Directory doesn't contain Gradle build files

**Solution:**
```bash
# Check for build files
ls -la /path/to/project/build.gradle
ls -la /path/to/project/settings.gradle

# Make sure at least one exists
```

### "No issues detected" but I know there are issues

**Possible Causes:**

**1. Already fixed**
```bash
# Check if already migrated
grep -r "compile " build.gradle  # Should find nothing
```

**2. Wrong directory**
```bash
# Make sure you're pointing to project root
# Not a subdirectory
```

**3. Kotlin DSL only**
```bash
# Tool has basic Kotlin DSL support
# Some patterns might not match .gradle.kts syntax
```

### Analysis is Slow

**Problem:**
Takes >30 seconds for small project

**Possible Causes:**

1. **Large number of files**
   ```bash
   # Count .gradle files
   find /path/to/project -name "*.gradle" | wc -l
   ```

2. **Network drives**
   - Don't analyze projects on network/cloud drives
   - Copy to local disk first

3. **Antivirus scanning**
   - Temporarily disable antivirus
   - Add project to exclusions

---

## Fix Issues

### "No project analysis found in session"

**Problem:**
Session expired or server restarted

**Solution:**
```bash
# Re-analyze project first
curl -X POST http://localhost:9080/api/analyze \
  -d "projectPath=/path/to/project"

# Then fix
curl -X POST http://localhost:9080/api/fix \
  -d '{"issueIds": ["..."]}'
```

### "No matching issues found"

**Problem:**
Wrong issue IDs or issues already fixed

**Solution:**
```bash
# Get current issues
curl http://localhost:9080/api/analyze | jq '.issues[].id'

# Use those IDs for fixing
```

### Fix Applied but File Unchanged

**Problem:**
API returns success but file not modified

**Possible Causes:**

**1. Wrong file path**
```bash
# Check the filePath in response
# Make sure it matches actual file location
```

**2. File permissions**
```bash
# Check if file is writable
ls -la /path/to/build.gradle

# Fix permissions
chmod 644 /path/to/build.gradle
```

**3. File locked**
- Close IDE/editor
- Make sure no other process has the file open

### Build Still Fails After Fixing

**Problem:**
Applied all fixes but `./gradlew build` fails

**Troubleshooting Steps:**

**1. Check what was fixed**
```bash
# Look at backup files
diff build.gradle build.gradle.backup.*
```

**2. Verify the changes**
```bash
# Make sure changes are correct
cat build.gradle
```

**3. Clean build**
```bash
./gradlew clean build --info
```

**4. Check for additional issues**
```bash
# Some issues need manual fixes
# Look for "autoFixable": false
curl http://localhost:9080/api/analyze | jq '.issues[] | select(.autoFixable == false)'
```

**5. Update plugins**
```groovy
// In build.gradle
plugins {
    id 'java'
    id 'com.android.application' version '8.0.0'  // Update to latest
}
```

---

## Backup Issues

### Cannot Find Backup Files

**Problem:**
Applied fix but no `.backup.*` files

**Solution:**
```bash
# Search recursively
find /path/to/project -name "*.backup.*"

# Check backup path in API response
# "backupPath": "/full/path/to/file.backup.timestamp"
```

### Backup Restoration Failed

**Problem:**
Tried to restore but file corrupted

**Solution:**
```bash
# Check backup file integrity
cat file.backup.* | head -10

# If corrupted, check git history
git log --all --full-history -- path/to/file

# Restore from git
git checkout HEAD~1 -- path/to/file
```

### Too Many Backup Files

**Problem:**
Hundreds of `.backup.*` files cluttering project

**Solution:**
```bash
# List backups by date
find . -name "*.backup.*" -ls

# Remove old backups (7+ days)
find . -name "*.backup.*" -mtime +7 -delete

# Remove all backups (after confirming fixes work)
find . -name "*.backup.*" -delete
```

---

## Web UI Issues

### UI Not Loading

**Problem:**
Blank page or "Cannot GET /"

**Solution:**
1. Make sure server is running
2. Check browser console for errors (F12)
3. Clear browser cache
4. Try incognito mode
5. Try different browser

### "Analyze" Button Not Working

**Problem:**
Click button but nothing happens

**Solution:**
1. Open browser console (F12)
2. Look for JavaScript errors
3. Check if path field is filled
4. Try refreshing page

### Issues Not Displaying

**Problem:**
Analysis complete but no issues shown

**Solution:**
```bash
# Check API directly
curl http://localhost:9080/api/analyze

# If API returns issues, it's a UI bug
# Refresh page or check browser console
```

---

## API Issues

### cURL Command Not Working

**Problem:**
```bash
curl: (6) Could not resolve host
```

**Solution:**
```bash
# Check server is running
curl http://localhost:9080

# Try 127.0.0.1
curl http://127.0.0.1:9080/api/analyze
```

### JSON Parse Error

**Problem:**
```json
{
  "error": "Unexpected token"
}
```

**Solution:**
```bash
# Make sure JSON is valid
echo '{"issueIds": ["123"]}' | jq .

# Use proper quotes
curl ... -d '{"issueIds": ["123"]}'  # Single quotes outside
```

### Session Expired

**Problem:**
"No project analysis found" after idle time

**Solution:**
- Sessions expire after 30 minutes
- Re-run analysis
- For long sessions, analyze periodically

---

## Performance Issues

### High Memory Usage

**Problem:**
Java process using >2GB RAM

**Solution:**
```bash
# Set max heap size
export JAVA_OPTS="-Xmx1024m"
./gradlew libertyRun
```

### Slow on Large Projects

**Problem:**
Project with 1000+ files takes minutes

**Workarounds:**
1. Analyze individual modules separately
2. Exclude build directories
3. Run overnight for very large projects

---

## Error Messages Explained

### "Error analyzing project: IOException"

**Cause:** File read/write error

**Solutions:**
- Check file permissions
- Ensure disk space available
- Close files in editors

### "Error applying fix: Pattern not found"

**Cause:** File changed since analysis

**Solution:**
- Re-run analysis
- Don't edit files between analyze and fix

### "Error: Failed to create backup"

**Cause:** No write permissions or disk full

**Solutions:**
```bash
# Check permissions
ls -la /path/to/file

# Check disk space
df -h
```

---

## Getting More Help

### Enable Debug Logging

```bash
./gradlew libertyRun --debug > debug.log 2>&1
```

### Check Server Logs

```bash
# Console log
tail -f build/wlp/usr/servers/defaultServer/logs/console.log

# Messages log
tail -f build/wlp/usr/servers/defaultServer/logs/messages.log
```

### Report an Issue

Include:
1. Operating system and version
2. Java version (`java -version`)
3. Full error message
4. Steps to reproduce
5. Sample build.gradle (if possible)

---

## Common Pitfalls

❌ **Using relative paths**
```bash
curl ... -d "projectPath=../my-project"
```
✅ Use absolute paths
```bash
curl ... -d "projectPath=/Users/you/my-project"
```

❌ **Not waiting for server**
```bash
./gradlew libertyRun &
curl ...  # Too fast!
```
✅ Wait for "server is ready"

❌ **Forgetting to re-analyze**
```bash
# Made manual changes
curl -X POST .../api/fix  # Uses old analysis!
```
✅ Re-analyze after changes

❌ **Batch fixing without testing**
```bash
# Fix all 50 issues at once
# Build breaks
```
✅ Fix in small batches, test each batch

---

**Still stuck?** Check [Examples](examples.md) for similar scenarios.

**Back to:** [Documentation Home](README.md) | [User Guide](user-guide.md)
