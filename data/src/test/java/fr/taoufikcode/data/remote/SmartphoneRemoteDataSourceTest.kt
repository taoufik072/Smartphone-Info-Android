@file:OptIn(ExperimentalCoroutinesApi::class)

package fr.taoufikcode.data.remote

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isInstanceOf
import fr.taoufikcode.data.core.DataError
import fr.taoufikcode.data.core.DataResult
import fr.taoufikcode.data.data.SmartphoneData
import fr.taoufikcode.data.smartphones.remote.SmartphoneRemoteDataSource
import fr.taoufikcode.data.utils.TestHttpClientFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class SmartphoneRemoteDataSourceTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var dataSource: SmartphoneRemoteDataSource

    data class MockResponse(
        val content: String,
        val statusCode: HttpStatusCode,
    )

    private var listResponse = MockResponse(SmartphoneData.homeContentsJson, HttpStatusCode.OK)
    private var detailsResponse = MockResponse(SmartphoneData.detailsJson, HttpStatusCode.OK)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val engine =
            MockEngine.create {
                dispatcher = testDispatcher
                addHandler { request ->
                    val path = request.url.encodedPath
                    when {
                        path == "/home/contents" -> {
                            respond(
                                content = listResponse.content,
                                status = listResponse.statusCode,
                                headers = headers { set("Content-Type", "application/json") },
                            )
                        }

                        path.startsWith("/smartphoneDetails/") -> {
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
        dataSource = SmartphoneRemoteDataSource(TestHttpClientFactory.create(engine))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    // --- getSmartphoneList ---

    @Test
    fun `getSmartphoneList on 200 returns Success with parsed smartphones`() =
        runTest {
            val result = dataSource.getSmartphoneList()

            assertThat(result).isInstanceOf(DataResult.Success::class)
            assertThat((result as DataResult.Success).data.smartphones.size).isEqualTo(2)
            assertThat(result.data.smartphones[0].model).isEqualTo("iPhone 15")
        }

    @Test
    fun `getSmartphoneList on 500 returns SERVER error`() =
        runTest {
            listResponse = MockResponse("error", HttpStatusCode.InternalServerError)

            val result = dataSource.getSmartphoneList()

            assertThat(result).isInstanceOf(DataResult.Error::class)
            assertThat((result as DataResult.Error).error).isEqualTo(DataError.Remote.SERVER)
        }

    @Test
    fun `getSmartphoneList on 408 returns REQUEST_TIMEOUT error`() =
        runTest {
            listResponse = MockResponse("error", HttpStatusCode.RequestTimeout)

            val result = dataSource.getSmartphoneList()

            assertThat(result).isInstanceOf(DataResult.Error::class)
            assertThat((result as DataResult.Error).error).isEqualTo(DataError.Remote.REQUEST_TIMEOUT)
        }

    @Test
    fun `getSmartphoneList on 429 returns TOO_MANY_REQUESTS error`() =
        runTest {
            listResponse = MockResponse("error", HttpStatusCode.TooManyRequests)

            val result = dataSource.getSmartphoneList()

            assertThat(result).isInstanceOf(DataResult.Error::class)
            assertThat((result as DataResult.Error).error).isEqualTo(DataError.Remote.TOO_MANY_REQUESTS)
        }

    @Test
    fun `getSmartphoneList on unhandled status returns UNKNOWN error`() =
        runTest {
            listResponse = MockResponse("error", HttpStatusCode.Forbidden)

            val result = dataSource.getSmartphoneList()

            assertThat(result).isInstanceOf(DataResult.Error::class)
            assertThat((result as DataResult.Error).error).isEqualTo(DataError.Remote.UNKNOWN)
        }

    // --- getSmartphoneDetails ---

    @Test
    fun `getSmartphoneDetails on 200 returns Success with correct DTO fields`() =
        runTest {
            val result = dataSource.getSmartphoneDetails("1")

            assertThat(result).isInstanceOf(DataResult.Success::class)
            val dto = (result as DataResult.Success).data
            assertThat(dto.id).isEqualTo("1")
            assertThat(dto.model).isEqualTo("iPhone 15")
            assertThat(dto.price).isEqualTo(999.99)
        }

    @Test
    fun `getSmartphoneDetails on 500 returns SERVER error`() =
        runTest {
            detailsResponse = MockResponse("error", HttpStatusCode.InternalServerError)

            val result = dataSource.getSmartphoneDetails("1")

            assertThat(result).isInstanceOf(DataResult.Error::class)
            assertThat((result as DataResult.Error).error).isEqualTo(DataError.Remote.SERVER)
        }
}
