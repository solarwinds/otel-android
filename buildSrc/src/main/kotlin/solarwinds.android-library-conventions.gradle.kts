import org.gradle.api.publish.maven.MavenPublication

plugins {
    id("signing")
    id("maven-publish")
    id("com.android.library")
}

val variantToPublish = "release"
val swoRumVersion: String by rootProject.extra

android {
    compileSdk = 35

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

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "failed")
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
            artifactId = computeArtifactId()
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
    annotationProcessor(libs.findLibrary("auto-service-processor").get())
    implementation(libs.findLibrary("androidx-annotation").get())
    coreLibraryDesugaring(libs.findLibrary("desugarJdkLibs").get())

    implementation(libs.findLibrary("auto-service-annotations").get())
    testImplementation(libs.findBundle("junit").get())
    testImplementation(libs.findBundle("mocking").get())

    testImplementation(libs.findLibrary("robolectric").get())
    testImplementation(libs.findLibrary("opentelemetry-sdk-testing").get())
}

fun computeArtifactId(): String {
    val path = project.path
    if (!path.contains("instrumentation")) {
        // Return default artifactId for non auto-instrumentation publications.
        return project.name
    }

    // Adding library name to its related auto-instrumentation subprojects.
    // For example, prepending "okhttp-3.0-" to both the "library" and "agent" subprojects inside the "okhttp-3.0" folder.
    val match = Regex("[^:]+:[^:]+\$").find(path)
    var artifactId = match!!.value.replace(":", "-")
    if (!artifactId.startsWith("instrumentation-")) {
        artifactId = "instrumentation-$artifactId"
    }

    logger.debug("Using artifact id: '{}' for subproject: '{}'", artifactId, path)
    return artifactId
}