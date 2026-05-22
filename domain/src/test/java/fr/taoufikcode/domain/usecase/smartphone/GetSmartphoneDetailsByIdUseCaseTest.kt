package fr.taoufikcode.domain.usecase.smartphone

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isTrue
import fr.taoufikcode.domain.model.smartphone.SmartphoneDetails
import fr.taoufikcode.domain.repository.smartphone.SmartphoneDetailsRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class GetSmartphoneDetailsByIdUseCaseTest {
    private lateinit var repository: SmartphoneDetailsRepository
    private lateinit var useCase: GetSmartphoneDetailsByIdUseCase

    @Before
    fun setup() {
        repository = mockk()
        useCase = GetSmartphoneDetailsByIdUseCase(repository)
    }

    @Test
    fun `when repository returns success then return details`() =
        runTest {
            // Given
            val id = "1"
            val details =
                SmartphoneDetails(
                    id = id,
                    model = "iPhone 15",
                    description = "iPhone",
                    price = 555.0,
                    imageUrl = "image_url",
                    constructionDate = java.time.LocalDate.now(),
                )
            coEvery { repository.getSmartphoneById(id) } returns Result.success(details)

            // When
            val result = useCase(id)

            // Then
            assertThat(result.isSuccess).isTrue()
            assertThat(result.getOrNull()).isEqualTo(details)
            coVerify { repository.getSmartphoneById(id) }
        }

    @Test
    fun `when repository fails then return failure`() =
        runTest {
            // Given
            val id = "1"
            val error = Exception("Network error")
            coEvery { repository.getSmartphoneById(id) } returns Result.failure(error)

            // When
            val result = useCase(id)

            // Then
            assertThat(result.isFailure).isTrue()
            assertThat(result.exceptionOrNull()).isEqualTo(error)
            coVerify { repository.getSmartphoneById(id) }
        }
}
