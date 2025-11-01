# Gradle 9 Migration Helper

A web-based tool to detect and fix Gradle 9 compatibility issues in Java projects.

## Prerequisites

- Java 17 or higher
- Gradle 8.5+ (included via wrapper)

## Quick Start

1. **Build the project**
   ```bash
   ./gradlew clean build
   ```

2. **Start the server**
   ```bash
   ./gradlew libertyRun
   ```

3. **Access the application**
   ```
   http://localhost:9080
   ```

## Technology Stack

- Jakarta EE 10
- OpenLiberty
- Gradle 8.5
- Vanilla JavaScript

## Project Structure

```
gradle-migration-helper/
├── src/main/
│   ├── java/
│   ├── liberty/config/
│   └── webapp/
├── build.gradle
└── settings.gradle
```

## License

MIT License
