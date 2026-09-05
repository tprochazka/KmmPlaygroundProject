package cz.myapp.tvguide.domain.repository

import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.model.ProgramCast
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant
import kotlin.time.Clock

/**
 * Repository for program data.
 * 
 * Provides access to TV program information and schedules.
 */
interface ProgramRepository {
    
    /**
     * Get programs currently airing on a channel.
     * 
     * @param channelId The channel ID
     * @param time The time to check (defaults to now)
     * @return Flow of the current program, or null if none
     */
    fun getCurrentProgram(
        channelId: String,
    time: Instant = Clock.System.now()
    ): Flow<Program?>
    
    /**
     * Get programs currently airing on multiple channels.
     * 
     * @param channelIds List of channel IDs
     * @param time The time to check (defaults to now)
     * @return Flow of map from channel ID to current program
     */
    fun getCurrentPrograms(
        channelIds: List<String>,
    time: Instant = Clock.System.now()
    ): Flow<Map<String, Program?>>
    
    /**
     * Get program schedule for a channel within a time range.
     * 
     * @param channelId The channel ID
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @return Flow of programs in the time range, ordered by start time
     */
    fun getProgramsForChannel(
        channelId: String,
        startTime: Instant,
        endTime: Instant
    ): Flow<List<Program>>
    
    /**
     * Get EPG data for multiple channels within a time range.
     * 
     * @param channelIds List of channel IDs
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @return Flow of map from channel ID to programs list
     */
    fun getEpgData(
        channelIds: List<String>,
        startTime: Instant,
        endTime: Instant
    ): Flow<Map<String, List<Program>>>
    
    /**
     * Search programs by title.
     * 
     * @param query Search query (case-insensitive)
     * @param startTime Optional start time filter
     * @param endTime Optional end time filter
     * @return Flow of programs matching the query
     */
    fun searchPrograms(
        query: String,
        startTime: Instant? = null,
        endTime: Instant? = null
    ): Flow<List<Program>>
    
    /**
     * Get programs chronologically across all channels.
     * 
     * Useful for "what's on TV now and next" views.
     * 
     * @param channelIds List of channel IDs to include
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @return Flow of programs ordered by start time
     */
    fun getProgramsChronologically(
        channelIds: List<String>,
        startTime: Instant,
        endTime: Instant
    ): Flow<List<Program>>
    
    /**
     * Get programs by time range for a specific channel.
     * 
     * @param channelId The channel ID
     * @param startTime Start of the time range
     * @param endTime End of the time range
     * @return Flow of programs in the time range
     */
    fun getProgramsByTimeRange(
        channelId: String,
        startTime: Instant,
        endTime: Instant
    ): Flow<List<Program>>
    
    /**
     * Get cast and crew information for a program.
     * 
     * @param programId The program ID
     * @return Program cast information (mock data in prototype phase)
     */
    suspend fun getProgramCast(programId: String): ProgramCast
    
    /**
     * Get a single program by ID (suspend version).
     * 
     * @param programId The program ID
     * @return The program, or null if not found
     */
    suspend fun getProgramById(programId: String): Program?
    
    /**
     * Get similar programs based on genre, cast, or keywords.
     * 
     * @param programId The reference program ID
     * @param limit Maximum number of similar programs to return
     * @return List of similar programs
     */
    suspend fun getSimilarPrograms(programId: String, limit: Int): List<Program>
    
    /**
     * Get programs featuring a specific cast member.
     * 
     * @param castMemberId The cast member ID
     * @param limit Maximum number of programs to return
     * @return List of programs featuring this cast member
     */
    suspend fun getProgramsByCastMember(castMemberId: String, limit: Int): List<Program>
}
