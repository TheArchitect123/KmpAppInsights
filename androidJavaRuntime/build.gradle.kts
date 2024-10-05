import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.cli.jvm.main

plugins {
    alias(libs.plugins.androidLibrary)
    id("org.gradle.maven-publish")
    id("signing")
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.28.0"
}

android {
    namespace = "com.architect.androidjavaruntime"
    compileSdk = 35
    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("com.github.microsoft.telemetry-client-for-android:SharedTelemetryContracts:2.0.0")
    implementation("com.github.microsoft.telemetry-client-for-android:AndroidCll:2.0.0")
}

afterEvaluate {
    mavenPublishing {
        // Define coordinates for the published artifact
        coordinates(
            groupId = "io.github.thearchitect123",
            artifactId = "insightsAndroidJavaRuntime",
            version = "0.0.3"
        )

        // Configure POM metadata for the published artifact
        pom {
            name.set("KmpAndroidInsightsJavaRuntime")
            description.set("")
            inceptionYear.set("2024")
            url.set("https://github.com/TheArchitect123/KmpAppInsights")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }

            // Specify developers information
            developers {
                developer {
                    id.set("Dan Gerchcovich")
                    name.set("TheArchitect123")
                    email.set("dan.developer789@gmail.com")
                }
            }

            // Specify SCM information
            scm {
                url.set("https://github.com/TheArchitect123/KmpAppInsights")
            }
        }

        // Configure publishing to Maven Central
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

        // Enable GPG signing for all publications
        signAllPublications()
    }
}



