plugins {
    id("solarwinds.publish-conventions")
    id("solarwinds.android-library-conventions")
}

description = "Solarwinds Android Log library instrumentation for Android"

android {
    namespace = "com.solarwinds.android.log.library"
}

dependencies {
    implementation(libs.opentelemetry.android.instrumentation)
    implementation(libs.opentelemetry.instrumentation.apiSemconv)
    implementation(libs.opentelemetry.api.incubator)
}
