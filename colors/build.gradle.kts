plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

android {
    compileSdk = 34
    namespace = "me.pavi2410.useCompose.colors"

    defaultConfig {
        minSdk = 21

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    val composeBom = platform(libs.compose.bom)
    api(composeBom)
    androidTestApi(composeBom)
    api(libs.compose.ui)
    api(libs.compose.material)
    api(libs.compose.tooling.preview)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.uiautomator)
    // Test rules and transitive dependencies:
    androidTestImplementation(libs.compose.test.junit4)
    // Needed for createComposeRule, but not createAndroidComposeRule:
    debugImplementation(libs.compose.test.manifest)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "me.pavi2410.useCompose"
            artifactId = "colors"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}