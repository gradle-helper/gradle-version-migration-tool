# Contributing to Gradle 9 Migration Helper

Thank you for your interest in contributing! This document provides guidelines and instructions for contributing to the project.

## 🤝 How to Contribute

### Reporting Bugs

1. Check if the bug has already been reported in [Issues](https://github.com/yourusername/gradle-migration-helper/issues)
2. If not, create a new issue with:
   - Clear title and description
   - Steps to reproduce
   - Expected vs actual behavior
   - Gradle version and project details
   - Screenshots if applicable

### Suggesting Enhancements

1. Check existing [Issues](https://github.com/yourusername/gradle-migration-helper/issues) for similar suggestions
2. Create a new issue with:
   - Clear description of the enhancement
   - Use cases and benefits
   - Possible implementation approach

### Pull Requests

1. Fork the repository
2. Create a feature branch from `main`
   ```bash
   git checkout -b feature/your-feature-name
   ```
3. Make your changes
4. Write or update tests as needed
5. Ensure all tests pass
   ```bash
   ./gradlew test
   ```
6. Commit your changes with clear messages
   ```bash
   git commit -m "Add feature: description"
   ```
7. Push to your fork
   ```bash
   git push origin feature/your-feature-name
   ```
8. Open a Pull Request

## 📝 Coding Standards

### Java Code

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public APIs
- Keep methods focused and concise
- Use proper exception handling

### JavaScript Code

- Use ES6+ features
- Follow consistent naming conventions
- Add comments for complex logic
- Keep functions small and focused

### CSS

- Use meaningful class names
- Follow BEM methodology where appropriate
- Keep styles modular and reusable
- Use CSS variables for theming

## 🧪 Testing

- Write unit tests for new features
- Ensure existing tests pass
- Test with different Gradle versions
- Test with multi-module projects

## 📚 Documentation

- Update README.md for new features
- Add inline comments for complex code
- Update API documentation
- Include usage examples

## 🔍 Adding New Issue Detectors

To add a new Gradle 9 issue detector:

1. Add pattern to `GradleIssueDetector.java`:
   ```java
   ISSUE_PATTERNS.put("YOUR_ISSUE_TYPE", new IssuePattern(
       Pattern.compile("your-regex-pattern"),
       "SEVERITY",
       "Issue Title",
       "Issue Description",
       autoFixable
   ));
   ```

2. Implement fix logic in `AutoFixer.java`:
   ```java
   case "YOUR_ISSUE_TYPE":
       return fixYourIssue(content, currentCode, suggestedFix);
   ```

3. Add explanation in `generateDetailedExplanation()` method

4. Add fix generation in `generateSuggestedFix()` method

5. Write tests for the new detector

6. Update README.md with the new issue type

## 🎨 UI/UX Guidelines

- Maintain consistent design language
- Ensure responsive design
- Use semantic HTML
- Provide clear user feedback
- Include loading states
- Handle errors gracefully

## 🚀 Release Process

1. Update version in `build.gradle`
2. Update CHANGELOG.md
3. Create a release tag
4. Build and test the release
5. Publish release notes

## 📞 Getting Help

- Open a discussion on GitHub
- Join our community chat
- Email: support@example.com

## 🙏 Recognition

Contributors will be recognized in:
- README.md contributors section
- Release notes
- Project website

Thank you for contributing! 🎉
