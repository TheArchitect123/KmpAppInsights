import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.tasks.bundling.Jar

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)

    id("org.gradle.maven-publish")
    id("signing")
    id("com.vanniktech.maven.publish") version "0.28.0"

    kotlin("plugin.serialization") version "2.0.0"
    id("com.google.devtools.ksp")
    id("de.jensklingenberg.ktorfit") version "2.1.0"
}

repositories {
    mavenCentral()
    google()
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

mavenPublishing {
    coordinates(
        groupId = "io.github.thearchitect123",
        artifactId = "appInsights",
        version = "0.5.9"
    )

    pom {
        name.set("KmpAppInsights")
        description.set("An AppInsights Client for Kotlin Multiplatform. Supports both iOS & Android")
        inceptionYear.set("2024")
        url.set("https://github.com/TheArchitect123/KmpAppInsights")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("Dan Gerchcovich")
                name.set("TheArchitect123")
                email.set("dan.developer789@gmail.com")
            }
        }

        scm {
            connection.set("scm:git:git://github.com/TheArchitect123/KmpAppInsights.git")
            developerConnection.set("scm:git:ssh://git@github.com/TheArchitect123/KmpAppInsights.git")
            url.set("https://github.com/TheArchitect123/KmpAppInsights")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
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
//
//// Task to generate sources JAR
//tasks.register<Jar>("customSourcesJar") {
//    archiveClassifier.set("sources")
//
//    // Ensure the KSP metadata task runs first
//    dependsOn("kspCommonMainKotlinMetadata")
//    from(kotlin.sourceSets["commonMain"].kotlin.srcDirs.filter { it.exists() })
//}
//
//// Attach sources JAR to publications
//publishing {
//    publications {
//        withType<MavenPublication> {
//            artifact(tasks["customSourcesJar"])
//        }
//    }
//}
//
//// Ensure artifacts are built for local publishing
//tasks.named("publishToMavenLocal") {
//    dependsOn("customSourcesJar")
//}

tasks.named("sourcesJar", org.gradle.jvm.tasks.Jar::class) {
    // Ensure KSP metadata is generated before creating the sources JAR
    dependsOn("kspCommonMainKotlinMetadata")

    // Include sources from the `commonMain` source set
    from(kotlin.sourceSets["commonMain"].kotlin.srcDirs.filter { it.exists() })
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