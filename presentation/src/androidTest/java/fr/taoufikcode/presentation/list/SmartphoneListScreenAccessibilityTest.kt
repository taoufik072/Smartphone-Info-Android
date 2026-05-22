package fr.taoufikcode.presentation.list

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import fr.taoufikcode.presentation.util.ComposeDataTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmartphoneListScreenAccessibilityTest {
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

    @Test
    fun loadingIndicatorAnnouncesLoadingContentDescription() {
        setContent(SmartphoneListState(isLoading = true))
        composeTestRule.onNodeWithContentDescription("Loading").assertExists()
    }

    @Test
    fun listItemCardIsMergedIntoSingleAccessibleNodeWithClickAction() {
        setContent(
            SmartphoneListState(
                isLoading = false,
                smartphones = listOf(ComposeDataTest.summaryItem1),
            ),
        )
        composeTestRule.onNode(hasClickAction()).assertExists()
        composeTestRule.onNodeWithText("iPhone 15 Pro").assertHasClickAction()
    }
}
