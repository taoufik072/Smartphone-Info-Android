plugins {
    alias(libs.plugins.jetbrains.kotlin.jvm)
    id("discover.kover")
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation(libs.kotlinx.coroutines.core)

    // Logging
    implementation(libs.kermit)

    // Testing
    testImplementation(libs.junit)
    testImplementation(libs.assertk)
    testImplementation(kotlin("test"))
}
