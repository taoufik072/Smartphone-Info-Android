package fr.taoufikcode.presentation.list

import app.cash.turbine.test
import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import assertk.assertions.isTrue
import assertk.assertions.isInstanceOf
import assertk.assertions.isNull
import fr.taoufikcode.domain.model.home.SmartphoneSummary
import fr.taoufikcode.domain.usecase.home.CheckAndSyncSmartphonesUseCase
import fr.taoufikcode.domain.usecase.home.GetSmartphonesSummaryUseCase
import fr.taoufikcode.domain.usecase.home.SyncSmartphonesUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SmartphoneListViewModelTest {
    private lateinit var getSmartphonesUseCase: GetSmartphonesSummaryUseCase
    private lateinit var syncUseCase: SyncSmartphonesUseCase
    private lateinit var checkAndSyncUseCase: CheckAndSyncSmartphonesUseCase

    private lateinit var viewModel: SmartphoneListViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        getSmartphonesUseCase = mockk(relaxed = true)
        syncUseCase = mockk(relaxed = true)
        checkAndSyncUseCase = mockk(relaxed = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when smartphones loaded successfully then state contains smartphones`() =
        runTest {
            // Given
            val smartphones =
                listOf(
                    SmartphoneSummary("1", "iPhone 15", "url1"),
                    SmartphoneSummary("2", "Samsung S24", "url2"),
                )
            every { getSmartphonesUseCase() } returns flowOf(smartphones)
            coEvery { checkAndSyncUseCase() } returns Result.success(Unit)

            // When
            viewModel =
                SmartphoneListViewModel(
                    getSmartphonesUseCase,
                    syncUseCase,
                    checkAndSyncUseCase,
                )

            // Then
            testScheduler.advanceUntilIdle()

            val state = viewModel.state.value
            assertThat(state.smartphones.size).isEqualTo(2)
            assertThat(state.isLoading).isFalse()
            assertThat(state.error).isNull()
        }

    @Test
    fun `when OnRefresh action then sync is triggered`() =
        runTest {
            // Given
            every { getSmartphonesUseCase() } returns flowOf(emptyList())
            coEvery { checkAndSyncUseCase() } returns Result.success(Unit)
            coEvery { syncUseCase() } returns Result.success(Unit)

            viewModel =
                SmartphoneListViewModel(
                    getSmartphonesUseCase,
                    syncUseCase,
                    checkAndSyncUseCase,
                )

            testScheduler.advanceUntilIdle()

            // When
            viewModel.onAction(ListActions.Refresh)
            testScheduler.advanceUntilIdle()

            // Then
            coVerify { syncUseCase() }
        }

    @Test
    fun `when sync fails then error event is emitted`() =
        runTest {
            // Given
            val errorMessage = "Sync failed"
            every { getSmartphonesUseCase() } returns flowOf(emptyList())
            coEvery { checkAndSyncUseCase() } returns Result.success(Unit)
            coEvery { syncUseCase() } returns Result.failure(Exception(errorMessage))

            viewModel =
                SmartphoneListViewModel(
                    getSmartphonesUseCase,
                    syncUseCase,
                    checkAndSyncUseCase,
                )

            testScheduler.advanceUntilIdle()

            // When/Then
            viewModel.events.test {
                viewModel.onAction(ListActions.Refresh)
                testScheduler.advanceUntilIdle()

                val event = awaitItem()
                assertThat(event).isInstanceOf(SmartphoneListEvent.ShowError::class)
                assertThat((event as SmartphoneListEvent.ShowError).message).contains(errorMessage)
            }
        }

    @Test
    fun `initial state has isLoading = true`() =
        runTest {
            // Given
            every { getSmartphonesUseCase() } returns flowOf(emptyList())
            coEvery { checkAndSyncUseCase() } returns Result.success(Unit)

            // When — construct but do not advance: SmartphoneListState defaults isLoading = true
            viewModel = SmartphoneListViewModel(getSmartphonesUseCase, syncUseCase, checkAndSyncUseCase)

            // Then
            assertThat(viewModel.state.value.isLoading).isTrue()
        }

    @Test
    fun `when smartphones loaded then isLoading becomes false`() =
        runTest {
            // Given
            every { getSmartphonesUseCase() } returns
                flowOf(listOf(SmartphoneSummary("1", "iPhone 15", "url")))
            coEvery { checkAndSyncUseCase() } returns Result.success(Unit)

            // When
            viewModel = SmartphoneListViewModel(getSmartphonesUseCase, syncUseCase, checkAndSyncUseCase)
            testScheduler.advanceUntilIdle()

            // Then
            assertThat(viewModel.state.value.isLoading).isFalse()
        }

    @Test
    fun `when refresh fails then error is set in state`() =
        runTest {
            // Given
            val errorMessage = "Network error"
            every { getSmartphonesUseCase() } returns flowOf(emptyList())
            coEvery { checkAndSyncUseCase() } returns Result.success(Unit)
            coEvery { syncUseCase() } returns Result.failure(Exception(errorMessage))

            viewModel = SmartphoneListViewModel(getSmartphonesUseCase, syncUseCase, checkAndSyncUseCase)
            testScheduler.advanceUntilIdle()

            // When
            viewModel.onAction(ListActions.Refresh)
            testScheduler.advanceUntilIdle()

            // Then
            assertThat(viewModel.state.value.error).isEqualTo(errorMessage)
            assertThat(viewModel.state.value.isRefreshing).isFalse()
        }
}
