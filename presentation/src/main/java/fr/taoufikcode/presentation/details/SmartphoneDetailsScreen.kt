package fr.taoufikcode.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import fr.taoufikcode.presentation.common.ErrorState
import fr.taoufikcode.presentation.common.LoadingState
import fr.taoufikcode.presentation.common.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

private val ScreenPadding = 16.dp
private val SpacerMedium = 8.dp
private val SpacerSmall = 4.dp
private const val IMAGE_ASPECT_RATIO_WIDTH = 16f
private const val IMAGE_ASPECT_RATIO_HEIGHT = 9f

@Composable
fun SmartphoneDetailsScreenRoot(
    viewModel: SmartphoneDetailsViewModel = koinViewModel(),
    onBackClick: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackBarHostState = remember { SnackbarHostState() }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is SmartphoneDetailsEvent.ShowError -> snackBarHostState.showSnackbar(event.message)
        }
    }

    SmartphoneDetailsScreen(
        state = state,
        snackBarHostState = snackBarHostState,
        onAction = viewModel::onAction,
        onBackClick = onBackClick,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmartphoneDetailsScreen(
    state: SmartphoneDetailsState,
    snackBarHostState: SnackbarHostState,
    onAction: (DetailsActions) -> Unit,
    onBackClick: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.smartphone?.model ?: "Details") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackBarHostState) },
    ) { padding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(padding),
        ) {
            when {
                state.isLoading -> {
                    LoadingState()
                }

                state.error != null -> {
                    ErrorState(
                        message = state.error,
                        onRetry = { onAction(DetailsActions.Retry) },
                    )
                }

                state.smartphone != null -> {
                    SmartphoneDetails(state.smartphone)
                }
            }
        }
    }
}

@Composable
private fun SmartphoneDetails(smartphone: SmartphoneDetailsUi) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(ScreenPadding),
        verticalArrangement = Arrangement.spacedBy(ScreenPadding),
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) {
            AsyncImage(
                model = smartphone.imageUrl,
                contentDescription = smartphone.model,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .aspectRatio(IMAGE_ASPECT_RATIO_WIDTH / IMAGE_ASPECT_RATIO_HEIGHT),
                contentScale = ContentScale.Crop,
            )
        }
        DetailItem(label = "Model", value = smartphone.model)
        DetailItem(label = "Price", value = "${smartphone.price} €")
        DetailItem(label = "Construction Date", value = smartphone.constructionDate)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
        ) {
            Column(
                modifier = Modifier
                    .padding(ScreenPadding)
                    .semantics(mergeDescendants = true) {},
            ) {
                Text(
                    text = "Description",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.semantics { heading() },
                )
                Spacer(modifier = Modifier.height(SpacerMedium))
                Text(
                    text = smartphone.description,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun DetailItem(
    label: String,
    value: String,
) {
    Column(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(SpacerSmall))
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SmartphoneDetailsPreview() {
    MaterialTheme {
        Surface {
            SmartphoneDetails(
                smartphone =
                    SmartphoneDetailsUi(
                        id = "1",
                        model = "iPhone 15 Pro",
                        imageUrl = "https://example.com/iphone.jpg",
                        price = 999.99,
                        constructionDate = "25 Jan 2025",
                        description = "iPhone 15 Pro",
                    ),
            )
        }
    }
}
