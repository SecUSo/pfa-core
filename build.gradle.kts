// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.lib) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

buildscript {
    extra.apply {
        set("workVersion", "2.8.1")
        set("roomVersion", "2.5.2")
    }
}

subprojects {

    if (this.name.contains("backup-api")) {
        return@subprojects
    }

    configurations.configureEach {
        resolutionStrategy.dependencySubstitution {
            substitute(module("org.secuso.pfacore:model")).using(project(":model"))
        }
    }

    group = "org.secuso.pfa-core"

    apply {
        plugin("com.android.library")
        plugin("org.jetbrains.kotlin.android")
    }

    val implementation by configurations
    val androidTestImplementation by configurations

    dependencies {
        val workVersion = rootProject.extra["workVersion"]
        implementation("androidx.work:work-runtime:$workVersion")
        implementation("androidx.work:work-runtime-ktx:$workVersion")
        androidTestImplementation("androidx.work:work-testing:$workVersion")
    }
}