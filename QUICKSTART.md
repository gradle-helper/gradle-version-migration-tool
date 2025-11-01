# 🚀 Quick Start Guide

Get up and running with Gradle 9 Migration Helper in 5 minutes!

## Prerequisites

- **Java 17+** installed
- **JAVA_HOME** environment variable set
- Terminal/Command Prompt access

## Step 1: Download the Gradle Wrapper

Since the gradle-wrapper.jar is a binary file, you need to download it:

```bash
# Download the Gradle wrapper
curl -L https://services.gradle.org/distributions/gradle-8.5-bin.zip -o gradle.zip
unzip gradle.zip
cp gradle-8.5/lib/plugins/gradle-wrapper-*.jar gradle/wrapper/gradle-wrapper.jar
rm -rf gradle-8.5 gradle.zip
```

Or simply run:
```bash
gradle wrapper --gradle-version 8.5
```

## Step 2: Build the Project

```bash
# Make gradlew executable (Unix/Mac)
chmod +x gradlew

# Build the project
./gradlew clean build
```

On Windows:
```cmd
gradlew.bat clean build
```

## Step 3: Start the Server

```bash
./gradlew libertyRun
```

On Windows:
```cmd
gradlew.bat libertyRun
```

The server will start on **http://localhost:9080**

## Step 4: Access the Application

Open your browser and navigate to:
```
http://localhost:9080
```

## Step 5: Analyze Your First Project

1. **Enter Project Path**: Type the absolute path to your Gradle project
   - Example (Mac/Linux): `/home/user/projects/my-gradle-app`
   - Example (Windows): `C:\Users\user\projects\my-gradle-app`

2. **Click "Analyze Project"**: Wait for the analysis to complete

3. **Review Issues**: Browse through detected issues with detailed explanations

4. **Fix Issues**: 
   - Click "Fix This Issue" for individual fixes
   - Select multiple issues and click "Fix Selected Issues"
   - Click "Fix All Auto-Fixable Issues" to fix everything at once

## Example Test Project

Want to test the tool? Create a sample project with known issues:

```bash
mkdir -p test-project
cd test-project

# Create build.gradle with deprecated syntax
cat > build.gradle << 'EOF'
plugins {
    id 'java'
}

group = 'com.example'
version = '1.0.0'
archivesBaseName = 'test-app'

repositories {
    mavenCentral()
}

dependencies {
    compile 'org.slf4j:slf4j-api:1.7.36'
    runtime 'org.slf4j:slf4j-simple:1.7.36'
    testCompile 'junit:junit:4.13.2'
}

task hello << {
    println 'Hello World'
}
EOF

# Create settings.gradle
echo "rootProject.name = 'test-project'" > settings.gradle

# Create gradle wrapper properties
mkdir -p gradle/wrapper
cat > gradle/wrapper/gradle-wrapper.properties << 'EOF'
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https\://services.gradle.org/distributions/gradle-7.6-bin.zip
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF
```

Now analyze this test project with the tool - it should detect:
- ✅ Deprecated `compile` configuration
- ✅ Deprecated `runtime` configuration
- ✅ Deprecated `testCompile` configuration
- ✅ Deprecated task `<<` operator
- ✅ Old Gradle version (7.6)
- ✅ Deprecated `archivesBaseName` property

## Troubleshooting

### Port Already in Use

If port 9080 is already in use, edit `src/main/liberty/config/server.xml`:
```xml
<httpEndpoint id="defaultHttpEndpoint"
              httpPort="9090"  <!-- Change this -->
              httpsPort="9443"
              host="*" />
```

### Java Version Issues

Ensure Java 17+ is installed:
```bash
java -version
```

### Build Failures

Clean and rebuild:
```bash
./gradlew clean build --refresh-dependencies
```

### Server Won't Start

Check logs in:
```
build/wlp/usr/servers/gradleMigrationServer/logs/
```

## Next Steps

- Read the [README.md](README.md) for detailed documentation
- Check [CONTRIBUTING.md](CONTRIBUTING.md) to contribute
- Review [CHANGELOG.md](CHANGELOG.md) for version history
- Report issues on GitHub

## Stopping the Server

Press `Ctrl+C` in the terminal where the server is running, or run:
```bash
./gradlew libertyStop
```

## Development Mode

For development with auto-reload:
```bash
./gradlew libertyDev
```

This will:
- Start the server
- Watch for file changes
- Automatically reload on changes
- Provide interactive console

## Production Deployment

Build the WAR file:
```bash
./gradlew clean build
```

The WAR file will be at:
```
build/libs/gradle-migration-helper.war
```

Deploy this to any Jakarta EE 10 compatible server.

---

**Need Help?** Open an issue on GitHub or check the documentation!
