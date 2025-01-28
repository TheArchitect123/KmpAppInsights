import com.vanniktech.maven.publish.SonatypeHost

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

val ktorVersion = "3.0.0"
repositories {
    google()
    mavenCentral()
    gradlePluginPortal()
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    // needs to be added into a build pipeline to automate creation of the static libraries (merged universal library)
    //lipo -create “libApplicationInsightsObjectiveC.a” “libApplicationInsightsObjectiveC.a” -output “libApplicationInsightsObjectiveC.a”
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
                implementation("co.touchlab:kermit:2.0.4")
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

//        // iOS Targets
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


afterEvaluate {
    mavenPublishing {
        // Define coordinates for the published artifact
        coordinates(
            groupId = "io.github.thearchitect123",
            artifactId = "appInsights",
            version = "0.6.9"
        )

        // Configure POM metadata for the published artifact
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
                connection.set("scm:git:git://github.com/TheArchitect123/KmpAppInsights.git")
                developerConnection.set("scm:git:ssh://git@github.com:TheArchitect123/KmpAppInsights.git")
                url.set("https://github.com/TheArchitect123/KmpAppInsights")
            }
        }

        // Configure publishing to Maven Central
        publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

        // Configure publishing to Maven Central
        repositories {
            maven {
                name = "mavenCentral"
                url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                credentials {
                    username = System.getenv("OSSRH_USERNAME")
                        ?: throw GradleException("OSSRH_USERNAME environment variable is missing")
                    password = System.getenv("OSSRH_PASSWORD")
                        ?: throw GradleException("OSSRH_PASSWORD environment variable is missing")
                }
            }
        }

        // Enable GPG signing for all publications
        signAllPublications()
    }
}

signing {
    val privateKey = System.getenv("GPG_PRIVATE_KEY")
    val passphrase = System.getenv("GPG_PASSPHRASE")

    if (privateKey.isNullOrBlank() || passphrase.isNullOrBlank()) {
        throw GradleException("GPG signing key and passphrase must be provided as environment variables")
    }

    useInMemoryPgpKeys(privateKey, passphrase)
    sign(publishing.publications)
}

dependencies {
    with("de.jensklingenberg.ktorfit:ktorfit-ksp:2.0.1") {
        add("kspAndroid", this)
        add("kspIosArm64", this)
        add("kspIosSimulatorArm64", this)
    }
}

tasks.named("sourcesJar").configure { dependsOn(":shared:kspCommonMainKotlinMetadata") }
tasks.register("publishAndReleaseToMavenCentralShared") {
    dependsOn("publishToMavenCentral")
    doLast {
        println("Releasing Maven Central staging repository for shared module...")
        // Automate release here
    }
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