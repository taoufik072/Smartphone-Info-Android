package fr.taoufikcode.data.di

import fr.taoufikcode.common.logger.LoggerDelegate
import fr.taoufikcode.data.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import java.util.concurrent.TimeUnit
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import org.koin.core.annotation.Module
import org.koin.core.annotation.Singleton

private const val KTOR_TAG = "Ktor"
private const val TIMEOUT_SECONDS = 30L

private const val BASE_URL = "https://taoufikcode.free.beeceptor.com"

@Module
class NetworkModule {
    @Singleton
    fun provideEngineHttp(): HttpClientEngine =
        OkHttp.create {
            preconfigured =
                OkHttpClient
                    .Builder()
                    .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                    .build()
        }

    @Singleton
    fun provideHttpClient(engine: HttpClientEngine): HttpClient =
        HttpClient(engine) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true
                        coerceInputValues = true
                    },
                )
            }
            install(Logging) {
                logger =
                    object : Logger {
                        override fun log(message: String) {
                            LoggerDelegate.d(KTOR_TAG) { message }
                        }
                    }
                level = if (BuildConfig.DEBUG) LogLevel.BODY else LogLevel.NONE
            }
            defaultRequest {
                url(BASE_URL)
                contentType(ContentType.Application.Json)
            }
        }
}
