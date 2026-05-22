package fr.taoufikcode.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import fr.taoufikcode.domain.usecase.smartphone.GetSmartphoneDetailsByIdUseCase
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.koin.core.annotation.KoinViewModel

@KoinViewModel
class SmartphoneDetailsViewModel(
    private val getSmartphoneDetailsByIdUseCase: GetSmartphoneDetailsByIdUseCase,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val smartphoneId: String? = savedStateHandle["smartphoneId"]
    private val _state = MutableStateFlow(SmartphoneDetailsState())
    val state: StateFlow<SmartphoneDetailsState> = _state.asStateFlow()

    private val _events = Channel<SmartphoneDetailsEvent>()
    val events = _events.receiveAsFlow()

    init {
        if (smartphoneId != null) {
            loadSmartphone()
        } else {
            _state.update { it.copy(isLoading = false, error = "Smartphone not found") }
        }
    }

    fun onAction(action: DetailsActions) {
        when (action) {
            DetailsActions.Retry -> if (smartphoneId != null) loadSmartphone()
        }
    }

    private fun loadSmartphone() {
        val id = smartphoneId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            getSmartphoneDetailsByIdUseCase(id)
                .onSuccess { smartphone ->
                    _state.update {
                        it.copy(
                            smartphone = smartphone.toUi(),
                            isLoading = false,
                            error = null,
                        )
                    }
                }.onFailure { exception ->
                    val message = exception.message ?: "Failed to load smartphone"
                    _state.update { it.copy(isLoading = false, error = message) }
                    _events.trySend(SmartphoneDetailsEvent.ShowError(message))
                }
        }
    }
}
