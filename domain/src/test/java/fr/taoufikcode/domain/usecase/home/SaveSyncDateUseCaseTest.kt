package fr.taoufikcode.domain.usecase.home

import fr.taoufikcode.domain.repository.home.SmartphonesSummaryRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class SaveSyncDateUseCaseTest {
    private lateinit var repository: SmartphonesSummaryRepository
    private lateinit var useCase: SaveSyncDateUseCase

    @Before
    fun setup() {
        repository = mockk(relaxed = true)
        useCase = SaveSyncDateUseCase(repository)
    }

    @Test
    fun `execute should delegate to repository`() =
        runTest {
            // Given
            val timestamp = 1234L

            // When
            useCase(timestamp)

            // Then
            coVerify { repository.saveSyncDateHome(timestamp) }
        }
}
