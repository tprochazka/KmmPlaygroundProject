package cz.myapp.tvguide.data.mock

import kotlinx.coroutines.delay
import kotlin.random.Random

/**
 * Utility for simulating network delays in mock repositories.
 * 
 * Provides realistic delay simulation for prototype testing.
 */
object DelaySimulator {
    
    /**
     * Simulate a random network delay.
     * 
     * @param minMs Minimum delay in milliseconds (default 100)
     * @param maxMs Maximum delay in milliseconds (default 300)
     */
    suspend fun randomDelay(minMs: Long = 100, maxMs: Long = 300) {
        val delayMs = Random.nextLong(minMs, maxMs + 1)
        delay(delayMs)
    }
    
    /**
     * Simulate a fixed network delay.
     * 
     * @param delayMs Delay in milliseconds
     */
    suspend fun fixedDelay(delayMs: Long = 200) {
        delay(delayMs)
    }
    
    /**
     * Simulate a delay for a "fast" operation (e.g., cache hit).
     * 
     * Default: 50-100ms
     */
    suspend fun fastDelay() {
        randomDelay(minMs = 50, maxMs = 100)
    }
    
    /**
     * Simulate a delay for a "slow" operation (e.g., large data fetch).
     * 
     * Default: 500-1000ms (max 1s)
     */
    suspend fun slowDelay() {
        randomDelay(minMs = 500, maxMs = 1000)
    }
}
