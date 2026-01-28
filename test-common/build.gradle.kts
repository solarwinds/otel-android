plugins {
    id("solarwinds.android-library-conventions")
}

description = "Solarwinds Android common test utils"

android {
    namespace = "com.solarwinds.android.test.common"
}

dependencies {
    api(platform(libs.opentelemetry.bom))
    api(libs.opentelemetry.android.core)
    api(libs.opentelemetry.android.api)
    api(libs.opentelemetry.api)

    api(libs.assertj.core)
    api(libs.opentelemetry.sdk)
    api(libs.opentelemetry.sdk.testing)
    implementation(libs.androidx.junit)
}
