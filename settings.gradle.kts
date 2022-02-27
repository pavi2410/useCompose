pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            version("compose", "1.1.0")
            library("compose-ui", "androidx.compose.ui", "ui").versionRef("compose")
            library("compose-material", "androidx.compose.material", "material").versionRef("compose")
            library("compose-tooling-debug", "androidx.compose.ui", "ui-tooling").versionRef("compose")
            library("compose-tooling-preview", "androidx.compose.ui", "ui-tooling-preview").versionRef("compose")
            library("compose-test-junit4", "androidx.compose.ui", "ui-test-junit4").versionRef("compose")
            library("compose-test-manifest", "androidx.compose.ui", "ui-test-manifest").versionRef("compose")

            library("androidx-core-ktx", "androidx.core:core-ktx:1.7.0")
            library("androidx-appcompat", "androidx.appcompat:appcompat:1.4.0")
            library("material", "com.google.android.material:material:1.4.0")
            library("androidx-lifecycle-runtime-ktx", "androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
            library("androidx-activity-compose", "androidx.activity:activity-compose:1.4.0")
            library("androidx-navigation-compose", "androidx.navigation:navigation-compose:2.4.1")
        }
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

rootProject.name = "useCompose"

include(":app")
include(":react")
include(":hooks")
include(":network")
include(":colors")
