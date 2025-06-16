import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("net.bytebuddy.byte-buddy-gradle-plugin")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ksp)
    kotlin("plugin.serialization") version "2.0.21"
}

val localProperties = Properties()
val ci = System.getenv("CI").toBoolean()
if (!ci) {
    localProperties.load(FileInputStream(rootProject.file("local.properties")))
}

android {
    namespace = "com.solarwinds.devthoughts"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.solarwinds.devthoughts"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        all {
            val accessToken = localProperties["api.token"] as String?
            val collectorUrl = localProperties["collector.url"] as String?

            resValue("string", "api_token", accessToken ?: "")
            resValue("string", "collector_url", collectorUrl ?: "")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
        isCoreLibraryDesugaringEnabled = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
        compose = true
    }
}

dependencies {
    implementation(project(":otel-android"))

    byteBuddy(project(":instrumentation:android-log:agent"))
    implementation(project(":instrumentation:android-log:library"))
    implementation(project(":instrumentation:view-click"))

    implementation(libs.opentelemetry.android)
    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.fragment.compose)
    implementation(libs.otel.android.okhttp.lib)
    byteBuddy(libs.otel.android.okhttp.agent)

    implementation(libs.otel.android.okhttp.websocket.lib)
    byteBuddy(libs.otel.android.okhttp.websocket.agent)

    implementation(libs.okhttp)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.work.manager)

    implementation(libs.androidx.room)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)

    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.navigation.fragment.ktx)

    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.material3)

    implementation(libs.androidx.ui.tooling)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.kotlin.serialization)

    implementation(libs.androidx.navigation.compose)
    implementation(platform(libs.androidx.compose.bom))
    coreLibraryDesugaring(libs.desugarJdkLibs)

    testImplementation(libs.junit.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
