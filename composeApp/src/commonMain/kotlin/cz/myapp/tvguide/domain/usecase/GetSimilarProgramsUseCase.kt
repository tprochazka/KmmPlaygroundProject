package cz.myapp.tvguide.domain.usecase

import cz.myapp.tvguide.domain.model.Program
import cz.myapp.tvguide.domain.repository.ProgramRepository

/**
 * Use case to retrieve similar programs based on genre, cast, or keywords.
 */
class GetSimilarProgramsUseCase(
    private val programRepository: ProgramRepository
) {
    /**
     * Get similar programs to the specified program.
     * 
     * @param programId The reference program ID
     * @param limit Maximum number of similar programs to return (default 10)
     * @return List of similar programs
     */
    suspend operator fun invoke(
        programId: String,
        limit: Int = 10
    ): List<Program> {
        // Note: In prototype phase, similarity will be based on genre matching
        // Future: Use ML-based recommendations or API-provided similar content
        
        val program = programRepository.getProgramById(programId)
            ?: return emptyList()
        
        return programRepository.getSimilarPrograms(programId, limit)
    }
}
