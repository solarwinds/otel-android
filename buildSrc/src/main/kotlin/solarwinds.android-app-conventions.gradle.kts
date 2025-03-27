import org.gradle.api.JavaVersion
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 35

    defaultConfig {
        minSdk = 24
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    androidTestImplementation(libs.findLibrary("androidx-test-core").get())
    androidTestImplementation(libs.findLibrary("androidx-test-rules").get())
    androidTestImplementation(libs.findLibrary("androidx-test-runner").get())
    androidTestImplementation(libs.findLibrary("opentelemetry-sdk-testing").get())
    coreLibraryDesugaring(libs.findLibrary("desugarJdkLibs").get())
}