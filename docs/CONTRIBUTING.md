# Contributing to SoothSayer

Thank you for your interest in contributing to SoothSayer! This document provides guidelines and instructions for contributing.

## Code of Conduct

### Our Pledge
We are committed to providing a welcoming and inclusive environment for all contributors.

### Our Standards
- Use welcoming and inclusive language
- Be respectful of differing viewpoints
- Accept constructive criticism gracefully
- Focus on what's best for the community

## How to Contribute

### Reporting Bugs

**Before submitting a bug report:**
. Check existing issues to avoid duplicates
. Collect relevant information (device, Android version, steps to reproduce)

**Bug Report Format:**
```markdown
**Description:**
A clear description of the bug.

**Steps to Reproduce:**
. Go to '...'
. Click on '....'
. See error

**Expected Behavior:**
What you expected to happen.

**Actual Behavior:**
What actually happened.

**Environment:**
- Device: [e.g., Pixel 5]
- Android Version: [e.g., ]
- App Version: [e.g., .0.0]

**Screenshots:**
If applicable, add screenshots.
```

### Suggesting Enhancements

**Enhancement Proposal Format:**
```markdown
**Feature Description:**
Clear description of the proposed feature.

**Use Case:**
Why is this feature needed?

**Proposed Solution:**
How should it work?

**Alternatives Considered:**
Other approaches you've thought about.
```

### Pull Requests

#### Before Starting
. Check if an issue exists for your change
. If not, create one to discuss the change
. Wait for approval before starting major work

#### Development Process
. Fork the repository
. Create a feature branch (`git checkout -b feature/AmazingFeature`)
. Make your changes
4. Write or update tests
5. Ensure all tests pass
6. Update documentation
7. Commit with clear messages
8. Push to your fork
9. Open a Pull Request

#### PR Guidelines

**Title Format:**
```
[Type] Brief description

Types: Feature, Fix, Refactor, Docs, Test, Chore
Example: [Feature] Add support for custom pattern detectors
```

**PR Description:**
```markdown
## Description
What does this PR do?

## Related Issue
Fixes #

## Changes Made
- Change 
- Change 

## Testing
How was this tested?

## Screenshots
If UI changes, add screenshots.

## Checklist
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] Code follows style guidelines
- [ ] No new warnings
```

## Development Setup

### Prerequisites
- Android Studio Arctic Fox or later
- JDK +
- Git

### Setup Steps
```bash
# Clone your fork
git clone https://github.com/YOUR_USERNAME/soothsayer-predictor.git
cd soothsayer-predictor

# Add upstream remote
git remote add upstream https://github.com/ORIGINAL_OWNER/soothsayer-predictor.git

# Create feature branch
git checkout -b feature/your-feature-name

# Keep your fork updated
git fetch upstream
git merge upstream/main
```

## Coding Standards

### Kotlin Style Guide
Follow [Kotlin coding conventions](https://kotlinlang.org/docs/coding-conventions.html)

**Key Points:**
- Use 4 spaces for indentation
- Maximum line length: 0 characters
- Use meaningful variable names
- Add KDoc comments for public APIs

**Example:**
```kotlin
/**
 * Detects patterns in cryptocurrency price data.
 *
 * @param priceData Historical price data to analyze
 * @param filters Configuration for pattern detection
 * @return List of detected patterns with confidence scores
 */
fun detectPatterns(
    priceData: List<PriceData>,
    filters: PatternFilter
): List<Pattern> {
    // Implementation
}
```

### Commit Messages

**Format:**
```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code formatting
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance tasks

**Example:**
```
feat(analysis): add support for custom time ranges

Allow users to specify custom date ranges for pattern analysis
instead of using preset options.

Closes #45
```

### Testing Requirements

#### Unit Tests
- All new business logic must have unit tests
- Aim for >80% code coverage
- Use meaningful test names

```kotlin
@Test
fun `detectDailyPatterns should return empty list when no data provided`() {
    val result = detector.detectDailyPatterns(emptyList())
    assertTrue(result.isEmpty())
}
```

#### Integration Tests
- Test repository implementations
- Test API integrations with mock servers
- Test database operations

#### UI Tests
- Test critical user flows
- Test error states
- Test loading states

### Documentation

**Required Documentation:**
- KDoc for all public classes and functions
- Update README.md for new features
- Update ARCHITECTURE.md for structural changes
- Add inline comments for complex logic

## Project Structure

```
app/
 src/
    main/
       java/com/soothsayer/predictor/
          data/           # Data layer
          domain/         # Business logic
          ui/             # Presentation layer
          utils/          # Utilities
       res/                # Resources
    test/                   # Unit tests
    androidTest/            # Integration tests
```

## Review Process

### What Reviewers Look For
. Code quality and readability
. Test coverage
. Performance implications
4. Security considerations
5. Documentation completeness
6. Adherence to architecture

### Addressing Feedback
- Respond to all comments
- Make requested changes
- Mark conversations as resolved
- Request re-review when ready

## Release Process

### Version Numbering
We use [Semantic Versioning](https://semver.org/):
- MAJOR: Breaking changes
- MINOR: New features (backward compatible)
- PATCH: Bug fixes

### Release Checklist
- [ ] All tests passing
- [ ] Documentation updated
- [ ] CHANGELOG.md updated
- [ ] Version number bumped
- [ ] Release notes prepared

## Getting Help

### Resources
- [Documentation](docs/)
- [Issue Tracker](https://github.com/OWNER/soothsayer-predictor/issues)
- [Discussions](https://github.com/OWNER/soothsayer-predictor/discussions)

### Questions
- Open a discussion for general questions
- Comment on relevant issues
- Tag maintainers if urgent

## Recognition

Contributors will be recognized in:
- README.md Contributors section
- Release notes
- About screen in the app

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

---

Thank you for contributing to SoothSayer! 
