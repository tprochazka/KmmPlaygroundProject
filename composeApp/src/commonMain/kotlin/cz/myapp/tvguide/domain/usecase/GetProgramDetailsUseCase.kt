package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.model.ProgramCast
import cz.myapp.tvguide.domain.repository.ProgramRepository

/**
 * Use case to retrieve detailed program information including cast and crew.
 */
class GetProgramDetailsUseCase(
    private val programRepository: ProgramRepository
) {
    /**
     * Get detailed program information.
     * 
     * @param programId The program ID
     * @return Pair of Program and its cast/crew, or null if not found
     */
    suspend operator fun invoke(programId: String): Pair<Program, ProgramCast>? {
        // Note: In prototype phase, cast data will be mocked in repository layer
        // Future: This will fetch from API with full cast details
        
        val program = programRepository.getProgramById(programId)
        if (program == null) return null
        
        val cast = programRepository.getProgramCast(programId)
        
        return program to cast
    }
}
