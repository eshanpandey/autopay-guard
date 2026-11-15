plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "app.autopayguard.spike"
    compileSdk = 35
    defaultConfig {
        applicationId = "app.autopayguard.spike"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "0.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation(project(":parser"))
    implementation("com.google.android.material:material:1.12.0")
}
