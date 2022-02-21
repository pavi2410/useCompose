plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

android {
    compileSdk = 31

    defaultConfig {
        minSdk = 21
        targetSdk = 31

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }
    packagingOptions {
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
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.uiautomator:uiautomator:2.2.0")
    // Test rules and transitive dependencies:
    androidTestImplementation(libs.compose.test.junit4)
    // Needed for createComposeRule, but not createAndroidComposeRule:
    debugImplementation(libs.compose.test.manifest)
}

afterEvaluate {
    publishing {
        publications {
            // Creates a Maven publication called "release".
            create<MavenPublication>("maven") {
                from(components["release"])

                groupId = "me.pavi2410.useCompose"
                artifactId = "query"
                version = "1.0.0"
            }
        }
    }
}