plugins {
    alias(libs.plugins.androidLibrary)
}

android {
    namespace = "com.architect.androidjavaruntime"
    compileSdk = 34

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
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