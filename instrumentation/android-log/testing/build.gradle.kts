plugins {
    id("solarwinds.android-app-conventions")
    id("net.bytebuddy.byte-buddy-gradle-plugin")
}

android {
    namespace = "com.solarwinds.android.log.test"
}

dependencies {
    byteBuddy(project(":instrumentation:android-log:agent"))
    implementation(project(":instrumentation:android-log:library"))
    androidTestImplementation(project(":test-common"))
}
