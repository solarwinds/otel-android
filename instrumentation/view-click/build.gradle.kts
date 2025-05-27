plugins {
    id("solarwinds.publish-conventions")
    id("solarwinds.android-library-conventions")
}

description = "Solarwinds Android View capture instrumentation"

android {
    namespace = "com.solarwinds.android.view.click"
}

dependencies {
    implementation(libs.opentelemetry.android.instrumentation)
    implementation(libs.opentelemetry.instrumentation.apiSemconv)
    implementation(libs.opentelemetry.api.incubator)

    testImplementation(project(":test-common"))
    testImplementation(libs.opentelemetry.android.session)
}
