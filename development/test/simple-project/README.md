# Simple Project Test Case

## Issues This Project Contains:

1. **Deprecated Configurations** (CRITICAL)
   - `compile` instead of `implementation`
   - `runtime` instead of `runtimeOnly`
   - `testCompile` instead of `testImplementation`
   - `testRuntime` instead of `testRuntimeOnly`

2. **Task Left-Shift Operator** (HIGH)
   - `task hello <<` should be `task hello { doLast`

3. **Archive Properties** (HIGH)
   - `archiveName` should use `archiveFileName.set()`

4. **Deprecated Properties** (MEDIUM)
   - `archivesBaseName` should use `base.archivesName.set()`

5. **Gradle Version** (CRITICAL)
   - Using Gradle 7.6 instead of 9.0+

## Expected Results:
- Total Issues: 8-10
- Critical Issues: 5
- Auto-Fixable: 8-10
