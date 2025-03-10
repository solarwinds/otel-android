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

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.android.plugin)
        classpath(libs.byteBuddy.plugin)
    }
}

plugins {
    alias(libs.plugins.publishPlugin)
}


val swoRumVersion = "0.0.1"
extra["swoRumVersion"] = swoRumVersion

group = "io.github.appoptics"
val versionSuffix: String? = System.getenv("SW_RUM_VERSION_SUFFIX")
version = if (versionSuffix != null) "$swoRumVersion-$versionSuffix" else swoRumVersion

nexusPublishing {
    repositories {
        sonatype {
            password = System.getenv("SONATYPE_TOKEN")
            username = System.getenv("SONATYPE_USERNAME")

            nexusUrl = uri("https://s01.oss.sonatype.org/service/local/")
            snapshotRepositoryUrl =
                uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}