// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:3.5.3")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.61")
        classpath ("com.novoda:bintray-release:0.9.2")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}