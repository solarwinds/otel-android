# Markdown File Instructions

## General Principles

- Use clear section headings and bullet points
- Match documentation style and formatting from existing markdown files
- Reference code examples using fenced code blocks
- Keep content concise and actionable

## File Structure

### Headings

- Use `#` for main title (H1)
- Use `##` for major sections (H2)
- Use `###` for subsections (H3)
- Use `####` and below sparingly
- Use blank lines between sections

### Bullet Points

- Use `-` for unordered lists
- Use numbered lists `1.`, `2.`, etc. for ordered sequences
- Indent nested lists with 2 spaces

Example:

```markdown
- Events
    - There is a websocket client that connects to `https://echo.websocket.org/.ws`
    - Trigger a crash: double tap any of your thoughts
- Metric
    - Metrics will generated when the user adds thought
```

## Code Blocks

### Fenced Code Blocks

Always use fenced code blocks with language identifiers:

````markdown
```kotlin
fun emitEvent(name: String) {
    logger.emit(name)
}
```

```properties
collector.url=<swo otel url with scheme i.e [scheme]://[url]>
api.token=<a valid Solarwinds observability token>
```

```bash
./gradlew build
```
````

### Inline Code

Use backticks for inline code, commands, file names, and technical terms:

- File names: `build.gradle.kts`
- Commands: `./gradlew test`
- Package names: `com.solarwinds.android`
- Method names: `emitEvent()`
- Variables: `swoRumVersion`

## Links

### Internal Links

Reference files within the repository:

```markdown
See [CONTRIBUTING.md](CONTRIBUTING.md)
See its [README](sample-app/README.md) for details
```

### External Links

```markdown
[OpenTelemetry Java SDK](https://github.com/open-telemetry/opentelemetry-java)
[Troubleshooting documentation](#troubleshooting)
```

## Common Sections

### README Files

Typical sections for README files:

1. Title and brief description
2. Features (bulleted list)
3. Getting Started / Installation
4. Configuration
5. Usage Examples
6. Contributing
7. License

### Feature Lists

Present features as clear bullet points:

```markdown
# Features

* Crash reporting
* ANR detection
* Network change detection
* Full Android Activity and Fragment lifecycle monitoring
* Access to the OpenTelemetry APIs for manual instrumentation
```

### Action Instructions

Use clear, action-oriented language:

```markdown
- Trigger a crash: double tap any of your thoughts in the Thought screen
- Trigger an ANR: long press on any of your thoughts in the Thought screen
```

## Formatting

### Emphasis

- **Bold** for important terms: `**bold text**`
- *Italics* for emphasis: `*italic text*`
- Use sparingly for maximum impact

### Line Breaks

- Use blank lines to separate sections
- No trailing whitespace at end of lines
- End all files with a newline

### Tables

Use tables for structured data:

```markdown
| Feature         | Description                          |
|-----------------|--------------------------------------|
| Crash reporting | Automatic crash detection            |
| ANR detection   | Application Not Responding detection |
```

## Technical Documentation

### API Documentation

Reference classes and methods properly:

```markdown
Use the {@link SolarwindsRumBuilder} to configure {@link SolarwindsRum}
```

### Version Information

Be explicit about versions and requirements:

```markdown
- Kotlin 2.2.21
- Java 8 (target compatibility)
- Android compileSdk: 36
- Android minSdk: 24
```

## File-Specific Guidelines

### README.md (Root)

- Start with clear project description
- Include feature list
- Provide getting started instructions
- Link to sample app and other documentation
- Include license and contributing information

### CONTRIBUTING.md

- Explain how to contribute
- List coding standards
- Describe PR process
- Include contact information

### Module READMEs

- Focus on specific module features
- Provide usage examples specific to the module
- Explain how the module fits in the larger project

## Style Guidelines

- Use American English spelling
- Use present tense ("generates" not "will generate")
- Use active voice when possible
- Keep sentences and paragraphs concise
- Use consistent terminology throughout documentation
