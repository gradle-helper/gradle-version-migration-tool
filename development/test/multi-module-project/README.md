# Multi-Module Project Test Case

## Project Structure:
```
multi-module-project/
├── build.gradle (root)
├── settings.gradle
├── core/
│   └── build.gradle
├── web/
│   └── build.gradle
└── api/
    └── build.gradle
```

## Issues This Project Contains:

### Root build.gradle:
1. Deprecated `compile` configurations
2. Deprecated `testCompile` configurations
3. Archive properties using direct assignment

### core/build.gradle:
1. Deprecated configurations
2. Task left-shift operator
3. SourceSet `classesDir` (should be `classesDirs`)

### web/build.gradle:
1. Deprecated configurations
2. Archive `archiveName` property

### api/build.gradle:
1. Deprecated configurations
2. Task left-shift operator

### Gradle Version:
- Using Gradle 7.4 (should be 9.0+)

## Expected Results:
- Total Issues: 15-20
- Critical Issues: 10+
- Auto-Fixable: 15+
- Modules Detected: 3 (core, web, api)
