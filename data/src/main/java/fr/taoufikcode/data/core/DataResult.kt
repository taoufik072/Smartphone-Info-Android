package fr.taoufikcode.data.core

sealed interface DataResult<out D, out E : AppError> {
    data class Success<out D>(
        val data: D,
    ) : DataResult<D, Nothing>

    data class Error<out E : AppError>(
        val error: E,
    ) : DataResult<Nothing, E>
}
