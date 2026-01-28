import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
}

val variantToPublish = "release"
val swoRumVersion: String by rootProject.extra

android {
    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    defaultConfig {
        minSdk = 24
    }

    lint {
        warningsAsErrors = false
        disable.add("NullSafeMutableLiveData")
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.testLogging {
                events("passed", "failed")
            }
        }
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_1_8)
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    annotationProcessor(libs.findLibrary("auto-service-processor").get())
    implementation(libs.findLibrary("androidx-annotation").get())
    coreLibraryDesugaring(libs.findLibrary("desugarJdkLibs").get())

    implementation(libs.findLibrary("auto-service-annotations").get())
    testImplementation(libs.findBundle("junit").get())
    testImplementation(libs.findBundle("mocking").get())

    testImplementation(libs.findLibrary("robolectric").get())
    testImplementation(libs.findLibrary("opentelemetry-sdk-testing").get())
}