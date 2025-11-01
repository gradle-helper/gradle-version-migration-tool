# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-10-30

### Added
- Initial release of Gradle 9 Migration Helper
- Automatic detection of 12 types of Gradle 9 compatibility issues
- Auto-fix capability for 9 common issue types
- Web-based UI with Jakarta EE Servlets
- OpenLiberty server integration
- Multi-module project support
- Direct file system access (no upload required)
- Detailed issue explanations
- Selective issue fixing
- Automatic backup creation before fixes
- Filter issues by severity and auto-fixability
- Responsive modern UI design
- Session-based project management
- Comprehensive documentation

### Supported Issue Types
- Deprecated configurations (compile, runtime, etc.)
- Deprecated Convention API
- Archive task properties
- Gradle version detection
- Task left-shift operator
- Deprecated API methods
- Deprecated dependency configuration methods
- SourceSet output properties
- Deprecated properties
- Dynamic properties detection
- Deprecated task types detection
- Legacy buildscript classpath detection

### Technical Stack
- Jakarta EE 10
- Servlets 6.0
- OpenLiberty 23.x
- Gradle 8.5
- Gson 2.10.1
- Modern CSS and JavaScript

## [Unreleased]

### Planned Features
- Kotlin DSL support (build.gradle.kts)
- CI/CD integration
- Export reports (PDF, HTML)
- Custom rule definitions
- Gradle version comparison
- Rollback functionality
- Real-time file watching
- Project templates

---

[1.0.0]: https://github.com/yourusername/gradle-migration-helper/releases/tag/v1.0.0
