plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

android {
    compileSdk = 34
    namespace = "me.pavi2410.useCompose.hooks"

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
    implementation(libs.compose.ui)
    implementation(libs.compose.material)
    implementation(libs.compose.tooling.preview)

    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    // Test rules and transitive dependencies:
    androidTestImplementation(libs.compose.test.junit4)
    // Needed for createComposeRule, but not createAndroidComposeRule:
    debugImplementation(libs.compose.test.manifest)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "me.pavi2410.useCompose"
            artifactId = "hooks"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}