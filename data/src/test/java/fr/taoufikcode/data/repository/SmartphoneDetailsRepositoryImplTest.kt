@file:OptIn(ExperimentalCoroutinesApi::class)

package fr.taoufikcode.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import fr.taoufikcode.data.data.SmartphoneData
import fr.taoufikcode.data.smartphones.remote.SmartphoneRemoteDataSource
import fr.taoufikcode.data.smartphones.repository.SmartphoneDetailsRepositoryImpl
import fr.taoufikcode.data.utils.TestDispatcherProvider
import fr.taoufikcode.data.utils.TestHttpClientFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class SmartphoneDetailsRepositoryImplTest {
    private val dispatchers = TestDispatcherProvider(UnconfinedTestDispatcher())
    private lateinit var repository: SmartphoneDetailsRepositoryImpl

    data class MockResponse(
        val content: String,
        val statusCode: HttpStatusCode,
    )

    private var detailsResponse = MockResponse(SmartphoneData.detailsJson, HttpStatusCode.OK)

    @Before
    fun setUp() {
        val engine =
            MockEngine.create {
                dispatcher = dispatchers.testDispatcher
                addHandler { request ->
                    when {
                        request.url.encodedPath.startsWith("/smartphoneDetails/") -> {
                            respond(
                                content = detailsResponse.content,
                                status = detailsResponse.statusCode,
                                headers = headers { set("Content-Type", "application/json") },
                            )
                        }

                        else -> {
                            respond("Not mocked", HttpStatusCode.NotFound)
                        }
                    }
                }
            }
        repository =
            SmartphoneDetailsRepositoryImpl(
                remoteDataSource = SmartphoneRemoteDataSource(TestHttpClientFactory.create(engine)),
                dispatchers = dispatchers,
            )
    }

    @Test
    fun `getSmartphoneById on 200 returns SmartphoneDetails with correct fields`() =
        runTest {
            val result = repository.getSmartphoneById("1")

            assertThat(result.isSuccess).isTrue()
            val details = result.getOrThrow()
            assertThat(details.id).isEqualTo("1")
            assertThat(details.model).isEqualTo("iPhone 15")
            assertThat(details.price).isEqualTo(999.99)
            assertThat(details.constructionDate).isEqualTo(LocalDate.of(2023, 9, 12))
        }

    @Test
    fun `getSmartphoneById on 500 returns failure with server error message`() =
        runTest {
            detailsResponse = MockResponse("error", HttpStatusCode.InternalServerError)

            val result = repository.getSmartphoneById("1")

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Server error. Please try again later.")
        }

    @Test
    fun `getSmartphoneById on 408 returns failure with timeout message`() =
        runTest {
            detailsResponse = MockResponse("error", HttpStatusCode.RequestTimeout)

            val result = repository.getSmartphoneById("1")

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Request timed out. Please try again.")
        }

    @Test
    fun `getSmartphoneById on 429 returns failure with TOO_MANY_REQUESTS message`() =
        runTest {
            detailsResponse = MockResponse("error", HttpStatusCode.TooManyRequests)

            val result = repository.getSmartphoneById("1")

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Too many requests. Please wait and retry.")
        }

    @Test
    fun `getSmartphoneById on 401 returns failure with UNKNOWN error message`() =
        runTest {
            detailsResponse = MockResponse("error", HttpStatusCode.Unauthorized)

            val result = repository.getSmartphoneById("1")

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("An unknown error occurred.")
        }
}
