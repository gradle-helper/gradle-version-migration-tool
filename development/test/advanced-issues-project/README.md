# Advanced Issues Project Test Case

## Issues This Project Contains:

### 1. Deprecated Configurations (CRITICAL)
- `compile()` method usage
- `runtime()` method usage
- `testCompile()` method usage

### 2. Deprecated Convention API (HIGH)
- `project.convention.getPlugin(JavaPluginConvention)`
- Should use Extensions API

### 3. Dynamic Properties (MEDIUM)
- `project.ext['customProperty']`
- `ext['anotherProperty']`

### 4. Deprecated Methods (HIGH)
- `jar.getArchivePath()`
- Should use `archiveFile.get()`

### 5. Archive Properties (HIGH)
- `archiveName` direct assignment
- `archiveBaseName` direct assignment
- `archiveVersion` direct assignment
- `archiveExtension` direct assignment

### 6. Task Left-Shift Operator (HIGH)
- `task deploy <<`

### 7. SourceSet Output (HIGH)
- `sourceSets.main.output.classesDir`
- Should be `classesDirs` (plural)

### 8. Legacy Buildscript (MEDIUM)
- Using `buildscript {}` block
- Should migrate to `plugins {}` block

### 9. Gradle Version (CRITICAL)
- Using Gradle 7.0

## Expected Results:
- Total Issues: 12-15
- Critical Issues: 4-5
- Auto-Fixable: 10-12
- Manual Review Required: 2-3
