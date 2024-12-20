// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.library") version "8.3.2" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}

buildscript {
    extra.apply {
        set("workVersion", "2.8.1")
    }
}

subprojects {

    if (this.name.contains("backup-api")) {
        return@subprojects
    }

    group = "org.secuso.pfa-core"

    apply {
        plugin("com.android.library")
        plugin("org.jetbrains.kotlin.android")
    }
}