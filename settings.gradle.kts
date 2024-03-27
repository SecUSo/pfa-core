pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PrivacyFriendlyCore"
include(":pfa-core")

include(":backup-api")
project(":backup-api").projectDir = File("libs/privacy-friendly-backup-api/BackupAPI")