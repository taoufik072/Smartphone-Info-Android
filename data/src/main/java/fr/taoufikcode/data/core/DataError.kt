package fr.taoufikcode.data.core

sealed interface DataError : AppError {
    enum class Remote : DataError {
        REQUEST_TIMEOUT,
        TOO_MANY_REQUESTS,
        NO_INTERNET,
        SERVER,
        SERIALIZATION,
        UNKNOWN,
    }

    enum class Local : DataError {
        DISK_FULL,
        UNKNOWN,
    }
}

fun DataError.Remote.toDomain(): String =
    when (this) {
        DataError.Remote.REQUEST_TIMEOUT -> "Request timed out. Please try again."
        DataError.Remote.TOO_MANY_REQUESTS -> "Too many requests. Please wait and retry."
        DataError.Remote.NO_INTERNET -> "No internet connection."
        DataError.Remote.SERVER -> "Server error. Please try again later."
        DataError.Remote.SERIALIZATION -> "Unexpected data format received."
        DataError.Remote.UNKNOWN -> "An unknown error occurred."
    }

fun DataError.Local.toDomain(): String =
    when (this) {
        DataError.Local.DISK_FULL -> "Not enough storage space. Please free up space and try again."
        DataError.Local.UNKNOWN -> "A local storage error occurred. Please try again."
    }
