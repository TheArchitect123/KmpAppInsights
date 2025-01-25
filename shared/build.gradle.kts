import com.vanniktech.maven.publish.SonatypeHost
import java.io.ByteArrayOutputStream

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)

    id("org.gradle.maven-publish")
    id("signing")
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.28.0"

    kotlin("plugin.serialization") version "2.0.0"
    id("com.google.devtools.ksp")
    id("de.jensklingenberg.ktorfit") version "2.1.0"
}

repositories {
    mavenCentral() // Public libraries from Maven Central
    google() // For Android-specific dependencies
}

val ktorVersion = "3.0.0"

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    // iOS targets
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
                implementation("de.jensklingenberg.ktorfit:ktorfit-lib:2.1.0")
                implementation("io.ktor:ktor-client-serialization:$ktorVersion")
                implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
                implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
                implementation("io.ktor:ktor-client-logging:$ktorVersion")
                implementation(libs.ktor.client.core)

                implementation("io.github.thearchitect123:kmpEssentials:1.8.5")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
            }
        }

        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation(libs.ktor.client.okhttp)
            }
        }

        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.darwin)
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["kotlin"])
            groupId = "io.github.thearchitect123"
            artifactId = "appInsights"
            version = "0.5.8"

            // Add artifacts for Android and iOS
            artifact(tasks["kotlinMultiplatform"]) {
                classifier = "kotlinMultiplatform"
            }
        }
    }

    repositories {
        maven {
            name = "ossrh"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("OSSRH_USERNAME") ?: ""
                password = System.getenv("OSSRH_PASSWORD") ?: ""
            }
            authentication {
                create<BasicAuthentication>("basic")
            }
        }
    }
}

mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = "io.github.thearchitect123",
        artifactId = "appInsights",
        version = "0.5.8"
    )

    // Publish to Maven Central
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    // Enable GPG signing for all publications
    signAllPublications()
}

signing {
    val privateKey = System.getenv("GPG_PRIVATE_KEY")
    val passphrase = System.getenv("GPG_PASSPHRASE")

    if (privateKey.isNullOrBlank() || passphrase.isNullOrBlank()) {
        throw GradleException("GPG_PRIVATE_KEY or GPG_PASSPHRASE is missing.")
    }

    useInMemoryPgpKeys(privateKey, passphrase)
    sign(publishing.publications)
}

// Task to verify artifacts
tasks.register("verifyArtifacts") {
    group = "verification"
    description = "Verify if artifacts are generated before uploading."

    doLast {
        val artifactsDir = project.buildDir.resolve("libs")
        println("Checking for artifacts in: $artifactsDir")

        if (!artifactsDir.exists()) {
            throw GradleException("No artifacts directory found at $artifactsDir. Artifact generation failed.")
        }

        val artifacts = artifactsDir.walkTopDown()
            .filter { it.isFile }
            .toList()

        if (artifacts.isEmpty()) {
            throw GradleException("No artifacts found in $artifactsDir. Artifact generation failed.")
        }

        println("Artifacts found:")
        artifacts.forEach { artifact ->
            println("FOUND ARTIFACT - ${artifact.path}")
        }
    }
}

// Ensure artifacts are built for local publishing
tasks.named("publishToMavenLocal") {
    dependsOn("verifyArtifacts")
}

ksp {
    arg("moduleName", project.name)
}

android {
    namespace = "com.architect.kmpappinsights"
    compileSdk = 34
    defaultConfig {
        minSdk = 21
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}