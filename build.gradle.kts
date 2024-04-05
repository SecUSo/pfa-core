// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.library") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" apply false
}

subprojects {

    if (this.name.contains("backup-api")) {
        return@subprojects
    }

    group = "org.secuso.pfa-core"

    apply {
        plugin("com.android.library")
        plugin("org.jetbrains.kotlin.android")
        plugin("org.jetbrains.kotlin.plugin.serialization")
    }

    val implementation by configurations
    val androidTestImplementation by configurations

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

        val workVersion = "2.8.1"
        implementation("androidx.work:work-runtime:$workVersion")
        implementation("androidx.work:work-runtime-ktx:$workVersion")
        androidTestImplementation("androidx.work:work-testing:$workVersion")
    }
}