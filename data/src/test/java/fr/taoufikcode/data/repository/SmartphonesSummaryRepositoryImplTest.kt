@file:OptIn(ExperimentalCoroutinesApi::class)

package fr.taoufikcode.data.repository

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import fr.taoufikcode.data.data.SmartphoneData
import fr.taoufikcode.data.smartphones.local.dao.HomeDao
import fr.taoufikcode.data.smartphones.local.datastore.SyncDataStore
import fr.taoufikcode.data.smartphones.local.entity.SmartphoneSummaryEntity
import fr.taoufikcode.data.smartphones.remote.SmartphoneRemoteDataSource
import fr.taoufikcode.data.smartphones.repository.SmartphonesSummaryRepositoryImpl
import fr.taoufikcode.data.utils.TestDispatcherProvider
import fr.taoufikcode.data.utils.TestHttpClientFactory
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import io.ktor.http.headers
import android.database.sqlite.SQLiteFullException
import fr.taoufikcode.data.core.DataError
import fr.taoufikcode.data.core.toDomain
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import java.io.IOException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SmartphonesSummaryRepositoryImplTest {
    private val dispatchers = TestDispatcherProvider(UnconfinedTestDispatcher())
    private lateinit var dao: HomeDao
    private lateinit var dataStore: SyncDataStore
    private lateinit var repository: SmartphonesSummaryRepositoryImpl

    data class MockResponse(
        val content: String,
        val statusCode: HttpStatusCode,
    )

    private var listResponse = MockResponse(SmartphoneData.homeContentsJson, HttpStatusCode.OK)

    @Before
    fun setup() {
        dao = mockk(relaxed = true)
        dataStore = mockk(relaxed = true)
        val engine =
            MockEngine.create {
                dispatcher = dispatchers.testDispatcher
                addHandler { request ->
                    when (request.url.encodedPath) {
                        "/home/contents" -> {
                            respond(
                                content = listResponse.content,
                                status = listResponse.statusCode,
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
            SmartphonesSummaryRepositoryImpl(
                remoteDataSource = SmartphoneRemoteDataSource(TestHttpClientFactory.create(engine)),
                homeDao = dao,
                homeSyncDate = dataStore,
                dispatchers = dispatchers,
            )
    }

    @Test
    fun `observeSmartphonesList returns mapped domain list from dao`() =
        runTest {
            val entity = SmartphoneSummaryEntity("1", "iPhone 15", "https://img.test/1.jpg")
            every { dao.observeHomeItems() } returns flowOf(listOf(entity))

            val result = repository.observeSmartphonesList().first()

            assertThat(result.size).isEqualTo(1)
            assertThat(result[0].model).isEqualTo("iPhone 15")
        }

    @Test
    fun `syncHome on 200 saves entities to dao and returns success`() =
        runTest {
            val result = repository.syncHome()

            assertThat(result.isSuccess).isTrue()
            coVerify { dao.replaceAll(any()) }
        }

    @Test
    fun `syncHome on 500 returns failure with server error message`() =
        runTest {
            listResponse = MockResponse("error", HttpStatusCode.InternalServerError)

            val result = repository.syncHome()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message).isEqualTo("Server error. Please try again later.")
        }

    @Test
    fun `syncHome when dao throws SQLiteFullException returns failure with DISK_FULL message`() =
        runTest {
            coEvery { dao.replaceAll(any()) } throws SQLiteFullException()

            val result = repository.syncHome()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message)
                .isEqualTo(DataError.Local.DISK_FULL.toDomain())
        }

    @Test
    fun `syncHome when dao throws generic exception returns failure with UNKNOWN message`() =
        runTest {
            coEvery { dao.replaceAll(any()) } throws RuntimeException("unexpected db error")

            val result = repository.syncHome()

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message)
                .isEqualTo(DataError.Local.UNKNOWN.toDomain())
        }

    @Test
    fun `saveSyncDateHome when dataStore throws IOException returns failure with DISK_FULL message`() =
        runTest {
            coEvery { dataStore.saveSyncDateHome(any()) } throws IOException("unexpected db error")

            val result = repository.saveSyncDateHome(System.currentTimeMillis())

            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()?.message)
                .isEqualTo(DataError.Local.UNKNOWN.toDomain())
        }

    @Test
    fun `saveSyncDateHome when dataStore succeeds returns success`() =
        runTest {
            val result = repository.saveSyncDateHome(System.currentTimeMillis())

            assertThat(result.isSuccess).isTrue()
            coVerify { dataStore.saveSyncDateHome(any()) }
        }
}
