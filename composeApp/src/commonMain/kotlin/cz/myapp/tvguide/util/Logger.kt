package cz.myapp.tvguide.util

/**
 * Lightweight logger wrapper (println-based) for deterministic console output across KMP targets.
 * Replaces Kermit temporarily to debug data flow issues.
 */
object AppLogger {
    private fun log(level: String, tag: String, msg: String, throwable: Throwable?) {
        val tagPart = if (tag.isNotBlank()) "$tag: " else ""
        val throwablePart = throwable?.let { "\n" + it.stackTraceToString() } ?: ""
        println("[$level] ${tagPart}${msg}${throwablePart}")
    }

    fun v(tag: String = "", throwable: Throwable? = null, message: () -> String) =
        log("VERBOSE", tag, message(), throwable)
    fun d(tag: String = "", throwable: Throwable? = null, message: () -> String) =
        log("DEBUG", tag, message(), throwable)
    fun i(tag: String = "", throwable: Throwable? = null, message: () -> String) =
        log("INFO", tag, message(), throwable)
    fun w(tag: String = "", throwable: Throwable? = null, message: () -> String) =
        log("WARN", tag, message(), throwable)
    fun e(tag: String = "", throwable: Throwable? = null, message: () -> String) =
        log("ERROR", tag, message(), throwable)
    fun a(tag: String = "", throwable: Throwable? = null, message: () -> String) =
        log("ASSERT", tag, message(), throwable)
}
