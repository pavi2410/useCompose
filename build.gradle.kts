plugins {
    id("com.android.application") version "8.1.1" apply false
    id("com.android.library") version "8.1.1" apply false
    kotlin("android") version "1.6.21" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}