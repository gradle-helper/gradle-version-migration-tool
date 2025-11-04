# 🔌 API Reference

REST API documentation for the Gradle 9 Migration Helper.

---

## Base URL

```
http://localhost:9080
```

---

## Endpoints

### POST /api/analyze

Analyzes a Gradle project for migration issues.

**Request:**
```http
POST /api/analyze HTTP/1.1
Content-Type: application/x-www-form-urlencoded

projectPath=/absolute/path/to/project
```

**cURL Example:**
```bash
curl -X POST http://localhost:9080/api/analyze \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "projectPath=/Users/you/my-gradle-project"
```

**Response (200 OK):**
```json
{
  "projectName": "my-project",
  "projectPath": "/Users/you/my-gradle-project",
  "currentGradleVersion": "7.6",
  "multiModule": true,
  "modules": ["core", "api"],
  "totalIssues": 10,
  "criticalIssues": 3,
  "autoFixableIssues": 8,
  "issues": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "type": "DEPRECATED_CONFIGURATIONS",
      "severity": "CRITICAL",
      "title": "Deprecated Configuration Usage",
      "description": "The 'compile' configuration is removed...",
      "filePath": "/Users/you/my-gradle-project/build.gradle",
      "lineNumber": 15,
      "currentCode": "compile 'com.google.guava:guava:30.0-jre'",
      "suggestedFix": "implementation 'com.google.guava:guava:30.0-jre'",
      "explanation": "Gradle 9 removes the 'compile' configuration...",
      "autoFixable": true,
      "module": "root"
    }
  ]
}
```

**Error Responses:**

```json
// 400 Bad Request
{
  "error": "Project path is required"
}

// 400 Bad Request
{
  "error": "Project path must be absolute"
}

// 400 Bad Request
{
  "error": "Project directory not found"
}

// 400 Bad Request
{
  "error": "Not a valid Gradle project (missing build.gradle or settings.gradle)"
}

// 500 Internal Server Error
{
  "error": "Error analyzing project: [details]"
}
```

---

### GET /api/analyze

Retrieves the cached analysis from the current session.

**Request:**
```http
GET /api/analyze HTTP/1.1
```

**cURL Example:**
```bash
curl http://localhost:9080/api/analyze
```

**Response (200 OK):**
Same format as POST /api/analyze

**Error Response:**
```json
// 404 Not Found
{
  "error": "No project analysis found in session"
}
```

---

### POST /api/fix

Applies fixes to selected issues.

**Request:**
```http
POST /api/fix HTTP/1.1
Content-Type: application/json

{
  "issueIds": [
    "550e8400-e29b-41d4-a716-446655440000",
    "6ba7b810-9dad-11d1-80b4-00c04fd430c8"
  ]
}
```

**cURL Example:**
```bash
curl -X POST http://localhost:9080/api/fix \
  -H "Content-Type: application/json" \
  -d '{
    "issueIds": ["550e8400-e29b-41d4-a716-446655440000"]
  }'
```

**Response (200 OK):**
```json
{
  "totalProcessed": 2,
  "successCount": 2,
  "failureCount": 0,
  "results": [
    {
      "issueId": "550e8400-e29b-41d4-a716-446655440000",
      "filePath": "/Users/you/my-gradle-project/build.gradle",
      "success": true,
      "message": "Successfully applied fix to build.gradle",
      "backupPath": "/Users/you/my-gradle-project/build.gradle.backup.1699234567890",
      "fixedCode": "implementation 'com.google.guava:guava:30.0-jre'"
    },
    {
      "issueId": "6ba7b810-9dad-11d1-80b4-00c04fd430c8",
      "filePath": "/Users/you/my-gradle-project/core/build.gradle",
      "success": true,
      "message": "Successfully applied fix to core/build.gradle",
      "backupPath": "/Users/you/my-gradle-project/core/build.gradle.backup.1699234567890",
      "fixedCode": "testImplementation 'junit:junit:4.13.2'"
    }
  ]
}
```

**Error Responses:**
```json
// 400 Bad Request
{
  "error": "Issue IDs are required"
}

// 404 Not Found
{
  "error": "No project analysis found in session"
}

// 404 Not Found
{
  "error": "No matching issues found"
}

// 500 Internal Server Error
{
  "error": "Error applying fix: [details]"
}
```

---

## Data Models

### ProjectInfo

| Field | Type | Description |
|-------|------|-------------|
| projectName | string | Project directory name |
| projectPath | string | Absolute path to project |
| currentGradleVersion | string | Detected Gradle version (e.g., "7.6") |
| multiModule | boolean | True if project has multiple modules |
| modules | string[] | List of module names |
| totalIssues | integer | Total number of issues found |
| criticalIssues | integer | Number of critical issues |
| autoFixableIssues | integer | Number of auto-fixable issues |
| issues | MigrationIssue[] | Array of detected issues |

### MigrationIssue

| Field | Type | Description |
|-------|------|-------------|
| id | string (UUID) | Unique issue identifier |
| type | string | Pattern ID (e.g., "DEPRECATED_CONFIGURATIONS") |
| severity | string | "CRITICAL", "HIGH", "MEDIUM", or "LOW" |
| title | string | Human-readable issue title |
| description | string | What the issue is |
| filePath | string | Absolute path to file with issue |
| lineNumber | integer | Line number where issue occurs |
| currentCode | string | Current problematic code |
| suggestedFix | string | Suggested fix (if auto-fixable) |
| explanation | string | Why this needs to be fixed |
| autoFixable | boolean | True if can be auto-fixed |
| module | string | Module name (or "root") |

### BatchFixResult

| Field | Type | Description |
|-------|------|-------------|
| totalProcessed | integer | Total issues attempted |
| successCount | integer | Number successfully fixed |
| failureCount | integer | Number that failed |
| results | FixResult[] | Array of individual results |

### FixResult

| Field | Type | Description |
|-------|------|-------------|
| issueId | string (UUID) | ID of the issue |
| filePath | string | File that was modified |
| success | boolean | True if fix succeeded |
| message | string | Success or error message |
| backupPath | string | Path to backup file (if created) |
| fixedCode | string | The new fixed code (if successful) |

---

## Usage Examples

### Example 1: Simple Analysis

```bash
# Analyze
curl -X POST http://localhost:9080/api/analyze \
  -d "projectPath=$HOME/my-project"

# Output shows 5 issues with IDs
```

### Example 2: Fix All Auto-Fixable Issues

```bash
# Get issues
response=$(curl -s http://localhost:9080/api/analyze)

# Extract auto-fixable issue IDs
issue_ids=$(echo $response | jq -r '.issues[] | select(.autoFixable == true) | .id')

# Create JSON array
json_array=$(echo $issue_ids | jq -R -s 'split("\n") | map(select(length > 0))')

# Apply fixes
curl -X POST http://localhost:9080/api/fix \
  -H "Content-Type: application/json" \
  -d "{\"issueIds\": $json_array}"
```

### Example 3: Fix Only Critical Issues

```bash
# Get critical issue IDs
critical_ids=$(curl -s http://localhost:9080/api/analyze | \
  jq -r '.issues[] | select(.severity == "CRITICAL" and .autoFixable == true) | .id')

# Fix them
curl -X POST http://localhost:9080/api/fix \
  -H "Content-Type: application/json" \
  -d "{\"issueIds\": [$(echo $critical_ids | sed 's/ /","/g' | sed 's/^/"/;s/$/"/')]}"
```

### Example 4: CI/CD Integration

```bash
#!/bin/bash
# analyze-and-fix.sh

PROJECT_PATH=$1
API_URL="http://localhost:9080"

# Start server in background
./gradlew libertyRun > /dev/null 2>&1 &
SERVER_PID=$!

# Wait for server
sleep 10

# Analyze
response=$(curl -s -X POST "$API_URL/api/analyze" -d "projectPath=$PROJECT_PATH")

# Check for issues
total_issues=$(echo $response | jq -r '.totalIssues')

if [ "$total_issues" -gt 0 ]; then
  echo "Found $total_issues issues"
  
  # Fix auto-fixable
  issue_ids=$(echo $response | jq -r '.issues[] | select(.autoFixable == true) | .id' | jq -R -s 'split("\n") | map(select(length > 0))')
  
  curl -s -X POST "$API_URL/api/fix" \
    -H "Content-Type: application/json" \
    -d "{\"issueIds\": $issue_ids}"
  
  echo "Fixes applied"
fi

# Stop server
kill $SERVER_PID
```

---

## Sessions

The API uses HTTP sessions to maintain state:

- **Session Creation**: Automatic on first request
- **Session Timeout**: 30 minutes of inactivity
- **Session Storage**: Project analysis cached in session
- **Multiple Projects**: Analyzing a new project replaces the cached one

**Session Lifecycle:**
```
1. POST /api/analyze  → Creates session, stores ProjectInfo
2. GET /api/analyze   → Returns cached ProjectInfo
3. POST /api/fix      → Uses cached ProjectInfo
4. [30 min idle]      → Session expires
5. GET /api/analyze   → 404 (no cached data)
```

---

## Rate Limiting

**Current**: No rate limiting implemented

**Best Practices**:
- Don't hammer the analyze endpoint
- Cache analysis results
- Batch fix requests when possible

---

## Error Handling

All errors return JSON with an `error` field:

```json
{
  "error": "Error message here"
}
```

**HTTP Status Codes:**
- `200` - Success
- `400` - Bad request (invalid input)
- `404` - Resource not found (no session/analysis)
- `500` - Server error (analysis/fix failed)

---

**Back to:** [Documentation Home](README.md) | [User Guide](user-guide.md)
