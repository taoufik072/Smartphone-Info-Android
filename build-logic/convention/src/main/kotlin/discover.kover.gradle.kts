plugins {
    id("org.jetbrains.kotlinx.kover")
}

dependencies {
    kover(project(":app"))
    kover(project(":presentation"))
    kover(project(":data"))
    kover(project(":domain"))
    kover(project(":common-core"))
}

kover {
    reports {
        filters {
            excludes {
                // -- Room: database class, generated impl, DAOs, DataStore ------------
                packages(
                    "fr.taoufikcode.data.smartphones.local",
                    "fr.taoufikcode.data.smartphones.local.dao",
                    "fr.taoufikcode.data.smartphones.local.datastore",
                )

                // -- DTOs and Room entities --------------------------------------------
                packages(
                    "fr.taoufikcode.data.smartphones.remote.dto",
                    "fr.taoufikcode.data.smartphones.local.entity",
                )

                // -- Koin DI modules (source + Koin compiler plugin generated factories)
                packages("**.di")

                // -- Android framework entry points (not unit-testable) ----------------
                classes(
                    "fr.taoufikcode.discover.DiscoverApplication",
                    "fr.taoufikcode.discover.MainActivity",
                )

                // -- Compose navigation setup ------------------------------------------
                packages("fr.taoufikcode.discover.navigation")

                // -- Compose theme -----------------------------------------------------
                packages("fr.taoufikcode.discover.ui.theme")

                // -- Compose compiler generated singletons -----------------------------
                classes("**.ComposableSingletons", "**.ComposableSingletons\$*")

                // -- Compose UI screens (covered by instrumented UI tests) -------------
                packages("fr.taoufikcode.presentation.common")
                classes("**.*ScreenKt", "**.*ScreenKt\$*")

                // -- BuildConfig -------------------------------------------------------
                classes(
                    "fr.taoufikcode.discover.BuildConfig",
                    "fr.taoufikcode.data.BuildConfig",
                    "fr.taoufikcode.presentation.BuildConfig",
                )

                // -- Logging infrastructure (thin Kermit wrapper, no business logic) ---
                packages("fr.taoufikcode.common.logger")

                // -- Dispatcher infrastructure (trivial Dispatchers wrapper) ----------
                classes("fr.taoufikcode.common.coroutines.DefaultDispatcherProvider")
            }
        }
        total {
            xml {
                onCheck = true
                xmlFile = layout.buildDirectory.file("reports/kover/report.xml")
            }
            html {
                onCheck = true
                htmlDir = layout.buildDirectory.dir("reports/kover/html")
            }
        }
    }
}

tasks.register("coverageReport") {
    group = "verification"
    description = "Run all unit tests and generate Kover XML + HTML reports"
    dependsOn("koverXmlReport", "koverHtmlReport")
}
