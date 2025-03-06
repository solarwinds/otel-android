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
}

description = "Solarwinds build-time auto-instrumentation for OkHttp websocket on Android"

android {
    namespace = "com.solarwinds.android.okhttp.websocket.agent"
}

dependencies {
    implementation(project(":instrumentation:okhttp:websocket:library"))
    implementation(libs.byteBuddy)
    compileOnly(libs.okhttp)
}
