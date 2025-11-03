package cz.myapp.tvguide.domain.model

/**
 * Represents a cast or crew member in a program.
 */
data class CastMember(
    val id: String,
    val name: String,
    val role: String, // e.g., "Actor", "Director", "Writer"
    val character: String? = null, // Character name if actor
    val photoUrl: String? = null,
    val biography: String? = null
)

/**
 * Represents the cast and crew of a program.
 */
data class ProgramCast(
    val programId: String,
    val cast: List<CastMember> = emptyList(), // Actors
    val directors: List<CastMember> = emptyList(),
    val writers: List<CastMember> = emptyList(),
    val producers: List<CastMember> = emptyList()
) {
    /**
     * Get all cast and crew members combined.
     */
    fun getAllMembers(): List<CastMember> = 
        cast + directors + writers + producers
}
