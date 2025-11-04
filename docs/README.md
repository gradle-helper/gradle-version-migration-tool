# 📚 Gradle 9 Migration Helper - Documentation

Welcome to the Gradle 9 Migration Helper documentation! This guide will help you migrate your Gradle projects to Gradle 9.

---

## 📖 Documentation Index

### Getting Started
- [**Quick Start Guide**](getting-started.md) - Get up and running in 5 minutes
- [**Installation**](getting-started.md#installation) - How to install and run
- [**System Requirements**](getting-started.md#system-requirements) - What you need

### User Guides
- [**User Guide**](user-guide.md) - Complete guide to using the tool
- [**Web UI Guide**](user-guide.md#using-the-web-ui) - Using the web interface
- [**CLI Guide**](user-guide.md#using-the-api) - Using via command line

### Reference
- [**API Reference**](api-reference.md) - REST API endpoints and examples
- [**Detection Patterns**](patterns.md) - What issues are detected
- [**Troubleshooting**](troubleshooting.md) - Common problems and solutions

### Examples
- [**Spring Boot Projects**](examples.md#spring-boot-project) - Migrating Spring Boot apps
- [**Android Projects**](examples.md#android-project) - Migrating Android projects
- [**Multi-Module Projects**](examples.md#multi-module-project) - Complex project structures

---

## 🚀 Quick Links

**New to the tool?** → Start with [Getting Started](getting-started.md)

**Already installed?** → Jump to [User Guide](user-guide.md)

**Having issues?** → Check [Troubleshooting](troubleshooting.md)

**Need API details?** → See [API Reference](api-reference.md)

---

## 🤝 Contributing

Want to contribute? Check out our [Contributing Guidelines](../CONTRIBUTING.md)

---

## 💡 What This Tool Does

The Gradle 9 Migration Helper automatically:

1. **Detects** 12 types of Gradle 9 compatibility issues in your project
2. **Explains** what needs to change and why
3. **Auto-fixes** 9 out of 12 issue types automatically
4. **Creates backups** before making any changes
5. **Works with** single and multi-module projects

---

## 🎯 Key Features

- ✅ **12 Detection Patterns** - Catches all common Gradle 9 issues
- ✅ **75% Auto-Fix Rate** - 9 out of 12 patterns can be fixed automatically
- ✅ **Multi-Module Support** - Handles complex project structures
- ✅ **Web UI** - Easy-to-use browser interface
- ✅ **REST API** - Integrate with CI/CD pipelines
- ✅ **Safe Backups** - Never lose your original code

---

## 📋 Detected Issues

The tool detects these Gradle 9 compatibility issues:

| Issue Type | Severity | Auto-Fix |
|------------|----------|----------|
| Deprecated configurations (`compile`, `runtime`) | Critical | ✅ Yes |
| Deprecated dependency methods | Critical | ✅ Yes |
| Gradle version compatibility | Critical | ✅ Yes |
| Deprecated Convention API | High | ✅ Yes |
| Archive task properties | High | ✅ Yes |
| Task left-shift operator | High | ✅ Yes |
| Deprecated API methods | High | ✅ Yes |
| SourceSet output properties | High | ✅ Yes |
| Property assignments | Medium | ✅ Yes |
| Dynamic properties usage | Medium | ❌ Manual |
| Legacy buildscript classpath | Medium | ❌ Manual |
| Deprecated task types | High | ❌ Manual |

See [Detection Patterns](patterns.md) for detailed information.

---

## 🆘 Getting Help

- **Issues & Bugs**: Check [Troubleshooting](troubleshooting.md) first
- **Questions**: See the [User Guide](user-guide.md) FAQ section
- **Examples**: Browse [Examples](examples.md) for your use case

---


---

**Ready to get started?** → [Quick Start Guide](getting-started.md)
