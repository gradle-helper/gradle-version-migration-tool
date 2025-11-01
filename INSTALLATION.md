# 📦 Installation Guide

Complete installation instructions for the Gradle 9 Migration Helper.

## System Requirements

### Minimum Requirements
- **Java**: 17 or higher
- **RAM**: 512 MB
- **Disk Space**: 500 MB
- **OS**: Windows, macOS, or Linux

### Recommended Requirements
- **Java**: 21
- **RAM**: 2 GB
- **Disk Space**: 1 GB
- **OS**: Latest stable version

## Installation Methods

### Method 1: Automated Setup (Recommended)

#### Unix/macOS/Linux
```bash
# Clone the repository
git clone https://github.com/yourusername/gradle-migration-helper.git
cd gradle-migration-helper

# Run setup script
./setup.sh
```

#### Windows
```cmd
# Clone the repository
git clone https://github.com/yourusername/gradle-migration-helper.git
cd gradle-migration-helper

# Build manually
gradlew.bat clean build
```

### Method 2: Manual Setup

#### Step 1: Verify Java Installation
```bash
java -version
```

Expected output: `java version "17.x.x"` or higher

#### Step 2: Clone Repository
```bash
git clone https://github.com/yourusername/gradle-migration-helper.git
cd gradle-migration-helper
```

#### Step 3: Download Gradle Wrapper (if needed)
```bash
# Option A: Using existing Gradle installation
gradle wrapper --gradle-version 8.5

# Option B: Manual download
curl -L https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar \
  -o gradle/wrapper/gradle-wrapper.jar
```

#### Step 4: Make Scripts Executable (Unix/macOS/Linux)
```bash
chmod +x gradlew
chmod +x setup.sh
```

#### Step 5: Build Project
```bash
# Unix/macOS/Linux
./gradlew clean build

# Windows
gradlew.bat clean build
```

#### Step 6: Start Server
```bash
# Unix/macOS/Linux
./gradlew libertyRun

# Windows
gradlew.bat libertyRun
```

### Method 3: Docker Installation (Coming Soon)

```bash
# Pull image
docker pull yourusername/gradle-migration-helper:latest

# Run container
docker run -p 9080:9080 -v /path/to/projects:/projects \
  yourusername/gradle-migration-helper:latest
```

## Post-Installation

### Verify Installation

1. **Check Server Status**
   ```bash
   curl http://localhost:9080
   ```
   
   Expected: HTML response with the application UI

2. **Check Logs**
   ```bash
   tail -f build/wlp/usr/servers/gradleMigrationServer/logs/messages.log
   ```

3. **Access Web Interface**
   - Open browser: http://localhost:9080
   - You should see the Gradle 9 Migration Helper interface

### Configuration

#### Change HTTP Port

Edit `src/main/liberty/config/server.xml`:
```xml
<httpEndpoint id="defaultHttpEndpoint"
              httpPort="8080"  <!-- Change from 9080 -->
              httpsPort="9443"
              host="*" />
```

Then restart the server.

#### Adjust Memory Settings

Edit `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g
```

#### Enable Debug Mode

```bash
./gradlew libertyRun --debug-jvm
```

Debug port: 7777

## Troubleshooting

### Issue: Java Not Found

**Solution**:
```bash
# Check Java installation
which java

# Set JAVA_HOME (Unix/macOS)
export JAVA_HOME=/path/to/java
export PATH=$JAVA_HOME/bin:$PATH

# Set JAVA_HOME (Windows)
set JAVA_HOME=C:\Program Files\Java\jdk-17
set PATH=%JAVA_HOME%\bin;%PATH%
```

### Issue: Port Already in Use

**Solution**:
```bash
# Find process using port 9080
lsof -i :9080  # Unix/macOS
netstat -ano | findstr :9080  # Windows

# Kill the process or change port in server.xml
```

### Issue: Build Fails

**Solution**:
```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies

# Check for errors
./gradlew build --stacktrace
```

### Issue: Permission Denied

**Solution**:
```bash
# Make gradlew executable
chmod +x gradlew

# Or run with bash
bash gradlew clean build
```

### Issue: Gradle Wrapper Not Found

**Solution**:
```bash
# Download wrapper jar manually
mkdir -p gradle/wrapper
curl -L https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar \
  -o gradle/wrapper/gradle-wrapper.jar
```

## Upgrading

### From Source
```bash
# Pull latest changes
git pull origin main

# Rebuild
./gradlew clean build

# Restart server
./gradlew libertyStop
./gradlew libertyRun
```

### Using Release
```bash
# Download latest release
wget https://github.com/yourusername/gradle-migration-helper/releases/latest/download/gradle-migration-helper.war

# Deploy to your server
cp gradle-migration-helper.war /path/to/server/dropins/
```

## Uninstallation

### Remove Application
```bash
# Stop server
./gradlew libertyStop

# Remove directory
cd ..
rm -rf gradle-migration-helper
```

### Clean Build Artifacts
```bash
./gradlew clean
rm -rf build/
rm -rf .gradle/
```

## Development Installation

For contributors and developers:

```bash
# Clone with submodules
git clone --recursive https://github.com/yourusername/gradle-migration-helper.git
cd gradle-migration-helper

# Install development dependencies
./gradlew build

# Run in development mode (with hot reload)
./gradlew libertyDev

# Run tests
./gradlew test

# Generate coverage report
./gradlew jacocoTestReport
```

## IDE Setup

### IntelliJ IDEA
1. Open project directory
2. Import as Gradle project
3. Set JDK to 17+
4. Run configuration: Gradle task `libertyRun`

### Eclipse
1. Import > Existing Gradle Project
2. Select project directory
3. Configure JDK 17+
4. Run As > Gradle Build > `libertyRun`

### VS Code
1. Install Java Extension Pack
2. Install Gradle Extension
3. Open project folder
4. Run task: `libertyRun`

## Production Deployment

### Standalone WAR Deployment

1. **Build WAR**
   ```bash
   ./gradlew clean build
   ```

2. **Deploy to Server**
   ```bash
   # Copy WAR to server
   scp build/libs/gradle-migration-helper.war user@server:/path/to/deploy/
   
   # Or use server-specific deployment
   ```

3. **Supported Servers**
   - OpenLiberty 23.x+
   - WildFly 27+
   - Payara 6+
   - TomEE 9+
   - GlassFish 7+

### Cloud Deployment

#### Heroku
```bash
# Add Heroku remote
heroku create gradle-migration-helper

# Deploy
git push heroku main
```

#### AWS Elastic Beanstalk
```bash
# Create application
eb init -p java gradle-migration-helper

# Deploy
eb create gradle-migration-env
eb deploy
```

#### Google Cloud Platform
```bash
# Deploy to App Engine
gcloud app deploy
```

## Support

For installation issues:
- Check [Troubleshooting](#troubleshooting) section
- Review [GitHub Issues](https://github.com/yourusername/gradle-migration-helper/issues)
- Contact: support@example.com

## Next Steps

After successful installation:
1. Read [QUICKSTART.md](QUICKSTART.md) for usage guide
2. Review [README.md](README.md) for features
3. Check [CONTRIBUTING.md](CONTRIBUTING.md) to contribute

---

**Installation complete!** 🎉 Start analyzing your Gradle projects now.
