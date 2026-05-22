package fr.taoufikcode.presentation.list

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.taoufikcode.presentation.util.ComposeDataTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmartphoneListScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val snackbarHostState = SnackbarHostState()

    private fun setContent(
        state: SmartphoneListState,
        onAction: (ListActions) -> Unit = {},
    ) {
        composeTestRule.setContent {
            MaterialTheme {
                SmartphoneListScreen(
                    state = state,
                    snackBarHostState = snackbarHostState,
                    onAction = onAction,
                )
            }
        }
    }

    // ------------- State Rendering ---------

    @Test
    fun showsTopAppBarTitle() {
        setContent(
            SmartphoneListState(
                isLoading = false,
                smartphones = listOf(ComposeDataTest.summaryItem1),
            ),
        )
        composeTestRule.onNodeWithText("Smartphones").assertIsDisplayed()
    }

    @Test
    fun showsLoadingIndicatorWhenIsLoadingTrue() {
        setContent(SmartphoneListState(isLoading = true))
        composeTestRule
            .onNode(
                hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate),
            ).assertExists()
    }

    @Test
    fun doesNotShowListOrEmptyOrErrorWhileLoading() {
        setContent(SmartphoneListState(isLoading = true))
        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
        composeTestRule.onNodeWithText("No smartphones found").assertDoesNotExist()
    }

    @Test
    fun showsErrorMessageWhenErrorNotNull() {
        setContent(SmartphoneListState(isLoading = false, error = "Network error"))
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()
    }

    @Test
    fun showsRetryButtonWhenErrorNotNull() {
        setContent(SmartphoneListState(isLoading = false, error = "Network error"))
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun showsErrorIconWhenErrorNotNull() {
        setContent(SmartphoneListState(isLoading = false, error = "Network error"))
        composeTestRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    }

    @Test
    fun doesNotShowListWhenErrorNotNull() {
        setContent(SmartphoneListState(isLoading = false, error = "Network error"))
        composeTestRule.onNodeWithText("iPhone 15 Pro").assertDoesNotExist()
        composeTestRule.onNodeWithText("Galaxy S24").assertDoesNotExist()
    }

    @Test
    fun showsEmptyStateWhenSmartphonesEmptyAndNoLoadingAndNoError() {
        setContent(SmartphoneListState(isLoading = false, error = null, smartphones = emptyList()))
        composeTestRule.onNodeWithText("No smartphones found").assertIsDisplayed()
    }

    @Test
    fun doesNotShowRetryButtonInEmptyState() {
        setContent(SmartphoneListState(isLoading = false, error = null, smartphones = emptyList()))
        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
    }

    @Test
    fun showsAllListItemModelNames() {
        setContent(
            SmartphoneListState(
                isLoading = false,
                smartphones = listOf(ComposeDataTest.summaryItem1, ComposeDataTest.summaryItem2),
            ),
        )
        composeTestRule.onNodeWithText("iPhone 15 Pro").assertIsDisplayed()
        composeTestRule.onNodeWithText("Galaxy S24").assertIsDisplayed()
    }

    // ------- Action Callbacks ----------

    @Test
    fun clickingListItemDispatchesSmartphoneClickWithCorrectId() {
        val captured = mutableListOf<ListActions>()
        setContent(
            state = SmartphoneListState(
                isLoading = false,
                smartphones = listOf(ComposeDataTest.summaryItem1),
            ),
            onAction = { captured.add(it) },
        )
        composeTestRule.onNodeWithText("iPhone 15 Pro").performClick()
        assert(captured.firstOrNull() == ListActions.SmartphoneClick("id-1"))
    }

    @Test
    fun clickingSecondItemDispatchesCorrectId() {
        val captured = mutableListOf<ListActions>()
        setContent(
            state = SmartphoneListState(
                isLoading = false,
                smartphones = listOf(ComposeDataTest.summaryItem1, ComposeDataTest.summaryItem2),
            ),
            onAction = { captured.add(it) },
        )
        composeTestRule.onNodeWithText("Galaxy S24").performClick()
        assert(captured.firstOrNull() == ListActions.SmartphoneClick("id-2"))
    }

    @Test
    fun clickingRetryButtonDispatchesRefreshAction() {
        val captured = mutableListOf<ListActions>()
        setContent(
            state = SmartphoneListState(isLoading = false, error = "Network error"),
            onAction = { captured.add(it) },
        )
        composeTestRule.onNodeWithText("Retry").performClick()
        assert(captured.firstOrNull() == ListActions.Refresh)
    }

    // ------- State Transitions -------

    @Test
    fun switchingFromLoadingToSuccessShowsList() {
        var state by mutableStateOf(SmartphoneListState(isLoading = true))
        composeTestRule.setContent {
            MaterialTheme {
                SmartphoneListScreen(
                    state = state,
                    snackBarHostState = snackbarHostState,
                    onAction = {},
                )
            }
        }
        composeTestRule
            .onNode(
                hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate),
            ).assertExists()

        state =
            SmartphoneListState(
                isLoading = false,
                smartphones = listOf(ComposeDataTest.summaryItem1),
            )
        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(
                hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate),
            ).assertDoesNotExist()
        composeTestRule.onNodeWithText("iPhone 15 Pro").assertIsDisplayed()
    }

    @Test
    fun switchingFromErrorToSuccessHidesError() {
        var state by mutableStateOf(SmartphoneListState(isLoading = false, error = "Network error"))
        composeTestRule.setContent {
            MaterialTheme {
                SmartphoneListScreen(
                    state = state,
                    snackBarHostState = snackbarHostState,
                    onAction = {},
                )
            }
        }
        composeTestRule.onNodeWithText("Network error").assertIsDisplayed()

        state =
            SmartphoneListState(
                isLoading = false,
                smartphones = listOf(ComposeDataTest.summaryItem1),
            )
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Network error").assertDoesNotExist()
        composeTestRule.onNodeWithText("iPhone 15 Pro").assertIsDisplayed()
    }

    @Test
    fun switchingFromLoadingToEmptyShowsEmptyState() {
        var state by mutableStateOf(SmartphoneListState(isLoading = true))
        composeTestRule.setContent {
            MaterialTheme {
                SmartphoneListScreen(
                    state = state,
                    snackBarHostState = snackbarHostState,
                    onAction = {},
                )
            }
        }
        composeTestRule
            .onNode(
                hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate),
            ).assertExists()

        state = SmartphoneListState(isLoading = false, smartphones = emptyList())
        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(
                hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate),
            ).assertDoesNotExist()
        composeTestRule.onNodeWithText("No smartphones found").assertIsDisplayed()
    }
}
