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

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    api(platform(libs.opentelemetry.instrumentation.bom))
    api(platform(libs.opentelemetry.bom))
    api(libs.opentelemetry.android)
    api(libs.opentelemetry.android.session)

    mockitoAgent(libs.mockito.core) { isTransitive = false }
    implementation(libs.opentelemetry.exporter.otlp)

    testImplementation(libs.androidx.junit)
}

tasks.withType<Test> {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}