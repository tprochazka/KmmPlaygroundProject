package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.repository.ChannelRepository
import cz.myapp.tvguide.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.first

/**
 * Use case to retrieve programs featuring a specific cast member.
 */
class GetProgramsByCastMemberUseCase(
    private val programRepository: ProgramRepository,
    private val channelRepository: ChannelRepository
) {
    /**
     * Get all programs featuring the specified cast member.
     * 
     * @param castMemberId The cast member ID
     * @param limit Maximum number of programs to return (default 20)
     * @return List of program-channel pairs featuring this cast member
     */
    suspend operator fun invoke(
        castMemberId: String,
        limit: Int = 20
    ): List<Pair<Channel, Program>> {
        // Note: In prototype phase, this will use simple name matching in mock data
        // Future: Use proper cast member IDs from API
        
        val programs = programRepository.getProgramsByCastMember(castMemberId, limit)
        
        // Get channels for each program
        val channelsById = channelRepository.getAllChannels().first()
            .associateBy { it.id }
        
        return programs.mapNotNull { program ->
            val channel = channelsById[program.channelId]
            if (channel != null) {
                channel to program
            } else {
                null
            }
        }
    }
}
