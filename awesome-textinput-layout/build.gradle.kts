import com.novoda.gradle.release.PublishExtension

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.novoda.bintray-release")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(14)
        versionName = "1.0.1"
        versionCode = 1001
    }

    sourceSets["main"].java.srcDirs(
        sourceSets["main"].java.srcDirs.toString(),
        "src/main/kotlin"
    )
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.3.61")
}

configure<PublishExtension> {
    userOrg = "anoop44"
    groupId = "ss.anoop"
    artifactId = "awesome-textinput-layout"
    publishVersion = "1.0.1"
    desc = "An EditText decorator inspired by the EditText fields in Add New Contact screen of Google's Contact App"
    website = "https://github.com/anoop44/AwesomeTextInputLayout"
}