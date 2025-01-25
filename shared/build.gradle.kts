import com.vanniktech.maven.publish.SonatypeHost

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

mavenPublishing {
    coordinates(
        groupId = "io.github.thearchitect123",
        artifactId = "appInsights",
        version = "0.6.0"
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

tasks.register("verifyArtifacts") {
    group = "verification"
    description = "Verify if artifacts are generated before uploading."

    doLast {
        val dirsToCheck = listOf(
            project.buildDir.resolve("libs"),
            project.buildDir.resolve("bin/iosArm64/releaseFramework"),
            project.buildDir.resolve("bin/iosSimulatorArm64/releaseFramework")
        )

        println("Verifying artifacts in the following directories:")
        dirsToCheck.forEach { println(it.absolutePath) }

        val artifacts = dirsToCheck.flatMap { dir ->
            if (dir.exists()) dir.walkTopDown().filter { it.isFile }.toList() else emptyList()
        }

        if (artifacts.isEmpty()) {
            throw GradleException("No artifacts found. Ensure the build process generated the necessary outputs.")
        }

        println("Artifacts verified successfully:")
        artifacts.forEach { artifact ->
            println("FOUND ARTIFACT - ${artifact.path}")
        }
    }
}

tasks.named("publishToMavenLocal") {
    dependsOn("verifyArtifacts")
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