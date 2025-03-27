plugins {
    id("solarwinds.publish-conventions")
    id("solarwinds.android-library-conventions")
}

description = "Solarwinds Android Logging instrumentation"

android {
    namespace = "com.solarwinds.android.instrumentation.log"
}

dependencies {
    api(libs.opentelemetry.api)
    implementation(libs.byteBuddy)
    implementation(project(":instrumentation:android-log:library"))
}
