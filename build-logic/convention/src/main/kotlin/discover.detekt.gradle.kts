import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

// Applied to the root project; wires Detekt + ktlint into every subproject.
subprojects {
    configurations.all {
        exclude(group = "com.intellij", module = "annotations")
    }

    // -- Detekt ------------------------------------------------------------------
    apply(plugin = "io.gitlab.arturbosch.detekt")
    configure<DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom("${rootDir}/config/detekt/detekt.yml")
        source.setFrom(
            listOfNotNull(
                file("$projectDir/src/main/java").takeIf { it.exists() },
                file("$projectDir/src/main/kotlin").takeIf { it.exists() },
            )
        )

    }
    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
        }
    }

    // -- ktlint ------------------------------------------------------------------
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    configure<KtlintExtension> {
        version.set("1.8.0")
        android.set(true)
        outputToConsole.set(true)
        ignoreFailures.set(false)
        baseline.set(file("$projectDir/ktlint-baseline.xml"))
        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
            reporter(ReporterType.JSON)
        }
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }
}

// -- Aggregate tasks -------------------------------------------------------------
tasks.register("detektAll") {
    group = "verification"
    description = "Run Detekt on all modules"
    dependsOn(subprojects.map { "${it.path}:detekt" })
}

tasks.register("ktlintCheckAll") {
    group = "verification"
    description = "Run ktlint check on all modules"
    dependsOn(subprojects.map { "${it.path}:ktlintCheck" })
}

tasks.register("ktlintFormatAll") {
    group = "formatting"
    description = "Auto-format all modules with ktlint"
    dependsOn(subprojects.map { "${it.path}:ktlintFormat" })
}
