// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.android.plugin)
    }
}

plugins {
    alias(libs.plugins.publishPlugin)
}


val swoRumVersion = "0.0.1"
extra["swoRumVersion"] = swoRumVersion

group = "io.github.appoptics"
val versionSuffix: String? = System.getenv("SW_RUM_VERSION_SUFFIX")
version =  if(versionSuffix != null) "$swoRumVersion-$versionSuffix" else swoRumVersion

nexusPublishing {
    repositories {
        sonatype {
            password = System.getenv("SONATYPE_TOKEN")
            username = System.getenv("SONATYPE_USERNAME")

            nexusUrl = uri("https://s01.oss.sonatype.org/service/local/")
            snapshotRepositoryUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}