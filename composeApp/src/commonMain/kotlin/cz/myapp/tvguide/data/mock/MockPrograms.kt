package cz.myapp.tvguide.data.mock

import cz.myapp.tvguide.domain.model.*
import kotlinx.datetime.*
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.datetime.TimeZone
import kotlin.random.Random

/**
 * Mock program data generator for Phase 1 prototype.
 * 
 * Generates 1000+ realistic TV programs across all channels for 7 days.
 */
object MockPrograms {
    
    // Anchor time for generation
    private val now = Clock.System.now()
    private val pragueTz = TimeZone.of("Europe/Prague")
    
    /**
     * All generated mock programs (1000+ total).
     * 
     * Programs are generated for 7 days starting from yesterday at midnight.
     */
    val all: List<Program> by lazy {
        val oneDayMillis = 24L * 60 * 60 * 1000
        val startDate = Instant.fromEpochMilliseconds(now.toEpochMilliseconds() - oneDayMillis)
            .toLocalDateTime(pragueTz)
            .let { LocalDateTime(it.year, it.month, it.day, 0, 0) }
            .toInstant(pragueTz)
        
        MockChannels.all.flatMap { channel ->
            generateProgramsForChannel(channel, startDate, daysCount = 7)
        }
    }
    
    /**
     * Generate programs for a specific channel over multiple days.
     */
    private fun generateProgramsForChannel(
        channel: Channel,
        startTime: Instant,
        daysCount: Int
    ): List<Program> {
        val programs = mutableListOf<Program>()
        var currentTime = startTime
    val endTime = Instant.fromEpochMilliseconds(startTime.toEpochMilliseconds() + daysCount * 24L * 60 * 60 * 1000)
        
        while (currentTime < endTime) {
            val localTime = currentTime.toLocalDateTime(pragueTz)
            val hourOfDay = localTime.hour
            
            // Select program template based on time of day and channel category
            val template = selectProgramTemplate(channel.category, hourOfDay)
            
            val program = mockProgram {
                id = "prog_${channel.id}_${currentTime.epochSeconds}"
                channelId = channel.id
                title = template.title
                subtitle = template.subtitle
                description = template.description
                shortDescription = template.shortDescription
                this.startTime = currentTime
                this.endTime = Instant.fromEpochMilliseconds(currentTime.toEpochMilliseconds() + template.durationMinutes * 60L * 1000)
                type = template.type
                rating = template.rating
                isLive = template.isLive
                isRepeat = template.isRepeat && Random.nextBoolean()
                isPremiere = template.isPremiere && Random.nextFloat() < 0.1f
                season = template.season
                episode = template.episode
                year = template.year
                country = template.country
                genres = template.genres
                directors = template.directors
                cast = template.cast
            }
            
            programs.add(program)
            currentTime = program.endTime
        }
        
        return programs
    }
    
    /**
     * Select appropriate program template based on channel category and time.
     */
    private fun selectProgramTemplate(category: ChannelCategory, hourOfDay: Int): ProgramTemplate {
        val templates = when (category) {
            ChannelCategory.NATIONAL -> nationalProgramTemplates
            ChannelCategory.PRIVATE -> privateProgramTemplates
            ChannelCategory.REGIONAL -> regionalProgramTemplates
            ChannelCategory.THEMATIC_MOVIES -> movieProgramTemplates
            ChannelCategory.THEMATIC_SPORT -> sportProgramTemplates
            ChannelCategory.THEMATIC_NEWS -> newsProgramTemplates
            ChannelCategory.THEMATIC_KIDS -> kidsProgramTemplates
            ChannelCategory.THEMATIC_MUSIC -> musicProgramTemplates
            ChannelCategory.THEMATIC_DOCUMENTARY -> documentaryProgramTemplates
            ChannelCategory.THEMATIC_OTHER -> generalProgramTemplates
            ChannelCategory.INTERNATIONAL -> internationalProgramTemplates
        }
        
        // Filter by time of day
        val filteredTemplates = templates.filter { template ->
            when {
                hourOfDay in 6..11 -> template.timeSlot == TimeSlot.MORNING
                hourOfDay in 12..17 -> template.timeSlot == TimeSlot.AFTERNOON
                hourOfDay in 18..21 -> template.timeSlot == TimeSlot.PRIME_TIME
                else -> template.timeSlot == TimeSlot.LATE_NIGHT
            }
        }
        
        // If no templates match the time slot, use all templates as fallback
        return if (filteredTemplates.isNotEmpty()) {
            filteredTemplates.random()
        } else {
            templates.random()
        }
    }
    
    /**
     * Time slots for program scheduling.
     */
    private enum class TimeSlot {
        MORNING,      // 6:00-12:00
        AFTERNOON,    // 12:00-18:00
        PRIME_TIME,   // 18:00-22:00
        LATE_NIGHT    // 22:00-6:00
    }
    
    /**
     * Template for generating program data.
     */
    private data class ProgramTemplate(
        val title: String,
        val subtitle: String? = null,
        val description: String,
        val shortDescription: String,
        val durationMinutes: Int,
        val type: ProgramType,
        val rating: AgeRating,
        val timeSlot: TimeSlot,
        val isLive: Boolean = false,
        val isRepeat: Boolean = false,
        val isPremiere: Boolean = false,
        val season: Int? = null,
        val episode: Int? = null,
        val year: Int? = null,
        val country: String? = "CZ",
        val genres: List<String> = emptyList(),
        val directors: List<String> = emptyList(),
        val cast: List<String> = emptyList()
    )
    
    // Program templates for each category
    private val nationalProgramTemplates = listOf(
        // Morning
        ProgramTemplate(
            title = "Dobré ráno",
            description = "Ranní zpravodajsko-publicistický pořad",
            shortDescription = "Ranní zpravodajství",
            durationMinutes = 180,
            type = ProgramType.NEWS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING,
            isLive = true
        ),
        ProgramTemplate(
            title = "Pohádka",
            description = "Klasická česká pohádka pro děti",
            shortDescription = "Česká pohádka",
            durationMinutes = 60,
            type = ProgramType.KIDS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING
        ),
        // Afternoon
        ProgramTemplate(
            title = "Ordinace v růžové zahradě",
            subtitle = "Nová naděje",
            description = "Populární český seriál z prostředí nemocnice",
            shortDescription = "Český seriál",
            durationMinutes = 50,
            type = ProgramType.SERIES,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.AFTERNOON,
            season = Random.nextInt(1, 15),
            episode = Random.nextInt(1, 300),
            genres = listOf("Drama", "Romance")
        ),
        ProgramTemplate(
            title = "Zprávy",
            description = "Hlavní zpravodajská relace",
            shortDescription = "Zpravodajství",
            durationMinutes = 30,
            type = ProgramType.NEWS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.AFTERNOON,
            isLive = true
        ),
        // Prime time
        ProgramTemplate(
            title = "Pelíšky",
            description = "Kultovní český film režiséra Jana Hřebejka",
            shortDescription = "Český film",
            durationMinutes = 120,
            type = ProgramType.MOVIE,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.PRIME_TIME,
            year = 1999,
            directors = listOf("Jan Hřebejk"),
            genres = listOf("Komedie", "Drama"),
            cast = listOf("Miroslav Donutil", "Jiří Kodet", "Stella Zázvorková")
        ),
        ProgramTemplate(
            title = "Návštěvníci",
            subtitle = "Tajemné stopy",
            description = "Populární český seriál o cestování časem",
            shortDescription = "Český sci-fi seriál",
            durationMinutes = 60,
            type = ProgramType.SERIES,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.PRIME_TIME,
            season = Random.nextInt(1, 3),
            episode = Random.nextInt(1, 20),
            genres = listOf("Sci-Fi", "Drama")
        ),
        // Late night
        ProgramTemplate(
            title = "Noční proud",
            description = "Diskusní pořad s hosty",
            shortDescription = "Diskusní pořad",
            durationMinutes = 90,
            type = ProgramType.TALK_SHOW,
            rating = AgeRating.PG_15,
            timeSlot = TimeSlot.LATE_NIGHT,
            isLive = true
        )
    )
    
    private val privateProgramTemplates = listOf(
        ProgramTemplate(
            title = "Snídaně s Novou",
            description = "Ranní zpravodajsko-zábavní pořad",
            shortDescription = "Ranní show",
            durationMinutes = 180,
            type = ProgramType.ENTERTAINMENT,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING,
            isLive = true
        ),
        ProgramTemplate(
            title = "Ordinace v růžové zahradě 2",
            description = "Pokračování oblíbeného seriálu",
            shortDescription = "Seriál",
            durationMinutes = 55,
            type = ProgramType.SERIES,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.AFTERNOON,
            season = Random.nextInt(1, 10),
            episode = Random.nextInt(1, 200),
            genres = listOf("Drama")
        ),
        ProgramTemplate(
            title = "MasterChef Česko",
            description = "Soutěž amatérských kuchařů",
            shortDescription = "Reality soutěž",
            durationMinutes = 120,
            type = ProgramType.REALITY,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.PRIME_TIME,
            season = Random.nextInt(1, 10),
            episode = Random.nextInt(1, 15)
        ),
        ProgramTemplate(
            title = "Výměna manželek",
            description = "Reality show o výměně manželek",
            shortDescription = "Reality show",
            durationMinutes = 90,
            type = ProgramType.REALITY,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.PRIME_TIME
        )
    )
    
    private val regionalProgramTemplates = listOf(
        ProgramTemplate(
            title = "Ranní region",
            description = "Ranní zpravodajství z regionu",
            shortDescription = "Ranní zprávy",
            durationMinutes = 20,
            type = ProgramType.NEWS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING,
            isLive = true
        ),
        ProgramTemplate(
            title = "Regionální zprávy",
            description = "Zprávy z regionu",
            shortDescription = "Regionální zpravodajství",
            durationMinutes = 15,
            type = ProgramType.NEWS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.AFTERNOON,
            isLive = true
        ),
        ProgramTemplate(
            title = "Náš region",
            description = "Magazín o životě v regionu",
            shortDescription = "Regionální magazín",
            durationMinutes = 30,
            type = ProgramType.DOCUMENTARY,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.PRIME_TIME
        ),
        ProgramTemplate(
            title = "Repríza",
            description = "Repríza denního vysílání",
            shortDescription = "Repríza",
            durationMinutes = 120,
            type = ProgramType.OTHER,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.LATE_NIGHT,
            isRepeat = true
        )
    )
    
    private val movieProgramTemplates = listOf(
        ProgramTemplate(
            title = "Temnota",
            description = "Napínavý horor o prokletém domě",
            shortDescription = "Horor",
            durationMinutes = 105,
            type = ProgramType.MOVIE,
            rating = AgeRating.PG_15,
            timeSlot = TimeSlot.MORNING,
            year = 2020,
            genres = listOf("Horor", "Thriller"),
            country = "USA"
        ),
        ProgramTemplate(
            title = "Rychle a zběsile",
            description = "Akční film o závodech a adrenalinu",
            shortDescription = "Akční film",
            durationMinutes = 130,
            type = ProgramType.MOVIE,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.AFTERNOON,
            year = 2021,
            genres = listOf("Akční", "Thriller"),
            country = "USA"
        ),
        ProgramTemplate(
            title = "Láska na první pohled",
            description = "Romantická komedie o náhodném setkání",
            shortDescription = "Romantická komedie",
            durationMinutes = 95,
            type = ProgramType.MOVIE,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.PRIME_TIME,
            year = 2019,
            genres = listOf("Komedie", "Romance"),
            country = "USA"
        ),
        ProgramTemplate(
            title = "Temný rytíř",
            description = "Batman bojuje proti Jokerovi v Gotham City",
            shortDescription = "Superhero film",
            durationMinutes = 152,
            type = ProgramType.MOVIE,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.LATE_NIGHT,
            year = 2008,
            genres = listOf("Akční", "Drama"),
            directors = listOf("Christopher Nolan"),
            cast = listOf("Christian Bale", "Heath Ledger", "Aaron Eckhart")
        )
    )
    
    private val sportProgramTemplates = listOf(
        ProgramTemplate(
            title = "Sportovní zprávy",
            description = "Přehled sportovních událostí",
            shortDescription = "Sport zprávy",
            durationMinutes = 15,
            type = ProgramType.SPORTS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING,
            isLive = true
        ),
        ProgramTemplate(
            title = "Sportovní magazín",
            description = "Zprávy ze světa sportu",
            shortDescription = "Sport",
            durationMinutes = 60,
            type = ProgramType.SPORTS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.AFTERNOON
        ),
        ProgramTemplate(
            title = "Fotbal: Liga mistrů",
            description = "Přímý přenos zápasu Ligy mistrů",
            shortDescription = "Fotbal ŽIVĚ",
            durationMinutes = 120,
            type = ProgramType.SPORTS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.PRIME_TIME,
            isLive = true
        ),
        ProgramTemplate(
            title = "Hokej: NHL",
            description = "Zápas NHL",
            shortDescription = "Hokej",
            durationMinutes = 150,
            type = ProgramType.SPORTS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.LATE_NIGHT,
            isLive = false
        )
    )
    
    private val newsProgramTemplates = listOf(
        ProgramTemplate(
            title = "Zprávy",
            description = "Aktuální zpravodajství",
            shortDescription = "Zprávy",
            durationMinutes = 30,
            type = ProgramType.NEWS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING,
            isLive = true
        ),
        ProgramTemplate(
            title = "Polední zprávy",
            description = "Zpravodajství o poledni",
            shortDescription = "Zprávy",
            durationMinutes = 30,
            type = ProgramType.NEWS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.AFTERNOON,
            isLive = true
        ),
        ProgramTemplate(
            title = "Reportéři",
            description = "Investigativní reportáže",
            shortDescription = "Reportáže",
            durationMinutes = 60,
            type = ProgramType.NEWS,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.PRIME_TIME
        ),
        ProgramTemplate(
            title = "Noční zprávy",
            description = "Shrnutí dne",
            shortDescription = "Zprávy",
            durationMinutes = 20,
            type = ProgramType.NEWS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.LATE_NIGHT,
            isLive = true
        )
    )
    
    private val kidsProgramTemplates = listOf(
        ProgramTemplate(
            title = "Prasátko Peppa",
            description = "Animovaný seriál pro nejmenší",
            shortDescription = "Animovaný seriál",
            durationMinutes = 10,
            type = ProgramType.KIDS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING,
            season = Random.nextInt(1, 8),
            episode = Random.nextInt(1, 50)
        ),
        ProgramTemplate(
            title = "SpongeBob v kalhotách",
            description = "Dobrodružství mořské houby",
            shortDescription = "SpongeBob",
            durationMinutes = 20,
            type = ProgramType.KIDS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.AFTERNOON,
            season = Random.nextInt(1, 13),
            episode = Random.nextInt(1, 250)
        ),
        ProgramTemplate(
            title = "Ledové království",
            description = "Animovaný film od Disneyho",
            shortDescription = "Animovaný film",
            durationMinutes = 102,
            type = ProgramType.MOVIE,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.PRIME_TIME,
            year = 2013,
            genres = listOf("Animovaný", "Fantasy")
        ),
        ProgramTemplate(
            title = "Dobrou noc, děti",
            description = "Večerní pohádka na dobrou noc",
            shortDescription = "Pohádka",
            durationMinutes = 15,
            type = ProgramType.KIDS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.LATE_NIGHT
        )
    )
    
    private val musicProgramTemplates = listOf(
        ProgramTemplate(
            title = "Ranní hudba",
            description = "Ranní hudební mix",
            shortDescription = "Hudební mix",
            durationMinutes = 60,
            type = ProgramType.MUSIC,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING
        ),
        ProgramTemplate(
            title = "Top 40",
            description = "Žebříček 40 nejlepších hitů",
            shortDescription = "Hudební žebříček",
            durationMinutes = 60,
            type = ProgramType.MUSIC,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.AFTERNOON
        ),
        ProgramTemplate(
            title = "Live koncert",
            description = "Živý koncert populární kapely",
            shortDescription = "Živý koncert",
            durationMinutes = 90,
            type = ProgramType.MUSIC,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.PRIME_TIME,
            isLive = true
        ),
        ProgramTemplate(
            title = "Hudební klipy",
            description = "Nonstop hudební klipy",
            shortDescription = "Hudební klipy",
            durationMinutes = 120,
            type = ProgramType.MUSIC,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.LATE_NIGHT
        )
    )
    
    private val documentaryProgramTemplates = listOf(
        ProgramTemplate(
            title = "Ranní dokument",
            description = "Inspirativní dokument do rána",
            shortDescription = "Dokument",
            durationMinutes = 30,
            type = ProgramType.DOCUMENTARY,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING
        ),
        ProgramTemplate(
            title = "Planeta Země",
            description = "Dokument o přírodě naší planety",
            shortDescription = "Příroda",
            durationMinutes = 50,
            type = ProgramType.DOCUMENTARY,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.AFTERNOON,
            season = Random.nextInt(1, 3),
            episode = Random.nextInt(1, 12)
        ),
        ProgramTemplate(
            title = "Tajemství druhé světové války",
            description = "Historický dokument o válce",
            shortDescription = "Historie",
            durationMinutes = 60,
            type = ProgramType.DOCUMENTARY,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.PRIME_TIME,
            year = 2018
        ),
        ProgramTemplate(
            title = "Noční svět zvířat",
            description = "Noční život divokých zvířat",
            shortDescription = "Příroda",
            durationMinutes = 45,
            type = ProgramType.DOCUMENTARY,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.LATE_NIGHT
        )
    )
    
    private val generalProgramTemplates = listOf(
        ProgramTemplate(
            title = "Ranní show",
            description = "Zábavný ranní pořad",
            shortDescription = "Show",
            durationMinutes = 60,
            type = ProgramType.ENTERTAINMENT,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING
        ),
        ProgramTemplate(
            title = "Magazín",
            description = "Zábavně-vzdělávací magazín",
            shortDescription = "Magazín",
            durationMinutes = 30,
            type = ProgramType.ENTERTAINMENT,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.AFTERNOON
        ),
        ProgramTemplate(
            title = "Večerní show",
            description = "Večerní zábavný pořad",
            shortDescription = "Show",
            durationMinutes = 90,
            type = ProgramType.ENTERTAINMENT,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.PRIME_TIME
        ),
        ProgramTemplate(
            title = "Noční film",
            description = "Film do nočních hodin",
            shortDescription = "Film",
            durationMinutes = 120,
            type = ProgramType.MOVIE,
            rating = AgeRating.PG_15,
            timeSlot = TimeSlot.LATE_NIGHT
        )
    )
    
    private val internationalProgramTemplates = listOf(
        ProgramTemplate(
            title = "World News",
            description = "International news coverage",
            shortDescription = "News",
            durationMinutes = 30,
            type = ProgramType.NEWS,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.MORNING,
            isLive = true
        ),
        ProgramTemplate(
            title = "International Talk Show",
            description = "Afternoon talk show",
            shortDescription = "Talk show",
            durationMinutes = 60,
            type = ProgramType.ENTERTAINMENT,
            rating = AgeRating.ALL,
            timeSlot = TimeSlot.AFTERNOON
        ),
        ProgramTemplate(
            title = "International Movie",
            description = "Foreign language film",
            shortDescription = "Movie",
            durationMinutes = 110,
            type = ProgramType.MOVIE,
            rating = AgeRating.PG_12,
            timeSlot = TimeSlot.PRIME_TIME,
            year = 2020
        ),
        ProgramTemplate(
            title = "Late Night International",
            description = "Late night international programming",
            shortDescription = "Late night",
            durationMinutes = 90,
            type = ProgramType.ENTERTAINMENT,
            rating = AgeRating.PG_15,
            timeSlot = TimeSlot.LATE_NIGHT
        )
    )
}
