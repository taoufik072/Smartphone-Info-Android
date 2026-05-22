package fr.taoufikcode.presentation.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import fr.taoufikcode.presentation.common.EmptyState
import fr.taoufikcode.presentation.common.ErrorState
import fr.taoufikcode.presentation.common.LoadingState
import fr.taoufikcode.presentation.common.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

private val ListContentPadding = 16.dp
private val ItemPadding = 12.dp
private val ThumbnailSize = 80.dp

@Composable
fun SmartphoneListScreenRoot(
    viewModel: SmartphoneListViewModel = koinViewModel(),
    onNavigate: (String) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    @Suppress("BracesOnWhenStatements")
    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SmartphoneListEvent.ShowError -> {
                snackBarHostState.showSnackbar(event.message)
            }
        }
    }

    SmartphoneListScreen(
        state = state,
        snackBarHostState = snackBarHostState,
        onAction = { onAction ->
            when (onAction) {
                is ListActions.SmartphoneClick -> onNavigate(onAction.smartphoneId)
                else -> Unit
            }
            viewModel.onAction(onAction)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SmartphoneListScreen(
    state: SmartphoneListState,
    snackBarHostState: SnackbarHostState,
    onAction: (ListActions) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Smartphones") })
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
        content = { padding ->
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding),
            ) {
                PullToRefreshBox(
                    isRefreshing = state.isRefreshing,
                    onRefresh = { onAction(ListActions.Refresh) },
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when {
                        state.isLoading -> {
                            LoadingState()
                        }

                        state.error != null -> {
                            ErrorState(
                                message = state.error,
                                onRetry = { onAction(ListActions.Refresh) },
                            )
                        }

                        state.isEmpty -> {
                            EmptyState(message = "No smartphones found")
                        }

                        else -> {
                            SmartphoneList(
                                smartphones = state.smartphones,
                                onAction = onAction,
                            )
                        }
                    }
                }
            }
        },
    )
}

@Composable
private fun SmartphoneList(
    smartphones: List<SmartphoneSummaryUi>,
    onAction: (ListActions) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(ListContentPadding),
        verticalArrangement = Arrangement.spacedBy(ItemPadding),
    ) {
        items(items = smartphones, key = { it.id }) { smartphone ->
            SmartphoneListItem(
                smartphone = smartphone,
                onClick = {
                    onAction(ListActions.SmartphoneClick(smartphone.id))
                },
            )
        }
    }
}

@Composable
private fun SmartphoneListItem(
    smartphone: SmartphoneSummaryUi,
    onClick: () -> Unit,
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(ItemPadding),
            horizontalArrangement = Arrangement.spacedBy(ItemPadding),
        ) {
            AsyncImage(
                modifier = Modifier.size(ThumbnailSize),
                model = smartphone.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Text(
                modifier = Modifier.weight(1f),
                text = smartphone.model,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SmartphoneListPreview() {
    val smartphones =
        listOf(
            SmartphoneSummaryUi(
                id = "1",
                model = "iPhone 15 Pro",
                imageUrl = "iphone15.jpg",
            ),
            SmartphoneSummaryUi(
                id = "2",
                model = "samsung s24 Pro",
                imageUrl = "iphone15.jpg",
            ),
            SmartphoneSummaryUi(
                id = "3",
                model = "Google Pixel 9",
                imageUrl = "iphone15.jpg",
            ),
        )
    MaterialTheme {
        Surface {
            SmartphoneList(smartphones = smartphones, onAction = {})
        }
    }
}
