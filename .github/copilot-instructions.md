# GitHub Copilot Instructions

## Priority Guidelines

When generating code for this repository:

1. **Version Compatibility**: Always detect and respect the exact versions of languages, frameworks, and libraries used in this project
2. **Context Files**: Prioritize patterns and standards defined in the .github/copilot/instructions directory
3. **Codebase Patterns**: When context files don't provide specific guidance, scan the codebase for established patterns
4. **Architectural Consistency**: Maintain the layered Android library architecture and established boundaries
5. **Code Quality**: Prioritize maintainability, testability, and security in all generated code

## Project Structure

This repository contains multiple modules:

- **otel-android**: Main Android library for SolarWinds OpenTelemetry instrumentation
- **sample-app**: Demo application showcasing library features
- **test-common**: Shared test utilities and helpers
- **buildSrc**: Build convention plugins for consistent configuration

## Context Files

Reference the following files in .github/copilot/instructions for file-type specific guidance:

- **gradle.md**: Gradle build file conventions
- **java.md**: Java code style and patterns
- **kotlin.md**: Kotlin code style and patterns
- **markdown.md**: Documentation standards

## Codebase Patterns

### Code Organization

Follow defensive coding patterns and adhere to SOLID principles

#### Kotlin Files

- Use idiomatic Kotlin with null safety
- Prefer data classes for simple data containers
- Use companion objects for static/singleton functionality
- Use lateinit for deferred initialization where appropriate
- Use `@JvmOverloads` for methods with default parameters when Java interop is needed
- Use `@JvmStatic` for Object methods when Java interop is needed

#### Java Files

- Use builder pattern for complex object construction
- Keep classes focused on single responsibilities
- Use final for immutability where appropriate


## Build Configuration Patterns

### Convention Plugins

Use buildSrc convention plugins for consistent configuration:

- `solarwinds.android-library-conventions`: For Android library modules
- `solarwinds.android-app-conventions`: For Android app modules
- `solarwinds.publish-conventions`: For publishable artifacts
- `solarwinds.spotless-conventions`: For code formatting (always applied to subprojects)

### Dependency Management

- Use version catalog (`gradle/libs.versions.toml`) for all dependencies
- Reference dependencies using `libs.{library.name}` format
- Use bundles for related dependencies (e.g., `libs.bundles.junit`, `libs.bundles.mocking`)
- Use BOM (Bill of Materials) for OpenTelemetry dependencies

### Version Properties

- RUM version is defined in root `build.gradle.kts` as `swoRumVersion`
- Access in subprojects via: `val swoRumVersion: String by rootProject.extra`

## Testing Standards

### Test Structure

#### Unit Tests

- Test that don't require Android framework classes should be written as normal Junit test
- Prefer robolectric to explicit mocking for Android framework classes

#### Parameterized Tests

- Use `@ParameterizedTest` with `@MethodSource` for data-driven tests
- Use `@TestInstance(TestInstance.Lifecycle.PER_CLASS)` when needed

### Dependency Injection

- Constructor injection is preferred
- Use constructor parameters for required dependencies
- Keep constructors focused on dependency wiring

## Publishing & Versioning

- Project uses semantic versioning
- Version is defined in root `build.gradle.kts`
- Published to Maven Central via Sonatype
- Publish configuration in `solarwinds.publish-conventions` plugin

## Important: Consistency Over External Standards

When generating code for this project:

1. **Prioritize consistency** with existing code over external best practices
2. **Scan similar files** before generating new code
3. **Match existing patterns** exactly, even if they differ from common conventions
4. **Respect version constraints** - never use features from newer versions than specified
5. **Reference context files** in .github/copilot/instructions for file-type specific guidance

## Additional Resources

- Main documentation: README.md
- Sample app details: sample-app/README.md
- Contributing guidelines: CONTRIBUTING.md
- License: LICENSE (Apache 2.0)

