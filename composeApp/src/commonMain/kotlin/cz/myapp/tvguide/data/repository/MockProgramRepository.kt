package cz.myapp.tvguide.data.repository

import cz.myapp.tvguide.data.mock.DelaySimulator
import cz.myapp.tvguide.data.mock.MockPrograms
import cz.myapp.tvguide.domain.model.CastMember
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.model.ProgramCast
import cz.myapp.tvguide.domain.repository.ProgramRepository
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.time.Clock
import kotlin.time.Instant

/**
 * Mock implementation of ProgramRepository for Phase 1 prototype.
 * 
 * Uses in-memory generated program data with simulated network delays.
 */
class MockProgramRepository : ProgramRepository {
    
    private val tag = "MockProgramRepository"
    
    override fun getCurrentProgram(channelId: String, time: Instant): Flow<Program?> = flow {
        AppLogger.d(tag) { "getCurrentProgram: channelId=$channelId, time=$time" }
        DelaySimulator.fastDelay()
        
        val program = MockPrograms.all.find { program ->
            program.channelId == channelId &&
            time >= program.startTime &&
            time < program.endTime
        }
        
        AppLogger.d(tag) { "getCurrentProgram: Found program: ${program?.title}" }
        emit(program)
    }
    
    override fun getCurrentPrograms(
        channelIds: List<String>,
        time: Instant
    ): Flow<Map<String, Program?>> = flow {
        AppLogger.d(tag) { "getCurrentPrograms: ${channelIds.size} channels, time=$time" }
        DelaySimulator.randomDelay()
        
        val programMap = channelIds.associateWith { channelId ->
            MockPrograms.all.find { program ->
                program.channelId == channelId &&
                time >= program.startTime &&
                time < program.endTime
            }
        }
        
        val foundCount = programMap.values.count { it != null }
        AppLogger.d(tag) { "getCurrentPrograms: Found $foundCount programs" }
        emit(programMap)
    }
    
    override fun getProgramsForChannel(
        channelId: String,
        startTime: Instant,
        endTime: Instant
    ): Flow<List<Program>> = flow {
        AppLogger.d(tag) { "getProgramsForChannel: channelId=$channelId, range=$startTime to $endTime" }
        DelaySimulator.randomDelay()
        
        val programs = MockPrograms.all.filter { program ->
            program.channelId == channelId &&
            program.startTime < endTime &&
            program.endTime > startTime
        }.sortedBy { it.startTime }
        
        AppLogger.d(tag) { "getProgramsForChannel: Returning ${programs.size} programs" }
        emit(programs)
    }
    
    override fun getEpgData(
        channelIds: List<String>,
        startTime: Instant,
        endTime: Instant
    ): Flow<Map<String, List<Program>>> = flow {
        AppLogger.d(tag) { "getEpgData: ${channelIds.size} channels, range=$startTime to $endTime" }
        DelaySimulator.slowDelay() // EPG data is large, simulate slower load
        
        val epgMap = channelIds.associateWith { channelId ->
            MockPrograms.all.filter { program ->
                program.channelId == channelId &&
                program.startTime < endTime &&
                program.endTime > startTime
            }.sortedBy { it.startTime }
        }
        
        val totalPrograms = epgMap.values.sumOf { it.size }
        AppLogger.d(tag) { "getEpgData: Returning $totalPrograms programs across ${channelIds.size} channels" }
        emit(epgMap)
    }
    
    override fun searchPrograms(
        query: String,
        startTime: Instant?,
        endTime: Instant?
    ): Flow<List<Program>> = flow {
        AppLogger.d(tag) { "searchPrograms: query='$query', time range=$startTime to $endTime" }
        DelaySimulator.randomDelay()
        
        val normalizedQuery = query.lowercase().trim()
        var programs = if (normalizedQuery.isEmpty()) {
            MockPrograms.all
        } else {
            MockPrograms.all.filter { program ->
                program.title.lowercase().contains(normalizedQuery) ||
                program.description.lowercase().contains(normalizedQuery) ||
                program.subtitle?.lowercase()?.contains(normalizedQuery) == true
            }
        }
        
        // Apply time filters if provided
        if (startTime != null) {
            programs = programs.filter { it.startTime >= startTime }
        }
        if (endTime != null) {
            programs = programs.filter { it.endTime <= endTime }
        }
        
        programs = programs.sortedBy { it.startTime }
        
        AppLogger.d(tag) { "searchPrograms: Returning ${programs.size} programs" }
        emit(programs)
    }
    
    override fun getProgramsChronologically(
        channelIds: List<String>,
        startTime: Instant,
        endTime: Instant
    ): Flow<List<Program>> = flow {
        AppLogger.d(tag) { "getProgramsChronologically: ${channelIds.size} channels, range=$startTime to $endTime" }
        DelaySimulator.randomDelay()
        
        val programs = MockPrograms.all.filter { program ->
            program.channelId in channelIds &&
            program.startTime < endTime &&
            program.endTime > startTime
        }.sortedBy { it.startTime }
        
        AppLogger.d(tag) { "getProgramsChronologically: Returning ${programs.size} programs" }
        emit(programs)
    }
    
    override fun getProgramsByTimeRange(
        channelId: String,
        startTime: Instant,
        endTime: Instant
    ): Flow<List<Program>> = flow {
        AppLogger.d(tag) { "getProgramsByTimeRange: channelId=$channelId, range=$startTime to $endTime" }
        DelaySimulator.randomDelay()
        
        val programs = MockPrograms.all.filter { program ->
            program.channelId == channelId &&
            program.startTime < endTime &&
            program.endTime > startTime
        }.sortedBy { it.startTime }
        
        AppLogger.d(tag) { "getProgramsByTimeRange: Returning ${programs.size} programs" }
        emit(programs)
    }
    
    override suspend fun getProgramCast(programId: String): ProgramCast {
        AppLogger.d(tag) { "getProgramCast: programId=$programId" }
        DelaySimulator.fastDelay()
        
        // Mock cast data - in real implementation, this would come from API
        val mockCast = listOf(
            CastMember(
                id = "cast_1",
                name = "Jan Novák",
                role = "Actor",
                character = "Hlavní role",
                photoUrl = "https://via.placeholder.com/150"
            ),
            CastMember(
                id = "cast_2",
                name = "Eva Nová",
                role = "Actor",
                character = "Vedlejší role",
                photoUrl = "https://via.placeholder.com/150"
            )
        )
        
        val mockDirector = CastMember(
            id = "dir_1",
            name = "Pavel Svoboda",
            role = "Director",
            photoUrl = "https://via.placeholder.com/150"
        )
        
        return ProgramCast(
            programId = programId,
            cast = mockCast,
            directors = listOf(mockDirector)
        )
    }
    
    override suspend fun getProgramById(programId: String): Program? {
        AppLogger.d(tag) { "getProgramById (suspend): programId=$programId" }
        DelaySimulator.fastDelay()
        
        val program = MockPrograms.all.find { it.id == programId }
        AppLogger.d(tag) { "getProgramById (suspend): Found program: ${program?.title}" }
        return program
    }
    
    override suspend fun getSimilarPrograms(programId: String, limit: Int): List<Program> {
        AppLogger.d(tag) { "getSimilarPrograms: programId=$programId, limit=$limit" }
        DelaySimulator.randomDelay()
        
        val program = MockPrograms.all.find { it.id == programId }
        if (program == null) {
            AppLogger.d(tag) { "getSimilarPrograms: Program not found" }
            return emptyList()
        }
        
        // Find similar programs by matching genres
        val similar = MockPrograms.all.filter { other ->
            other.id != programId &&
            other.genres.any { genre -> genre in program.genres }
        }.take(limit)
        
        AppLogger.d(tag) { "getSimilarPrograms: Returning ${similar.size} similar programs" }
        return similar
    }
    
    override suspend fun getProgramsByCastMember(castMemberId: String, limit: Int): List<Program> {
        AppLogger.d(tag) { "getProgramsByCastMember: castMemberId=$castMemberId, limit=$limit" }
        DelaySimulator.randomDelay()
        
        // Mock implementation - in real app would filter by cast member ID
        // For prototype, just return some random programs
        val programs = MockPrograms.all.shuffled().take(limit)
        
        AppLogger.d(tag) { "getProgramsByCastMember: Returning ${programs.size} programs" }
        return programs
    }
}
