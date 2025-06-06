plugins {
    id("solarwinds.android-app-conventions")
    id("net.bytebuddy.byte-buddy-gradle-plugin")
}

android {
    namespace = "com.solarwinds.android.view.test"
}

dependencies {
    byteBuddy(project(":instrumentation:view:agent"))
    implementation(project(":instrumentation:view:library"))
    androidTestImplementation(project(":test-common"))
}
