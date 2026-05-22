# Discover — Sample Android App

A sample Android application showcasing a Clean Architecture + MVI stack with modern tooling: Koin Compiler Plugin, Ktor, Jetpack Compose, Room, and a full quality pipeline (Detekt, ktlint, Kover, SonarCloud).

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=taoufik072_SampleSmarthphoneListDetails&metric=alert_status)](https://sonarcloud.io/project/overview?id=taoufik072_SampleSmarthphoneListDetails)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=taoufik072_SampleSmarthphoneListDetails&metric=coverage)](https://sonarcloud.io/project/overview?id=taoufik072_SampleSmarthphoneListDetails)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=taoufik072_SampleSmarthphoneListDetails&metric=bugs)](https://sonarcloud.io/project/overview?id=taoufik072_SampleSmarthphoneListDetails)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=taoufik072_SampleSmarthphoneListDetails&metric=code_smells)](https://sonarcloud.io/project/overview?id=taoufik072_SampleSmarthphoneListDetails)

---

## Tech Stack

| Category | Library | Version |
|---|---|---|
| Language | Kotlin | 2.3.21 |
| Coroutines | kotlinx-coroutines | 1.10.2 |
| UI | Jetpack Compose BOM | 2026.04.01 |
| Navigation | Navigation Compose | 2.9.8 |
| DI | Koin BOM | 4.2.1 |
| DI codegen | Koin Compiler Plugin | 1.0.0-RC2 |
| Networking | Ktor (OkHttp engine) | 3.4.3 |
| Local DB | Room | 2.8.4 |
| Image loading | Coil 3 | 3.4.0 |
| Logging | Kermit | 2.1.0 |

### Testing

| Library | Purpose | Version |
|---|---|---|
| JUnit 4 | Test runner | 4.13.2 |
| MockK | Mocking | 1.14.9 |
| Turbine | `Flow` & `StateFlow` assertions | 1.2.1 |
| Ktor `MockEngine` | Network layer unit tests | 3.4.3 |

### Quality & Coverage

| Tool | Purpose | Version |
|---|---|---|
| Kover | Code coverage (XML + HTML) | 0.9.8 |
| Detekt | Static analysis | 1.23.8 |
| ktlint | Kotlin formatter | 14.2.0 |
| SonarCloud | Continuous inspection | 7.3.0.8198 |

---

## Architecture

The project follows **Clean Architecture** with an **MVI** presentation layer, split across five Gradle modules.

```
:app              → Entry point — DI wiring, navigation, theme
:presentation     → Compose screens + ViewModels (MVI)
:data             → Repository impls, Room, DataStore, Ktor remote API
:domain           → Pure Kotlin — interfaces, use cases, domain models
:common-core      → Shared utilities (DispatcherProvider, TimeExtensions, LoggerDelegate)
```

### MVI Pattern

Each screen owns four files:

| File | Role |
|---|---|
| `*State.kt` | Immutable `data class` — the complete UI snapshot |
| `*Actions.kt` | `sealed interface` — all user intents |
| `*Event.kt` | `sealed interface` — one-shot events via `Channel` (errors, navigation) |
| `*ViewModel.kt` | Holds `StateFlow<State>`, handles `onAction()`, sends events |


---
## Dependency Injection — Koin Compiler Plugin

Koin 4.x + the **Koin Compiler Plugin** (`io.insert-koin.compiler.plugin`) replaces KSP-based annotation processing.

Definitions are auto-discovered at compile time — no manual `modules(...)` list is needed.

Each Gradle module declares a root `@Module` + `@ComponentScan` that auto-discovers all annotated classes in its package:

| Annotation | Scope | Used for |
|---|---|---|
| `@Singleton` | Application lifetime | Repositories, data sources, HttpClient |
| `@Factory` | New instance per injection | Use cases |
| `@KoinViewModel` | ViewModel scope | All ViewModels |

---

## Networking — Ktor

`HttpClient` with the OkHttp engine, configured in `data/di/NetworkModule.kt`.

---

## Build Commands

```bash

# Coverage (Kover)
./gradlew koverXmlReport                  # XML  → build/reports/kover/report.xml
./gradlew koverHtmlReport                 # HTML → build/reports/kover/html/

# Static analysis
./gradlew detektAll                       # Detekt on all modules
./gradlew ktlintCheckAll                  # ktlint check
./gradlew ktlintFormatAll                 # auto-format

# SonarCloud
./gradlew detektAll koverXmlReport        # generate reports first
./gradlew sonar -Dsonar.token=<token>     # run analysis

./gradlew clean
```

---
