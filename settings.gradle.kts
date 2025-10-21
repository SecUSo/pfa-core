pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
//    resolutionStrategy {
//        eachPlugin {
//            if(requested.id.namespace == "com.android") {
//                useModule("com.android.tools.build:gradle:${requested.version}")
//            }
//        }
//    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }

    libs {
        from(files("libs.versions.toml"))
    }
}

rootProject.name = "PrivacyFriendlyCore"

include(":backup-api")
project(":backup-api").projectDir = File("libs/privacy-friendly-backup-api/BackupAPI")
include(":ui-compose")
include(":model")
include(":ui-view")
