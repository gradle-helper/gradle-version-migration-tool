# 📚 Examples

Real-world migration examples for different project types.

---

## Simple Java Project

### Before Migration

```groovy
// build.gradle
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0.0'

dependencies {
    compile 'com.google.guava:guava:30.0-jre'
    compile 'org.apache.commons:commons-lang3:3.12.0'
    testCompile 'junit:junit:4.13.2'
    runtime 'mysql:mysql-connector-java:8.0.28'
}

task myTask << {
    println 'Running task'
}
```

### Analysis Results

- 4 critical issues (configurations)
- 1 high issue (task operator)
- All auto-fixable

### After Auto-Fix

```groovy
// build.gradle
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0.0'

dependencies {
    implementation 'com.google.guava:guava:30.0-jre'
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    testImplementation 'junit:junit:4.13.2'
    runtimeOnly 'mysql:mysql-connector-java:8.0.28'
}

task myTask {
    doLast {
        println 'Running task'
    }
}
```

---

## Spring Boot Project

### Before Migration

```groovy
// build.gradle
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'org.springframework.boot:spring-boot-gradle-plugin:2.7.0'
    }
}

apply plugin: 'java'
apply plugin: 'org.springframework.boot'

dependencies {
    compile 'org.springframework.boot:spring-boot-starter-web'
    compile 'org.springframework.boot:spring-boot-starter-data-jpa'
    testCompile 'org.springframework.boot:spring-boot-starter-test'
}
```

### Issues Found

- ✅ Deprecated configurations
- ⚠️ Legacy buildscript block (manual)
- ⚠️ Old plugin application (manual)

### After Migration

```groovy
// build.gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.1.0'  // Manual: Update version
    id 'io.spring.dependency-management' version '1.1.0'  // Manual: Add this
}

dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
}
```

**Manual Steps:**
1. Update Spring Boot to 3.x
2. Migrate from `buildscript {}` to `plugins {}`
3. Update Java to 17+

---

## Android Project

### Before Migration

```groovy
// app/build.gradle
apply plugin: 'com.android.application'

android {
    compileSdkVersion 31
    buildToolsVersion "31.0.0"
    
    defaultConfig {
        applicationId "com.example.app"
        minSdkVersion 21
        targetSdkVersion 31
    }
}

dependencies {
    compile 'androidx.appcompat:appcompat:1.4.0'
    compile 'com.google.android.material:material:1.5.0'
    testCompile 'junit:junit:4.13.2'
}
```

### After Migration

```groovy
// app/build.gradle
plugins {
    id 'com.android.application'
}

android {
    compileSdk 33  // Manual: Update SDK versions
    
    defaultConfig {
        applicationId "com.example.app"
        minSdk 21
        targetSdk 33
    }
    
    buildToolsVersion "33.0.0"  // Manual: Update
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.0'
    implementation 'com.google.android.material:material:1.8.0'
    testImplementation 'junit:junit:4.13.2'
}
```

**Manual Steps:**
1. Update Android Gradle Plugin to 8.0+
2. Update compileSdk and targetSdk
3. Update dependencies to latest versions

---

## Multi-Module Project

### Project Structure

```
my-app/
├── settings.gradle
├── build.gradle (root)
├── core/
│   └── build.gradle
├── api/
│   └── build.gradle
└── web/
    └── build.gradle
```

### Root build.gradle - Before

```groovy
buildscript {
    repositories {
        mavenCentral()
    }
}

subprojects {
    apply plugin: 'java'
    
    repositories {
        mavenCentral()
    }
    
    dependencies {
        testCompile 'junit:junit:4.13.2'
    }
}
```

### Root build.gradle - After

```groovy
plugins {
    id 'java'
}

subprojects {
    apply plugin: 'java'
    
    repositories {
        mavenCentral()
    }
    
    dependencies {
        testImplementation 'junit:junit:4.13.2'
    }
}
```

### core/build.gradle - Before/After

```groovy
// Before
dependencies {
    compile 'com.google.guava:guava:30.0-jre'
}

// After
dependencies {
    implementation 'com.google.guava:guava:30.0-jre'
}
```

### Migration Strategy

1. **Analyze entire project**
   ```bash
   curl -X POST http://localhost:9080/api/analyze \
     -d "projectPath=/path/to/my-app"
   ```

2. **Fix root first**
   - Issues in root build.gradle
   - Test: `./gradlew clean`

3. **Fix each module**
   - Fix core module issues
   - Test: `./gradlew :core:build`
   - Repeat for other modules

4. **Test everything**
   ```bash
   ./gradlew clean build
   ```

---

## Gradle Plugin Project

### Before Migration

```groovy
// build.gradle
plugins {
    id 'groovy'
    id 'java-gradle-plugin'
}

dependencies {
    compile gradleApi()
    compile localGroovy()
    testCompile 'junit:junit:4.13.2'
}

jar {
    archiveName = 'my-plugin.jar'
}
```

### After Migration

```groovy
// build.gradle
plugins {
    id 'groovy'
    id 'java-gradle-plugin'
}

dependencies {
    implementation gradleApi()
    implementation localGroovy()
    testImplementation 'junit:junit:4.13.2'
}

jar {
    archiveFileName.set('my-plugin.jar')
}
```

---

## CI/CD Integration Example

### GitHub Actions Workflow

```yaml
name: Gradle 9 Migration Check

on: [push, pull_request]

jobs:
  migrate:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v3
      
      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'
      
      - name: Start Migration Helper
        run: |
          git clone https://github.com/your-repo/gradle-migration-helper.git
          cd gradle-migration-helper
          ./gradlew libertyRun &
          sleep 30  # Wait for server
      
      - name: Analyze Project
        run: |
          response=$(curl -s -X POST http://localhost:9080/api/analyze \
            -d "projectPath=$GITHUB_WORKSPACE")
          echo "$response" | jq .
          
          # Check for critical issues
          critical=$(echo "$response" | jq '.criticalIssues')
          if [ "$critical" -gt 0 ]; then
            echo "❌ Found $critical critical issues"
            exit 1
          fi
      
      - name: Apply Fixes
        run: |
          # Get all auto-fixable issue IDs
          issue_ids=$(curl -s http://localhost:9080/api/analyze | \
            jq -r '.issues[] | select(.autoFixable == true) | .id')
          
          # Apply fixes
          curl -X POST http://localhost:9080/api/fix \
            -H "Content-Type: application/json" \
            -d "{\"issueIds\": [$(echo $issue_ids | sed 's/ /","/g' | sed 's/^/"/;s/$/"/')"]}"
      
      - name: Test Build
        run: ./gradlew clean build
```

---

## Common Patterns

### Pattern 1: Configuration Replacement

```groovy
// Find and replace pattern
compile          → implementation
testCompile      → testImplementation
runtime          → runtimeOnly
testRuntime      → testRuntimeOnly
```

### Pattern 2: Archive Properties

```groovy
// Find and replace pattern
archiveName =         → archiveFileName.set(
archiveBaseName =     → archiveBaseName.set(
archiveVersion =      → archiveVersion.set(
archiveExtension =    → archiveExtension.set(
```

### Pattern 3: Task Configuration

```groovy
// Find and replace pattern
task myTask <<     → task myTask { doLast
<<                 → { doLast
```

---

## Migration Checklist

Use this checklist for any project:

### Pre-Migration
- [ ] Commit all changes
- [ ] Verify build passes: `./gradlew clean build`
- [ ] Note current Gradle version
- [ ] Backup project

### Migration
- [ ] Analyze project with tool
- [ ] Review all detected issues
- [ ] Fix critical issues first
- [ ] Test after each fix
- [ ] Fix high priority issues
- [ ] Fix medium priority issues
- [ ] Review manual issues

### Post-Migration
- [ ] Full build: `./gradlew clean build test`
- [ ] Run application/tests
- [ ] Update Gradle wrapper to 9.0
- [ ] Update plugins to latest versions
- [ ] Clean up backup files
- [ ] Commit migration changes
- [ ] Update documentation

---

## Tips by Project Type

### Java Libraries
- Focus on dependency configurations
- Update `jar` task archive properties
- Check `sourceSet` configurations

### Web Applications
- Update server dependencies
- Check servlet API versions
- Update plugin versions

### Android Apps
- Update Android Gradle Plugin first
- Check SDK versions
- Update AndroidX dependencies

### Multi-Module Projects
- Fix root `build.gradle` first
- Test each module independently
- Watch for shared configurations

### Gradle Plugins
- Update `gradleApi()` usage
- Check task implementations
- Update plugin dependencies

---

**Need more help?** See [User Guide](user-guide.md) or [Troubleshooting](troubleshooting.md)

**Back to:** [Documentation Home](README.md)
