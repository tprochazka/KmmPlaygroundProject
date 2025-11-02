package cz.myapp.tvguide.data.mock

import cz.myapp.tvguide.domain.model.Channel
import cz.myapp.tvguide.domain.model.ChannelCategory

/**
 * Mock channel data for Phase 1 prototype.
 * 
 * Contains 100+ realistic Czech and Slovak TV channels.
 */
object MockChannels {
    
    /**
     * All mock channels (100+ total).
     */
    val all: List<Channel> by lazy {
        buildList {
            addAll(nationalChannels)
            addAll(privateChannels)
            addAll(regionalChannels)
            addAll(moviesChannels)
            addAll(sportsChannels)
            addAll(newsChannels)
            addAll(kidsChannels)
            addAll(musicChannels)
            addAll(documentaryChannels)
            addAll(internationalChannels)
        }
    }
    
    /**
     * National public channels.
     */
    val nationalChannels = listOf(
        mockChannel {
            id = "ct1"
            name = "ČT1"
            number = 1
            category = ChannelCategory.NATIONAL
            description = "Česká televize 1 - zpravodajství, filmy, zábava"
            isHd = true
        },
        mockChannel {
            id = "ct2"
            name = "ČT2"
            number = 2
            category = ChannelCategory.NATIONAL
            description = "Česká televize 2 - kultura, vzdělání, sport"
            isHd = true
        },
        mockChannel {
            id = "ct24"
            name = "ČT24"
            number = 3
            category = ChannelCategory.NATIONAL
            description = "Zpravodajství ČT non-stop"
            isHd = true
        },
        mockChannel {
            id = "ct_sport"
            name = "ČT sport"
            number = 4
            category = ChannelCategory.NATIONAL
            description = "Sportovní kanál České televize"
            isHd = true
        },
        mockChannel {
            id = "ct_art"
            name = "ČT :D / ČT art"
            number = 5
            category = ChannelCategory.NATIONAL
            description = "Dětské pořady a umění"
            isHd = false
        },
        mockChannel {
            id = "stv1"
            name = "STV1"
            number = 6
            category = ChannelCategory.NATIONAL
            description = "Slovenská televize 1"
            languageCode = "sk"
            isHd = true
        },
        mockChannel {
            id = "stv2"
            name = "STV2"
            number = 7
            category = ChannelCategory.NATIONAL
            description = "Slovenská televize 2"
            languageCode = "sk"
            isHd = true
        }
    )
    
    /**
     * Private channels.
     */
    val privateChannels = listOf(
        mockChannel {
            id = "nova"
            name = "Nova"
            number = 10
            category = ChannelCategory.PRIVATE
            description = "TV Nova - zábava, seriály, reality show"
            isHd = true
        },
        mockChannel {
            id = "nova_cinema"
            name = "Nova Cinema"
            number = 11
            category = ChannelCategory.PRIVATE
            description = "Filmový kanál TV Nova"
            isHd = true
        },
        mockChannel {
            id = "nova_sport1"
            name = "Nova Sport 1"
            number = 12
            category = ChannelCategory.PRIVATE
            description = "Sportovní kanál TV Nova"
            isHd = true
        },
        mockChannel {
            id = "nova_sport2"
            name = "Nova Sport 2"
            number = 13
            category = ChannelCategory.PRIVATE
            description = "Druhý sportovní kanál TV Nova"
            isHd = true
        },
        mockChannel {
            id = "nova_action"
            name = "Nova Action"
            number = 14
            category = ChannelCategory.PRIVATE
            description = "Akční filmy a seriály"
            isHd = true
        },
        mockChannel {
            id = "nova_gold"
            name = "Nova Gold"
            number = 15
            category = ChannelCategory.PRIVATE
            description = "Klasické pořady TV Nova"
            isHd = false
        },
        mockChannel {
            id = "prima"
            name = "Prima"
            number = 20
            category = ChannelCategory.PRIVATE
            description = "TV Prima - zpravodajství, seriály, zábava"
            isHd = true
        },
        mockChannel {
            id = "prima_cool"
            name = "Prima COOL"
            number = 21
            category = ChannelCategory.PRIVATE
            description = "Seriály a zábava pro mladé"
            isHd = true
        },
        mockChannel {
            id = "prima_love"
            name = "Prima LOVE"
            number = 22
            category = ChannelCategory.PRIVATE
            description = "Životní styl, seriály, reality show"
            isHd = true
        },
        mockChannel {
            id = "prima_max"
            name = "Prima MAX"
            number = 23
            category = ChannelCategory.PRIVATE
            description = "Filmy, seriály, dokumenty"
            isHd = true
        },
        mockChannel {
            id = "prima_zoom"
            name = "Prima ZOOM"
            number = 24
            category = ChannelCategory.PRIVATE
            description = "Pořady o přírodě a cestování"
            isHd = true
        },
        mockChannel {
            id = "prima_krimi"
            name = "Prima KRIMI"
            number = 25
            category = ChannelCategory.PRIVATE
            description = "Krimi seriály a filmy"
            isHd = true
        },
        mockChannel {
            id = "prima_star"
            name = "Prima STAR"
            number = 26
            category = ChannelCategory.PRIVATE
            description = "Klasické filmy a seriály"
            isHd = false
        },
        mockChannel {
            id = "markiza"
            name = "TV Markíza"
            number = 30
            category = ChannelCategory.PRIVATE
            description = "TV Markíza - slovenský soukromý kanál"
            languageCode = "sk"
            isHd = true
        },
        mockChannel {
            id = "joj"
            name = "JOJ"
            number = 31
            category = ChannelCategory.PRIVATE
            description = "TV JOJ - slovenský soukromý kanál"
            languageCode = "sk"
            isHd = true
        }
    )
    
    /**
     * Regional channels.
     */
    val regionalChannels = listOf(
        mockChannel {
            id = "prague_tv"
            name = "Praha TV"
            number = 40
            category = ChannelCategory.REGIONAL
            description = "Regionální televize pro Prahu"
            isHd = false
        },
        mockChannel {
            id = "polar"
            name = "Polar"
            number = 41
            category = ChannelCategory.REGIONAL
            description = "Regionální televize Polar"
            isHd = false
        },
        mockChannel {
            id = "regionalni_cz"
            name = "Regionální televize"
            number = 42
            category = ChannelCategory.REGIONAL
            description = "Regionální zpravodajství"
            isHd = false
        }
    )
    
    /**
     * Movie channels.
     */
    val moviesChannels = listOf(
        mockChannel {
            id = "filmbox"
            name = "FilmBox"
            number = 50
            category = ChannelCategory.THEMATIC_MOVIES
            description = "Filmový kanál FilmBox"
            isHd = true
        },
        mockChannel {
            id = "filmbox_premium"
            name = "FilmBox Premium"
            number = 51
            category = ChannelCategory.THEMATIC_MOVIES
            description = "Prémiové filmy FilmBox"
            isHd = true
        },
        mockChannel {
            id = "filmbox_plus"
            name = "FilmBox Plus"
            number = 52
            category = ChannelCategory.THEMATIC_MOVIES
            description = "FilmBox Plus"
            isHd = true
        },
        mockChannel {
            id = "filmbox_family"
            name = "FilmBox Family"
            number = 53
            category = ChannelCategory.THEMATIC_MOVIES
            description = "Rodinné filmy"
            isHd = true
        },
        mockChannel {
            id = "kino_barrandov"
            name = "Kino Barrandov"
            number = 54
            category = ChannelCategory.THEMATIC_MOVIES
            description = "České a zahraniční filmy"
            isHd = true
        },
        mockChannel {
            id = "cinemax"
            name = "Cinemax"
            number = 55
            category = ChannelCategory.THEMATIC_MOVIES
            description = "Hollywoodské filmy Cinemax"
            isHd = true
        },
        mockChannel {
            id = "cinemax2"
            name = "Cinemax 2"
            number = 56
            category = ChannelCategory.THEMATIC_MOVIES
            description = "Další filmový kanál Cinemax"
            isHd = true
        },
        mockChannel {
            id = "hbo"
            name = "HBO"
            number = 57
            category = ChannelCategory.THEMATIC_MOVIES
            description = "HBO - prémiové filmy a seriály"
            isHd = true
        },
        mockChannel {
            id = "hbo2"
            name = "HBO 2"
            number = 58
            category = ChannelCategory.THEMATIC_MOVIES
            description = "HBO 2"
            isHd = true
        },
        mockChannel {
            id = "hbo3"
            name = "HBO 3"
            number = 59
            category = ChannelCategory.THEMATIC_MOVIES
            description = "HBO 3"
            isHd = true
        }
    )
    
    /**
     * Sports channels.
     */
    val sportsChannels = listOf(
        mockChannel {
            id = "eurosport1"
            name = "Eurosport 1"
            number = 60
            category = ChannelCategory.THEMATIC_SPORT
            description = "Mezinárodní sportovní kanál"
            isHd = true
        },
        mockChannel {
            id = "eurosport2"
            name = "Eurosport 2"
            number = 61
            category = ChannelCategory.THEMATIC_SPORT
            description = "Druhý Eurosport kanál"
            isHd = true
        },
        mockChannel {
            id = "canal_plus_sport"
            name = "Canal+ Sport"
            number = 62
            category = ChannelCategory.THEMATIC_SPORT
            description = "Canal+ Sport"
            isHd = true
        },
        mockChannel {
            id = "canal_plus_sport2"
            name = "Canal+ Sport 2"
            number = 63
            category = ChannelCategory.THEMATIC_SPORT
            description = "Canal+ Sport 2"
            isHd = true
        },
        mockChannel {
            id = "golf_channel"
            name = "Golf Channel"
            number = 64
            category = ChannelCategory.THEMATIC_SPORT
            description = "Golfový kanál"
            isHd = true
        },
        mockChannel {
            id = "fight_channel"
            name = "Fight Channel"
            number = 65
            category = ChannelCategory.THEMATIC_SPORT
            description = "Bojové sporty"
            isHd = true
        }
    )
    
    /**
     * News channels.
     */
    val newsChannels = listOf(
        mockChannel {
            id = "cnn_prima"
            name = "CNN Prima NEWS"
            number = 70
            category = ChannelCategory.THEMATIC_NEWS
            description = "Zpravodajský kanál Prima"
            isHd = true
        },
        mockChannel {
            id = "nova_international"
            name = "Nova International"
            number = 71
            category = ChannelCategory.THEMATIC_NEWS
            description = "Mezinárodní zpravodajství Nova"
            isHd = true
        }
    )
    
    /**
     * Kids channels.
     */
    val kidsChannels = listOf(
        mockChannel {
            id = "minimax"
            name = "Minimax"
            number = 80
            category = ChannelCategory.THEMATIC_KIDS
            description = "Dětský kanál Minimax"
            isHd = true
        },
        mockChannel {
            id = "disney_channel"
            name = "Disney Channel"
            number = 81
            category = ChannelCategory.THEMATIC_KIDS
            description = "Disney Channel pro děti"
            isHd = true
        },
        mockChannel {
            id = "nickelodeon"
            name = "Nickelodeon"
            number = 82
            category = ChannelCategory.THEMATIC_KIDS
            description = "Nickelodeon - dětská zábava"
            isHd = true
        },
        mockChannel {
            id = "cartoon_network"
            name = "Cartoon Network"
            number = 83
            category = ChannelCategory.THEMATIC_KIDS
            description = "Cartoon Network"
            isHd = true
        },
        mockChannel {
            id = "duck_tv"
            name = "Duck TV"
            number = 84
            category = ChannelCategory.THEMATIC_KIDS
            description = "Duck TV pro nejmenší"
            isHd = false
        },
        mockChannel {
            id = "baby_tv"
            name = "Baby TV"
            number = 85
            category = ChannelCategory.THEMATIC_KIDS
            description = "Baby TV pro batolata"
            isHd = false
        }
    )
    
    /**
     * Music channels.
     */
    val musicChannels = listOf(
        mockChannel {
            id = "mtv"
            name = "MTV"
            number = 90
            category = ChannelCategory.THEMATIC_MUSIC
            description = "MTV - hudební kanál"
            isHd = true
        },
        mockChannel {
            id = "vh1"
            name = "VH1"
            number = 91
            category = ChannelCategory.THEMATIC_MUSIC
            description = "VH1 - klasická hudba"
            isHd = true
        },
        mockChannel {
            id = "ocko"
            name = "Óčko"
            number = 92
            category = ChannelCategory.THEMATIC_MUSIC
            description = "Český hudební kanál Óčko"
            isHd = true
        },
        mockChannel {
            id = "ocko_star"
            name = "Óčko Star"
            number = 93
            category = ChannelCategory.THEMATIC_MUSIC
            description = "Óčko Star - klasická česká hudba"
            isHd = true
        },
        mockChannel {
            id = "ocko_expres"
            name = "Óčko Expres"
            number = 94
            category = ChannelCategory.THEMATIC_MUSIC
            description = "Óčko Expres - moderní hity"
            isHd = true
        }
    )
    
    /**
     * Documentary channels.
     */
    val documentaryChannels = listOf(
        mockChannel {
            id = "natgeo"
            name = "National Geographic"
            number = 100
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "National Geographic - dokumenty o přírodě"
            isHd = true
        },
        mockChannel {
            id = "natgeo_wild"
            name = "Nat Geo Wild"
            number = 101
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "National Geographic Wild - zvířata"
            isHd = true
        },
        mockChannel {
            id = "discovery"
            name = "Discovery Channel"
            number = 102
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "Discovery Channel - objevování světa"
            isHd = true
        },
        mockChannel {
            id = "discovery_science"
            name = "Discovery Science"
            number = 103
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "Discovery Science - věda a technika"
            isHd = true
        },
        mockChannel {
            id = "animal_planet"
            name = "Animal Planet"
            number = 104
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "Animal Planet - svět zvířat"
            isHd = true
        },
        mockChannel {
            id = "history"
            name = "History Channel"
            number = 105
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "History - historie a kultura"
            isHd = true
        },
        mockChannel {
            id = "viasat_nature"
            name = "Viasat Nature"
            number = 106
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "Viasat Nature - příroda"
            isHd = true
        },
        mockChannel {
            id = "viasat_explore"
            name = "Viasat Explore"
            number = 107
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "Viasat Explore - dobrodružství"
            isHd = true
        },
        mockChannel {
            id = "viasat_history"
            name = "Viasat History"
            number = 108
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "Viasat History"
            isHd = true
        },
        mockChannel {
            id = "travel"
            name = "Travel Channel"
            number = 109
            category = ChannelCategory.THEMATIC_DOCUMENTARY
            description = "Travel Channel - cestování"
            isHd = true
        }
    )
    
    /**
     * International channels.
     */
    val internationalChannels = listOf(
        mockChannel {
            id = "bbc_world"
            name = "BBC World News"
            number = 110
            category = ChannelCategory.INTERNATIONAL
            description = "BBC World News"
            languageCode = "en"
            isHd = true
        },
        mockChannel {
            id = "cnn_international"
            name = "CNN International"
            number = 111
            category = ChannelCategory.INTERNATIONAL
            description = "CNN International"
            languageCode = "en"
            isHd = true
        },
        mockChannel {
            id = "euronews"
            name = "Euronews"
            number = 112
            category = ChannelCategory.INTERNATIONAL
            description = "Euronews - evropské zpravodajství"
            languageCode = "en"
            isHd = true
        },
        mockChannel {
            id = "rtl"
            name = "RTL"
            number = 113
            category = ChannelCategory.INTERNATIONAL
            description = "RTL - německá televize"
            languageCode = "de"
            isHd = true
        },
        mockChannel {
            id = "sat1"
            name = "SAT.1"
            number = 114
            category = ChannelCategory.INTERNATIONAL
            description = "SAT.1 - německá televize"
            languageCode = "de"
            isHd = true
        },
        mockChannel {
            id = "pro7"
            name = "Pro7"
            number = 115
            category = ChannelCategory.INTERNATIONAL
            description = "Pro7 - německá televize"
            languageCode = "de"
            isHd = true
        },
        mockChannel {
            id = "tvp1"
            name = "TVP1"
            number = 116
            category = ChannelCategory.INTERNATIONAL
            description = "TVP1 - polská televize"
            languageCode = "pl"
            isHd = true
        },
        mockChannel {
            id = "tvp2"
            name = "TVP2"
            number = 117
            category = ChannelCategory.INTERNATIONAL
            description = "TVP2 - polská televize"
            languageCode = "pl"
            isHd = true
        },
        mockChannel {
            id = "orf1"
            name = "ORF1"
            number = 118
            category = ChannelCategory.INTERNATIONAL
            description = "ORF1 - rakouská televize"
            languageCode = "de"
            isHd = true
        },
        mockChannel {
            id = "orf2"
            name = "ORF2"
            number = 119
            category = ChannelCategory.INTERNATIONAL
            description = "ORF2 - rakouská televize"
            languageCode = "de"
            isHd = true
        }
    )
}
