package fr.taoufikcode.presentation.details

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.taoufikcode.presentation.util.ComposeDataTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmartphoneDetailsScreenAccessibilityTest {
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

    @Test
    fun loadingIndicatorAnnouncesLoadingContentDescription() {
        setContent(SmartphoneDetailsState(isLoading = true))
        composeTestRule.onNodeWithContentDescription("Loading").assertExists()
    }

    @Test
    fun detailItemLabelAndValueAreMergedIntoSingleFocusNode() {
        setContent(
            SmartphoneDetailsState(smartphone = ComposeDataTest.detailsUi, isLoading = false),
        )
        composeTestRule.onNode(hasText("Model") and hasText("iPhone 15 Pro")).assertExists()
        composeTestRule.onNode(hasText("Price") and hasText("999.99 €")).assertExists()
        composeTestRule
            .onNode(
                hasText("Construction Date") and hasText("25 Jan 2025"),
            ).assertExists()
    }

    @Test
    fun descriptionSectionIsMarkedAsHeading() {
        setContent(
            SmartphoneDetailsState(smartphone = ComposeDataTest.detailsUi, isLoading = false),
        )
        composeTestRule
            .onNode(
                hasText("Description") and
                    SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading),
            ).assertExists()
    }
}
