#!/bin/bash

echo "🔧 Gradle 9 Migration Helper - Setup Script"
echo "==========================================="
echo ""

# Check Java version
echo "📋 Checking Java version..."
if command -v java &> /dev/null; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    echo "✅ Java $JAVA_VERSION detected"
    
    if [ "$JAVA_VERSION" -lt 17 ]; then
        echo "❌ Error: Java 17 or higher is required"
        echo "   Current version: $JAVA_VERSION"
        exit 1
    fi
else
    echo "❌ Error: Java is not installed or not in PATH"
    exit 1
fi

echo ""
echo "📦 Downloading Gradle Wrapper..."

# Check if gradle is available
if command -v gradle &> /dev/null; then
    echo "✅ Gradle found, generating wrapper..."
    gradle wrapper --gradle-version 8.5
else
    echo "⚠️  Gradle not found, downloading wrapper manually..."
    
    # Download gradle wrapper jar
    WRAPPER_URL="https://raw.githubusercontent.com/gradle/gradle/master/gradle/wrapper/gradle-wrapper.jar"
    
    if command -v curl &> /dev/null; then
        curl -L -o gradle/wrapper/gradle-wrapper.jar "$WRAPPER_URL"
    elif command -v wget &> /dev/null; then
        wget -O gradle/wrapper/gradle-wrapper.jar "$WRAPPER_URL"
    else
        echo "❌ Error: Neither curl nor wget is available"
        echo "   Please install curl or wget and try again"
        exit 1
    fi
fi

echo ""
echo "🔨 Building project..."
./gradlew clean build

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Setup completed successfully!"
    echo ""
    echo "🚀 To start the server, run:"
    echo "   ./gradlew libertyRun"
    echo ""
    echo "🌐 Then open your browser to:"
    echo "   http://localhost:9080"
    echo ""
    echo "📖 For more information, see:"
    echo "   - README.md (comprehensive documentation)"
    echo "   - QUICKSTART.md (quick start guide)"
    echo ""
else
    echo ""
    echo "❌ Build failed. Please check the error messages above."
    exit 1
fi
