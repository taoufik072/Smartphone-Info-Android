plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.koin.compiler) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    id("discover.kover")
    id("discover.sonar")
    id("discover.detekt")
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
