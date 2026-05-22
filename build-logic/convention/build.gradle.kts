plugins {
    `kotlin-dsl`
}

group = "fr.taoufikcode.buildlogic"

dependencies {
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${libs.versions.detekt.get()}")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:${libs.versions.ktlint.get()}")
    implementation("org.jetbrains.kotlinx:kover-gradle-plugin:${libs.versions.kover.get()}")
    implementation("org.sonarsource.scanner.gradle:sonarqube-gradle-plugin:${libs.versions.sonarqube.get()}")
}
