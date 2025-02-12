plugins {
    id("solarwinds.android-library-conventions")
}

android {
    namespace = "com.solarwinds.android"
    compileSdk = 35

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    api(platform(libs.opentelemetry.instrumentation.bom))
    api(platform(libs.opentelemetry.bom))
    api(libs.opentelemetry.android)

    implementation(libs.opentelemetry.exporter.otlp)
}