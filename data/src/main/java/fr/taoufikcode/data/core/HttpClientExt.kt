package fr.taoufikcode.data.core

import fr.taoufikcode.common.logger.LoggerDelegate
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.call.body
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.statement.HttpResponse
import java.net.UnknownHostException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

const val HTTP_OK_MIN = 200
const val HTTP_OK_MAX = 299
const val HTTP_REQUEST_TIMEOUT = 408
const val HTTP_TOO_MANY_REQUESTS = 429
const val HTTP_SERVER_ERROR_MIN = 500
const val HTTP_SERVER_ERROR_MAX = 599

@Suppress("ReturnCount", "TooGenericExceptionCaught")
suspend inline fun <reified T> safeCall(
    execute: suspend () -> HttpResponse,
): DataResult<T, DataError.Remote> {
    val tag = T::class.simpleName ?: "safeCall"
    val response =
        try {
            execute()
        } catch (e: SocketTimeoutException) {
            LoggerDelegate.e(tag) { "call error: ${DataError.Remote.REQUEST_TIMEOUT}" }
            return DataResult.Error(DataError.Remote.REQUEST_TIMEOUT)
        } catch (e: UnknownHostException) {
            LoggerDelegate.e(tag) { "call error: ${DataError.Remote.NO_INTERNET}" }
            return DataResult.Error(DataError.Remote.NO_INTERNET)
        } catch (e: Exception) {
            currentCoroutineContext().ensureActive()
            LoggerDelegate.e(tag, e) { "call error: ${DataError.Remote.UNKNOWN}" }
            return DataResult.Error(DataError.Remote.UNKNOWN)
        }

    return responseToResult(response)
}

suspend inline fun <reified T> responseToResult(
    response: HttpResponse,
): DataResult<T, DataError.Remote> {
    val tag = T::class.simpleName ?: "responseToResult"
    return when (response.status.value) {
        in HTTP_OK_MIN..HTTP_OK_MAX -> {
            try {
                DataResult.Success(response.body<T>())
            } catch (_: NoTransformationFoundException) {
                DataResult.Error(DataError.Remote.SERIALIZATION)
            }
        }

        HTTP_REQUEST_TIMEOUT -> {
            DataResult.Error(DataError.Remote.REQUEST_TIMEOUT)
        }

        HTTP_TOO_MANY_REQUESTS -> {
            DataResult.Error(DataError.Remote.TOO_MANY_REQUESTS)
        }

        in HTTP_SERVER_ERROR_MIN..HTTP_SERVER_ERROR_MAX -> {
            DataResult.Error(DataError.Remote.SERVER)
        }

        else -> {
            DataResult.Error(DataError.Remote.UNKNOWN)
        }
    }.also { result ->
        if (result is DataResult.Error) LoggerDelegate.e(tag) { "call error: ${result.error}" }
    }
}
