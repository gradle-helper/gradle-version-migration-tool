# Mixed Scenarios Project Test Case

## Purpose:
Test the tool's ability to handle projects with a mix of old and new syntax.

## Characteristics:
- ⚠️ Mix of deprecated and modern syntax
- ⚠️ Some dependencies already migrated
- ⚠️ Some tasks using old syntax, some using new
- ⚠️ Partially correct archive configurations

## Issues This Project Contains:

### Deprecated Configurations (Partial)
- ✅ `compile` - needs fix
- ✅ `runtime` - needs fix
- ✅ `testCompile` - needs fix
- ✓ `implementation` - already correct
- ✓ `runtimeOnly` - already correct
- ✓ `testImplementation` - already correct

### Archive Properties (Mixed)
- ✅ `archiveName` - needs fix
- ✓ `archiveBaseName.set()` - already correct
- ✓ `archiveFileName.set()` - already correct

### Task Syntax (Mixed)
- ✅ `task oldTask <<` - needs fix
- ✅ `task anotherOldTask <<` - needs fix
- ✓ `tasks.register()` - already correct
- ✓ `task mixedTask { doLast }` - already correct

### Other Issues
- ✅ `archivesBaseName` property
- ⚠️ Gradle 8.0 (close to 9.0 but still needs update)

## Expected Results:
- Total Issues: 6-8
- Critical Issues: 3-4
- Auto-Fixable: 6-8
- Already Correct: Multiple items (should not be flagged)

## Test Scenarios:
1. ✅ Verify only problematic code is flagged
2. ✅ Verify correct code is not flagged (no false positives)
3. ✅ Test selective fixing (fix only old syntax)
4. ✅ Verify mixed file handling
5. ✅ Check that fixes don't break already-correct code

## Use Case:
Realistic scenario where a project has been partially migrated.
Tests the tool's precision in identifying only actual issues.
