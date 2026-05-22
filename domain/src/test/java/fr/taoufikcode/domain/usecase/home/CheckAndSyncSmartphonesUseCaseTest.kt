package fr.taoufikcode.domain.usecase.home

import assertk.assertThat
import assertk.assertions.isTrue
import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class CheckAndSyncSmartphonesUseCaseTest {

    private val repository: SmartphonesSummaryRepository = mockk()
    private val useCase = CheckAndSyncSmartphonesUseCase(repository)

    @Test
    fun `when last sync is recent then returns success without calling syncHome`() =
        runTest {
            // Given — 1 second ago is well within the 5-minute window
            every { repository.getSyncDateHomeStatus() } returns
                flowOf(System.currentTimeMillis() - 1_000L)

            // When
            val result = useCase()

            // Then
            assertThat(result.isSuccess).isTrue()
            coVerify(exactly = 0) { repository.syncHome() }
            coVerify(exactly = 0) { repository.saveSyncDateHome(any()) }
        }

    @Test
    fun `when last sync is expired and sync succeeds then calls syncHome and saves date`() =
        runTest {
            // Given — epoch (0L) is always older than 5 minutes
            every { repository.getSyncDateHomeStatus() } returns flowOf(0L)
            coEvery { repository.syncHome() } returns Result.success(Unit)
            coEvery { repository.saveSyncDateHome(any()) } returns Result.success(Unit)

            // When
            val result = useCase()

            // Then
            assertThat(result.isSuccess).isTrue()
            coVerify(exactly = 1) { repository.syncHome() }
            coVerify(exactly = 1) { repository.saveSyncDateHome(any()) }
        }

    @Test
    fun `when last sync is expired and sync fails then does not save date`() =
        runTest {
            // Given
            every { repository.getSyncDateHomeStatus() } returns flowOf(0L)
            coEvery { repository.syncHome() } returns Result.failure(Exception("Network error"))

            // When
            val result = useCase()

            // Then
            assertThat(result.isFailure).isTrue()
            coVerify(exactly = 1) { repository.syncHome() }
            coVerify(exactly = 0) { repository.saveSyncDateHome(any()) }
        }
}
