package fr.taoufikcode.common.logger

import co.touchlab.kermit.Logger as KLogger

internal class KermitAppLogger : AppLogger {
    override fun d(
        tag: String,
        message: () -> String,
    ) = KLogger.withTag(tag).d(message())

    override fun i(
        tag: String,
        message: () -> String,
    ) = KLogger.withTag(tag).i(message())

    override fun w(
        tag: String,
        message: () -> String,
    ) = KLogger.withTag(tag).w(message())

    override fun e(
        tag: String,
        throwable: Throwable?,
        message: () -> String,
    ) = KLogger.withTag(tag).e(message(), throwable = throwable)
}
