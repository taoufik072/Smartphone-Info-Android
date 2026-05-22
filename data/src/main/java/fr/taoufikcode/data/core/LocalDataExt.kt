package fr.taoufikcode.data.core

import android.database.sqlite.SQLiteFullException
import fr.taoufikcode.common.logger.LoggerDelegate
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive

private const val TAG = "safeLocalCall"

@Suppress("ReturnCount", "TooGenericExceptionCaught")
suspend fun <T> safeLocalCall(execute: suspend () -> T): DataResult<T, DataError.Local> =
    try {
        DataResult.Success(execute())
    } catch (e: SQLiteFullException) {
        LoggerDelegate.e(TAG, e) { "local call error: ${DataError.Local.DISK_FULL}" }
        DataResult.Error(DataError.Local.DISK_FULL)
    } catch (e: Exception) {
        currentCoroutineContext().ensureActive()
        LoggerDelegate.e(TAG, e) { "local call error: ${DataError.Local.UNKNOWN}" }
        DataResult.Error(DataError.Local.UNKNOWN)
    }
