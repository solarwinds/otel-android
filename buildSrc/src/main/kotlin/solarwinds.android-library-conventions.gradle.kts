import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("signing")
    id("maven-publish")
    id("com.android.library")
}

val variantToPublish = "release"
val swoRumVersion: String by rootProject.extra

android {
    lint {
        warningsAsErrors = true
    }

    publishing {
        singleVariant(variantToPublish) {
            withJavadocJar()
            withSourcesJar()
        }
    }
}

afterEvaluate {
    val javadoc by tasks.registering(Javadoc::class) {
        source = android.sourceSets.named("main").get().java.getSourceFiles()
        classpath += project.files(android.bootClasspath)

        // grab the library variants, because apparently this is where the real classpath lives that
        // is needed for javadoc generation.
        val firstVariant = project.android.libraryVariants.toList().first()
        val javaCompile = firstVariant.javaCompileProvider.get()
        classpath += javaCompile.classpath
        classpath += javaCompile.outputs.files

        with(options as StandardJavadocDocletOptions) {
            addBooleanOption("Xdoclint:all,-missing", true)
        }
    }
}


val versionSuffix: String? = System.getenv("SW_RUM_VERSION_SUFFIX")
publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "io.github.appoptics"
            artifactId = base.archivesName.get()
            version =  if(versionSuffix != null) "$swoRumVersion-$versionSuffix" else swoRumVersion

            afterEvaluate {
                from(components.findByName(variantToPublish))
            }

            pom {
                name.set(base.archivesName)
                description.set("Solarwinds Java Instrumentation libraries.")
                url.set("www.solarwinds.com")

                scm {
                    connection.set("scm:git:git@github.com:solarwinds/otel-android.git")
                    developerConnection.set("scm:git:git@github.com:solarwinds/otel-android.git")
                    url.set("git@github.com:solarwinds/otel-android.git")
                }

                developers {
                    developer {
                        id.set("APM")
                        name.set("The APM Library Team")
                    }
                }

                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                    }
                }
            }
        }
    }
}

signing {
    setRequired {
        gradle.taskGraph.allTasks.any { (it.javaClass == PublishToMavenRepository::class) }
    }

    val signingKey = System.getenv("GPG_PRIVATE_KEY")
    val signingPassword = System.getenv("GPG_PRIVATE_KEY_PASSPHRASE")
    useInMemoryPgpKeys(signingKey, signingPassword)

    sign(publishing.publications["maven"])
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
dependencies {
    testImplementation(libs.findLibrary("opentelemetry-sdk-testing").get())
    coreLibraryDesugaring(libs.findLibrary("desugarJdkLibs").get())
}
