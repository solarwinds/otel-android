# Gradle File Instructions

When working with Gradle build files (*.gradle.kts) in this repository:

## File Format
- Use Kotlin DSL (`.gradle.kts`) for all Gradle files

## Dependency Management

### Version Catalog
- All dependencies are defined in `gradle/libs.versions.toml`
- Reference dependencies using `libs.{library.name}` format
- Example: `implementation(libs.androidx.core.ktx)`

### Bundles
- Use bundles for related dependencies:
  - `libs.bundles.junit` for JUnit dependencies
  - `libs.bundles.mocking` for Mockito dependencies
- Example: `testImplementation(libs.bundles.junit)`

### Core Library Desugaring
- Always include for Java 8+ API support on older Android versions:
```kotlin
dependencies {
    coreLibraryDesugaring(libs.desugarJdkLibs)
}
```

## Convention Plugins
Apply common conventions via buildSrc plugins:

### Android Library
```kotlin
plugins {
    id("solarwinds.android-library-conventions")
}
```

### Android Application
```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}
```

### Publishing
```kotlin
plugins {
    id("solarwinds.publish-conventions")
}
```

### Code Formatting
- Use `./gradlew spotlessApply` to apply all formatting rules

## Android Configuration

### Namespace
- Set in build.gradle.kts, not AndroidManifest.xml:
```kotlin
android {
    namespace = "com.solarwinds.android"
}
```

