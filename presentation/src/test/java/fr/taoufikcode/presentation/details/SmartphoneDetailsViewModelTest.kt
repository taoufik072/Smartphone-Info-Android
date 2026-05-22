package fr.taoufikcode.presentation.details

import androidx.lifecycle.SavedStateHandle
import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isNotNull
import assertk.assertions.isNull
import fr.taoufikcode.domain.model.smartphone.SmartphoneDetails
import fr.taoufikcode.domain.usecase.smartphone.GetSmartphoneDetailsByIdUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import app.cash.turbine.test
import assertk.assertions.contains
import assertk.assertions.isInstanceOf
import io.mockk.coVerify
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class SmartphoneDetailsViewModelTest {
    private lateinit var getSmartphoneDetailsByIdUseCase: GetSmartphoneDetailsByIdUseCase
    private lateinit var savedStateHandle: SavedStateHandle
    private lateinit var viewModel: SmartphoneDetailsViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSmartphoneDetailsByIdUseCase = mockk()
        savedStateHandle = SavedStateHandle(mapOf("smartphoneId" to "1"))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when load success then state contains details`() =
        runTest {
            // Given
            val details =
                SmartphoneDetails(
                    id = "1",
                    model = "iPhone 15",
                    description = "Desc",
                    price = 999.0,
                    imageUrl = "url",
                    constructionDate = LocalDate.now(),
                )
            coEvery { getSmartphoneDetailsByIdUseCase("1") } returns Result.success(details)

            // When
            viewModel = SmartphoneDetailsViewModel(getSmartphoneDetailsByIdUseCase, savedStateHandle)

            // Then
            testScheduler.advanceUntilIdle()
            val state = viewModel.state.value

            assertThat(state.smartphone).isNotNull()
            assertThat(state.smartphone?.model).isEqualTo("iPhone 15")
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }

    @Test
    fun `when load failure then state contains error`() =
        runTest {
            // Given
            val errorMessage = "Network error"
            coEvery { getSmartphoneDetailsByIdUseCase("1") } returns Result.failure(Exception(errorMessage))

            // When
            viewModel = SmartphoneDetailsViewModel(getSmartphoneDetailsByIdUseCase, savedStateHandle)

            // Then
            testScheduler.advanceUntilIdle()
            val state = viewModel.state.value

            assertThat(state.smartphone).isNull()
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo(errorMessage)
        }

    @Test
    fun `when smartphoneId is null then state shows error without loading`() =
        runTest {
            // Given
            val noIdStateHandle = SavedStateHandle(emptyMap())

            // When — the else branch in init runs synchronously, no coroutine needed
            viewModel = SmartphoneDetailsViewModel(getSmartphoneDetailsByIdUseCase, noIdStateHandle)

            // Then
            val state = viewModel.state.value
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isEqualTo("Smartphone not found")
            coVerify(exactly = 0) { getSmartphoneDetailsByIdUseCase(any()) }
        }

    @Test
    fun `when Retry action then use case is called again`() =
        runTest {
            // Given
            val details =
                SmartphoneDetails(
                    id = "1",
                    model = "iPhone 15",
                    description = "Desc",
                    price = 999.0,
                    imageUrl = "url",
                    constructionDate = LocalDate.now(),
                )
            coEvery { getSmartphoneDetailsByIdUseCase("1") } returns Result.success(details)

            viewModel = SmartphoneDetailsViewModel(getSmartphoneDetailsByIdUseCase, savedStateHandle)
            testScheduler.advanceUntilIdle()

            // When
            viewModel.onAction(DetailsActions.Retry)
            testScheduler.advanceUntilIdle()

            // Then
            coVerify(exactly = 2) { getSmartphoneDetailsByIdUseCase("1") }
        }

    @Test
    fun `when Retry fails then ShowError event is emitted`() =
        runTest {
            // Given — first load succeeds, Retry fails
            val details =
                SmartphoneDetails(
                    id = "1",
                    model = "iPhone 15",
                    description = "Desc",
                    price = 999.0,
                    imageUrl = "url",
                    constructionDate = LocalDate.now(),
                )
            val errorMessage = "Network error"
            coEvery { getSmartphoneDetailsByIdUseCase("1") } returnsMany
                listOf(Result.success(details), Result.failure(Exception(errorMessage)))

            viewModel = SmartphoneDetailsViewModel(getSmartphoneDetailsByIdUseCase, savedStateHandle)
            testScheduler.advanceUntilIdle()

            viewModel.events.test {
                viewModel.onAction(DetailsActions.Retry)
                testScheduler.advanceUntilIdle()

                val event = awaitItem()
                assertThat(event).isInstanceOf(SmartphoneDetailsEvent.ShowError::class)
                assertThat((event as SmartphoneDetailsEvent.ShowError).message).contains(errorMessage)
            }
        }
}
