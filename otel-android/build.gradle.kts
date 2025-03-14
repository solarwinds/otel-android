import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

/*
 * © SolarWinds Worldwide, LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

plugins {
    id("solarwinds.android-library-conventions")
    id("com.gradleup.shadow") version("8.3.5")
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
    implementation(libs.sampling)
    implementation(libs.okhttp)
    implementation(libs.opentelemetry.instrumentation.apiSemconv)
    implementation(libs.jackson.jr.objects)
    implementation(libs.androidx.work.manager)

    testImplementation(libs.androidx.junit)
    testImplementation(libs.androidx.work.test)
    testImplementation(libs.okhttp.mockwebserver)
}

tasks.withType<Test> {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.withType<ShadowJar>{
    relocate("okhttp.","com.solarwinds.android.shaded.")
}