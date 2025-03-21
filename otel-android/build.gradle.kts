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
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.solarwinds.android"

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

val mockitoAgent = configurations.create("mockitoAgent")
dependencies {
    api(platform(libs.opentelemetry.bom))
    api(libs.opentelemetry.android)
    api(libs.opentelemetry.android.session)

    implementation(libs.androidx.core.ktx)
    implementation(libs.opentelemetry.exporter.otlp)
    implementation(libs.opentelemetry.api.incubator)

    testImplementation(libs.androidx.junit)
    mockitoAgent(libs.mockito.core) { isTransitive = false }
}

tasks.withType<Test> {
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}
