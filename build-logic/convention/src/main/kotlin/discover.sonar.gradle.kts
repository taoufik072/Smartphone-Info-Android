plugins {
    id("org.sonarqube")
}

sonar {
    properties {
        property("sonar.projectKey", "taoufik072_SampleSmarthphoneListDetails")
        property("sonar.organization", "taoufik072")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/kover/report.xml").get().asFile.absolutePath,
        )
        property(
            "sonar.kotlin.detekt.reportPaths",
            listOf(
                ":app",
                ":presentation",
                ":data",
                ":domain",
                ":common-core",
            ).joinToString(",") { moduleName ->
                "${project(moduleName).layout.buildDirectory.get()}/reports/detekt/detekt.xml"
            },
        )
        property(
            "sonar.sources",
            subprojects
                .flatMap { p ->
                    listOf("${p.projectDir}/src/main/java", "${p.projectDir}/src/main/kotlin")
                }
                .filter { File(it).exists() }
                .joinToString(","),
        )
        property(
            "sonar.tests",
            subprojects
                .flatMap { p ->
                    listOf("${p.projectDir}/src/test", "${p.projectDir}/src/androidTest")
                }
                .filter { File(it).exists() }
                .joinToString(","),
        )
        property(
            "sonar.exclusions",
            listOf(
                // Room generated
                "**/*Dao_Impl*.kt",
                "**/*Database_Impl*.kt",
                // Compose generated
                "**/*ComposableSingletons*.kt",
                // Build metadata
                "**/BuildConfig.kt",
                // DTOs and Room entities
                "**/dto/**",
                "**/entity/**",
            ).joinToString(","),
        )
        property(
            "sonar.coverage.exclusions",
            listOf(
                // DI wiring
                "**/di/**",
                // Room generated and database
                "**/*Dao_Impl*.kt",
                "**/*Database_Impl*.kt",
                "**/SmartphoneDatabase.kt",
                // Room DAOs and DataStore
                "**/local/dao/**",
                "**/local/datastore/**",
                // DTOs and Room entities
                "**/dto/**",
                "**/entity/**",
                // Android framework entry points
                "**/DiscoverApplication.kt",
                "**/MainActivity.kt",
                // Navigation and theme
                "**/navigation/**",
                "**/ui/theme/**",
                // Compose generated
                "**/*ComposableSingletons*.kt",
                // Compose UI screens and common components
                "**/presentation/common/**",
                "**/*Screen*.kt",
                // Build metadata
                "**/BuildConfig.kt",
                // Infrastructure
                "**/logger/**",
                "**/coroutines/DefaultDispatcherProvider.kt",
            ).joinToString(","),
        )
    }
}
//  "can't be indexed twice" call fix errors.
subprojects {
    sonar {
        isSkipProject = true
    }
}
