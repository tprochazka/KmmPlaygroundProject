package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.repository.ChannelRepository
import cz.myapp.tvguide.domain.repository.ProgramRepository
import cz.myapp.tvguide.util.AppLogger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

/**
 * Use case to get current programs for channels.
 * 
 * Returns list of channel-program pairs showing what's currently airing.
 * Handles errors gracefully and provides result wrapper.
 * 
 * US1: As a user, I want to see what's currently on TV across all channels.
 */
class GetCurrentProgramsUseCase(
    private val channelRepository: ChannelRepository,
    private val programRepository: ProgramRepository
) {
    /**
     * Get current programs for all channels.
     * 
     * @return Flow of Result with list of (Channel, Program?) pairs.
     *         Program is null if no current program found for channel.
     */
    operator fun invoke(): Flow<Result<List<Pair<Channel, Program?>>>> {
        return channelRepository.getAllChannels()
            .flatMapLatest { channels ->
                val channelIds = channels.map { it.id }
                programRepository.getCurrentPrograms(channelIds)
                    .map { currentPrograms ->
                        AppLogger.d("GetCurrentProgramsUseCase") { "Channels: ${channels.size}, Programs: ${currentPrograms.size}" }
                        
                        // Create pairs of channels with their current programs
                        val pairs = channels.map { channel ->
                            val program = currentPrograms[channel.id]
                            channel to program
                        }
                        
                        Result.success(pairs)
                    }
            }
            .catch { e ->
                AppLogger.e("GetCurrentProgramsUseCase", e) { "Error getting current programs" }
                emit(Result.failure(e))
            }
    }
    
    /**
     * Get current programs for specific channels only.
     * 
     * @param channelIds List of channel IDs to filter by
     * @return Flow of Result with filtered list of (Channel, Program?) pairs
     */
    fun forChannels(channelIds: List<String>): Flow<Result<List<Pair<Channel, Program?>>>> {
        return channelRepository.getAllChannels()
            .flatMapLatest { channels ->
                AppLogger.d("GetCurrentProgramsUseCase") { "forChannels: repository returned ${channels.size} total channels" }
                programRepository.getCurrentPrograms(channelIds)
                    .map { currentPrograms ->
                        AppLogger.d("GetCurrentProgramsUseCase") { "Filtering for ${channelIds.size} channels" }
                        AppLogger.d("GetCurrentProgramsUseCase") { "Program map size=${currentPrograms.size} keys=${currentPrograms.keys.take(5)}..." }
                        
                        // Filter channels by ID and pair with programs
                        val pairs = channels
                            .filter { it.id in channelIds }
                            .map { channel ->
                                val program = currentPrograms[channel.id]
                                AppLogger.d("GetCurrentProgramsUseCase") {
                                    "Pair channel='${channel.id}' program='${program?.title ?: "NONE"}' start='${program?.startTime}' end='${program?.endTime}'"
                                }
                                channel to program
                            }
                        
                        AppLogger.i("GetCurrentProgramsUseCase") { "forChannels result: ${pairs.count { it.second != null }} programs / ${pairs.size} channels" }
                        Result.success(pairs)
                    }
            }
            .catch { e ->
                AppLogger.e("GetCurrentProgramsUseCase", e) { "Error getting current programs for channels" }
                emit(Result.failure(e))
            }
    }
}
