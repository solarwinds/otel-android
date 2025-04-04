import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

val variantToPublish = "release"
val swoRumVersion: String by rootProject.extra

android {
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig{
        minSdk = 24
    }

    lint {
        warningsAsErrors = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed")
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