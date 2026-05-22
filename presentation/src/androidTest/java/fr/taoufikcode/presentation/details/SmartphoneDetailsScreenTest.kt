package fr.taoufikcode.presentation.details

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.taoufikcode.presentation.util.ComposeDataTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmartphoneDetailsScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    private val snackbarHostState = SnackbarHostState()

    private fun setContent(
        state: SmartphoneDetailsState,
        onAction: (DetailsActions) -> Unit = {},
        onBackClick: () -> Unit = {},
    ) {
        composeTestRule.setContent {
            MaterialTheme {
                SmartphoneDetailsScreen(
                    state = state,
                    snackBarHostState = snackbarHostState,
                    onAction = onAction,
                    onBackClick = onBackClick,
                )
            }
        }
    }

    // -- State Rendering -------------------------------------------

    @Test
    fun showsFallbackDetailsTitleWhenSmartphoneNull() {
        setContent(SmartphoneDetailsState(smartphone = null, isLoading = false))
        composeTestRule.onNodeWithText("Details").assertIsDisplayed()
    }

    @Test
    fun showsSmartphoneModelInTopAppBarWhenLoaded() {
        setContent(
            SmartphoneDetailsState(
                smartphone = ComposeDataTest.detailsUi,
                isLoading = false,
            ),
        )
        composeTestRule.onAllNodesWithText("iPhone 15 Pro").onFirst().assertIsDisplayed()
    }

    @Test
    fun showsBackButtonAlways() {
        setContent(SmartphoneDetailsState(isLoading = false))
        composeTestRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun showsLoadingIndicatorWhenIsLoadingTrue() {
        setContent(SmartphoneDetailsState(isLoading = true))
        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()
    }

    @Test
    fun doesNotShowContentOrRetryWhileLoading() {
        setContent(SmartphoneDetailsState(isLoading = true))
        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
        composeTestRule.onNodeWithText("Model").assertDoesNotExist()
    }

    @Test
    fun showsErrorMessageWhenErrorNotNull() {
        setContent(SmartphoneDetailsState(isLoading = false, error = "Failed to load"))
        composeTestRule.onNodeWithText("Failed to load").assertIsDisplayed()
    }

    @Test
    fun showsRetryButtonWhenErrorNotNull() {
        setContent(SmartphoneDetailsState(isLoading = false, error = "Failed to load"))
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()
    }

    @Test
    fun doesNotShowDetailContentWhenError() {
        setContent(SmartphoneDetailsState(isLoading = false, error = "Failed to load"))
        composeTestRule.onNodeWithText("Model").assertDoesNotExist()
    }

    @Test
    fun showsModelDetailItem() {
        setContent(
            SmartphoneDetailsState(
                smartphone = ComposeDataTest.detailsUi,
                isLoading = false,
            ),
        )
        composeTestRule.onNodeWithText("Model").assertIsDisplayed()
    }

    @Test
    fun showsPriceDetailItemWithEuroSuffix() {
        setContent(
            SmartphoneDetailsState(
                smartphone = ComposeDataTest.detailsUi,
                isLoading = false,
            ),
        )
        composeTestRule.onNodeWithText("Price").assertIsDisplayed()
        composeTestRule.onNodeWithText("999.99 €").assertIsDisplayed()
    }

    @Test
    fun showsConstructionDateDetailItem() {
        setContent(
            SmartphoneDetailsState(
                smartphone = ComposeDataTest.detailsUi,
                isLoading = false,
            ),
        )
        composeTestRule.onNodeWithText("Construction Date").assertIsDisplayed()
        composeTestRule.onNodeWithText("25 Jan 2025").assertIsDisplayed()
    }

    @Test
    fun showsDescriptionHeadingAndBody() {
        setContent(
            SmartphoneDetailsState(
                smartphone = ComposeDataTest.detailsUi,
                isLoading = false,
            ),
        )
        composeTestRule.onNodeWithText("Description").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apple flagship smartphone").assertIsDisplayed()
    }

    @Test
    fun asyncImageExistsWithModelAsContentDescription() {
        setContent(
            SmartphoneDetailsState(
                smartphone = ComposeDataTest.detailsUi,
                isLoading = false,
            ),
        )
        composeTestRule.onNodeWithContentDescription("iPhone 15 Pro").assertExists()
    }

    // -- Action Callbacks ------------------------------------------

    @Test
    fun clickingRetryDispatchesRetryAction() {
        val captured = mutableListOf<DetailsActions>()
        setContent(
            state = SmartphoneDetailsState(isLoading = false, error = "Failed to load"),
            onAction = { captured.add(it) },
        )
        composeTestRule.onNodeWithText("Retry").performClick()
        assert(captured.firstOrNull() == DetailsActions.Retry)
    }

    @Test
    fun clickingBackButtonInvokesOnBackClick() {
        var backClicked = false
        setContent(
            state = SmartphoneDetailsState(isLoading = false),
            onBackClick = { backClicked = true },
        )
        composeTestRule.onNodeWithContentDescription("Back").performClick()
        assert(backClicked)
    }

    // -- State Transitions -----------------------------------------

    @Test
    fun switchingFromLoadingToLoadedShowsContent() {
        var state by mutableStateOf(SmartphoneDetailsState(isLoading = true))
        composeTestRule.setContent {
            MaterialTheme {
                SmartphoneDetailsScreen(
                    state = state,
                    snackBarHostState = snackbarHostState,
                    onAction = {},
                    onBackClick = {},
                )
            }
        }
        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertExists()

        state =
            SmartphoneDetailsState(isLoading = false, smartphone = ComposeDataTest.detailsUi)
        composeTestRule.waitForIdle()

        composeTestRule
            .onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo.Indeterminate))
            .assertDoesNotExist()
        composeTestRule.onNodeWithText("Model").assertIsDisplayed()
    }

    @Test
    fun switchingFromErrorToLoadedHidesRetry() {
        var state by mutableStateOf(
            SmartphoneDetailsState(
                isLoading = false,
                error = "Failed to load",
            ),
        )
        composeTestRule.setContent {
            MaterialTheme {
                SmartphoneDetailsScreen(
                    state = state,
                    snackBarHostState = snackbarHostState,
                    onAction = {},
                    onBackClick = {},
                )
            }
        }
        composeTestRule.onNodeWithText("Retry").assertIsDisplayed()

        state =
            SmartphoneDetailsState(isLoading = false, smartphone = ComposeDataTest.detailsUi)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Retry").assertDoesNotExist()
        composeTestRule.onNodeWithText("Model").assertIsDisplayed()
    }
}
