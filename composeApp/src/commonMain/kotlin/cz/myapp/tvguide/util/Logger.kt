package cz.myapp.tvguide.util

import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity

/**
 * Application-wide logger wrapper using Kermit.
 * 
 * Provides convenient logging methods with consistent tagging and formatting.
 * 
 * Usage:
 * ```kotlin
 * AppLogger.d("MyTag") { "Debug message" }
 * AppLogger.e("MyTag", throwable) { "Error message" }
 * ```
 */
object AppLogger {
    
    private val logger = Logger.withTag("TvGuide")
    
    /**
     * Log a verbose message.
     * 
     * @param tag Optional tag to identify the source
     * @param throwable Optional exception to log
     * @param message Message provider lambda
     */
    fun v(tag: String = "", throwable: Throwable? = null, message: () -> String) {
        logger.v(throwable, tag) { message() }
    }
    
    /**
     * Log a debug message.
     * 
     * @param tag Optional tag to identify the source
     * @param throwable Optional exception to log
     * @param message Message provider lambda
     */
    fun d(tag: String = "", throwable: Throwable? = null, message: () -> String) {
        logger.d(throwable, tag) { message() }
    }
    
    /**
     * Log an info message.
     * 
     * @param tag Optional tag to identify the source
     * @param throwable Optional exception to log
     * @param message Message provider lambda
     */
    fun i(tag: String = "", throwable: Throwable? = null, message: () -> String) {
        logger.i(throwable, tag) { message() }
    }
    
    /**
     * Log a warning message.
     * 
     * @param tag Optional tag to identify the source
     * @param throwable Optional exception to log
     * @param message Message provider lambda
     */
    fun w(tag: String = "", throwable: Throwable? = null, message: () -> String) {
        logger.w(throwable, tag) { message() }
    }
    
    /**
     * Log an error message.
     * 
     * @param tag Optional tag to identify the source
     * @param throwable Optional exception to log
     * @param message Message provider lambda
     */
    fun e(tag: String = "", throwable: Throwable? = null, message: () -> String) {
        logger.e(throwable, tag) { message() }
    }
    
    /**
     * Log an assertion failure.
     * 
     * @param tag Optional tag to identify the source
     * @param throwable Optional exception to log
     * @param message Message provider lambda
     */
    fun a(tag: String = "", throwable: Throwable? = null, message: () -> String) {
        logger.a(throwable, tag) { message() }
    }
    
    /**
     * Set minimum log severity level.
     * 
     * @param severity Minimum severity to log (Verbose, Debug, Info, Warn, Error, Assert)
     */
    fun setMinSeverity(severity: Severity) {
        Logger.setMinSeverity(severity)
    }
}
