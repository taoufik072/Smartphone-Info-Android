package fr.taoufikcode.common.logger

object LoggerDelegate : AppLogger {
    private var impl: AppLogger = KermitAppLogger()

    fun install(logger: AppLogger) {
        impl = logger
    }

    override fun d(
        tag: String,
        message: () -> String,
    ) = impl.d(tag, message)

    override fun i(
        tag: String,
        message: () -> String,
    ) = impl.i(tag, message)

    override fun w(
        tag: String,
        message: () -> String,
    ) = impl.w(tag, message)

    override fun e(
        tag: String,
        throwable: Throwable?,
        message: () -> String,
    ) = impl.e(tag, throwable, message)
}
