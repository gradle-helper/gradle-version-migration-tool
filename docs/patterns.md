# 🔍 Detection Patterns

What issues the tool detects and how to fix them.

---

## Overview

The tool detects **12 types** of Gradle 9 compatibility issues:
- **9 auto-fixable** ✅ (75%)
- **3 require manual fixes** ❌ (25%)

---

## Critical Issues (Must Fix)

These will break your build in Gradle 9.

### 1. Deprecated Configurations ✅ Auto-Fix

**What it detects:**
```groovy
dependencies {
    compile 'lib:1.0'        // ❌ Removed in Gradle 9
    runtime 'other:2.0'      // ❌ Removed
    testCompile 'test:1.0'   // ❌ Removed
    testRuntime 'mock:1.0'   // ❌ Removed
}
```

**Fix:**
```groovy
dependencies {
    implementation 'lib:1.0'       // ✅ Use this
    runtimeOnly 'other:2.0'        // ✅ Use this
    testImplementation 'test:1.0'  // ✅ Use this
    testRuntimeOnly 'mock:1.0'     // ✅ Use this
}
```

**Why:** Gradle 9 completely removes the old configuration names.

---

### 2. Deprecated Dependency Methods ✅ Auto-Fix

**What it detects:**
```groovy
dependencies {
    compile('lib:1.0')        // ❌ Method removed
    testCompile('test:1.0')   // ❌ Method removed
}
```

**Fix:**
```groovy
dependencies {
    implementation('lib:1.0')       // ✅ Use this
    testImplementation('test:1.0')  // ✅ Use this
}
```

**Why:** Method names changed to match configuration names.

---

### 3. Gradle Version Check ✅ Auto-Fix

**What it detects:**
```properties
# gradle-wrapper.properties
distributionUrl=...gradle-7.6-bin.zip  // ❌ Too old
```

**Fix:**
```properties
# gradle-wrapper.properties
distributionUrl=...gradle-9.0-bin.zip  // ✅ Update to 9.0+
```

**Or run:**
```bash
./gradlew wrapper --gradle-version 9.0
```

**Why:** Your project needs Gradle 9+ to use new features.

---

## High Priority Issues

These will cause runtime failures.

### 4. Deprecated Convention API ✅ Auto-Fix

**What it detects:**
```groovy
convention.getPlugin(JavaPluginConvention)  // ❌ Removed
convention['java']                          // ❌ Removed
```

**Fix:**
```groovy
extensions.getByType(JavaPluginExtension)   // ✅ Use this
project.extensions.java                     // ✅ Use this
```

**Why:** Convention API is removed, use Extensions instead.

---

### 5. Archive Task Properties ✅ Auto-Fix

**What it detects:**
```groovy
jar {
    archiveName = 'app.jar'            // ❌ Deprecated
    archiveBaseName = 'app'            // ❌ Deprecated
    archiveVersion = '1.0'             // ❌ Deprecated
}
```

**Fix:**
```groovy
jar {
    archiveFileName.set('app.jar')     // ✅ Use Property API
    archiveBaseName.set('app')         // ✅ Use Property API
    archiveVersion.set('1.0')          // ✅ Use Property API
}
```

**Why:** Direct property assignment is replaced by Property API.

---

### 6. Task Left-Shift Operator ✅ Auto-Fix

**What it detects:**
```groovy
task myTask << {           // ❌ << operator removed
    println 'Hello'
}
```

**Fix:**
```groovy
task myTask {
    doLast {               // ✅ Use doLast
        println 'Hello'
    }
}
```

**Why:** `<<` operator is removed for clarity.

---

### 7. Deprecated API Methods ✅ Auto-Fix

**What it detects:**
```groovy
task.getArchivePath()      // ❌ Removed
sourceSets.main.output.getClassesDir()  // ❌ Removed
```

**Fix:**
```groovy
task.archiveFile.get()     // ✅ Use Property API
sourceSets.main.output.classesDirs      // ✅ Note: plural
```

**Why:** Old methods don't support lazy evaluation.

---

### 8. SourceSet Output Properties ✅ Auto-Fix

**What it detects:**
```groovy
sourceSets.main.output.classesDir  // ❌ classesDir removed
```

**Fix:**
```groovy
sourceSets.main.output.classesDirs // ✅ Use classesDirs (plural)
```

**Why:** Returns `FileCollection` instead of single directory.

---

### 11. Deprecated Task Types ❌ Manual Fix

**What it detects:**
```groovy
task uploadArchives(type: Upload) { }  // ❌ Upload removed
```

**Fix:**
```groovy
// Use maven-publish or ivy-publish plugin instead
publishing {
    publications {
        maven(MavenPublication) {
            from components.java
        }
    }
}
```

**Why:** Old publishing tasks removed, use modern plugins.

---

## Medium Priority Issues

Recommended to fix.

### 9. Deprecated Properties ✅ Auto-Fix

**What it detects:**
```groovy
archivesBaseName = 'app'   // ❌ Deprecated
version = '1.0'            // ❌ Direct assignment discouraged
```

**Fix:**
```groovy
base {
    archivesName.set('app')  // ✅ Use base extension
}
version = '1.0'  // ⚠️ Still works but consider gradle.properties
```

**Why:** Better organization and lazy evaluation support.

---

### 10. Dynamic Properties ❌ Manual Fix

**What it detects:**
```groovy
project.ext['myProp'] = 'value'  // ⚠️ Discouraged
ext['myProp'] = 'value'          // ⚠️ Discouraged
```

**Fix:**
```groovy
// Option 1: gradle.properties
myProp=value

// Option 2: Typed extension
interface MyExtension {
    Property<String> getMyProp()
}
```

**Why:** Better type safety and IDE support.

---

### 12. Legacy Buildscript Classpath ❌ Manual Fix

**What it detects:**
```groovy
buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:7.0.0'  // ⚠️ Legacy
    }
}
```

**Fix:**
```groovy
plugins {
    id 'com.android.application' version '8.0.0'  // ✅ Modern
}
```

**Why:** `plugins {}` block provides better dependency management.

---

## Pattern Statistics

| Pattern | Severity | Auto-Fix | Frequency |
|---------|----------|----------|-----------|
| Deprecated Configurations | Critical | ✅ | Very Common |
| Deprecated Dependency Methods | Critical | ✅ | Very Common |
| Gradle Version | Critical | ✅ | Always (if old) |
| Convention API | High | ✅ | Common |
| Archive Properties | High | ✅ | Common |
| Task Left-Shift | High | ✅ | Rare |
| API Methods | High | ✅ | Uncommon |
| SourceSet Output | High | ✅ | Uncommon |
| Properties | Medium | ✅ | Common |
| Dynamic Properties | Medium | ❌ | Uncommon |
| Deprecated Tasks | High | ❌ | Rare |
| Buildscript | Medium | ❌ | Common |

---

## False Positives

The tool may occasionally report false positives:

### Comments
```groovy
// compile 'lib:1.0'  // ⚠️ Will be detected (but it's a comment)
```

### String Literals
```groovy
def msg = "Use compile"  // ⚠️ Will be detected (but it's a string)
```

**Workaround:** Ignore false positives or fix them anyway (they're harmless).

---

## Pattern Priority

Fix issues in this order:

1. **Critical** (will break build)
   - Deprecated configurations
   - Deprecated methods
   - Version check

2. **High** (will cause runtime errors)
   - API changes
   - Task operators
   - Property APIs

3. **Medium** (recommended)
   - Property assignments
   - Build script patterns

4. **Review Manual Issues**
   - Dynamic properties
   - Deprecated task types
   - Legacy patterns

---

## Testing After Fixes

After applying fixes, always test:

```bash
# Clean build
./gradlew clean build

# Run tests
./gradlew test

# Check specific tasks
./gradlew tasks --all
```

---

**Need more help?** See [User Guide](user-guide.md) or [Troubleshooting](troubleshooting.md)

**Back to:** [Documentation Home](README.md)
