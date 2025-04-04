plugins {
    id("solarwinds.publish-conventions")
    id("solarwinds.android-library-conventions")
}

description = "Solarwinds Android view instrumentation"

android {
    namespace = "com.solarwinds.android.instrumentation.view"
}

dependencies {
    api(libs.opentelemetry.api)
    implementation(libs.byteBuddy)
    implementation(project(":instrumentation:view:library"))
}
