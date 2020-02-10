plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(14)
        versionName = "1.0"
        versionCode = 1000
    }

    sourceSets["main"].java.srcDirs(
        sourceSets["main"].java.srcDirs.toString(),
        "src/main/kotlin"
    )
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")
}