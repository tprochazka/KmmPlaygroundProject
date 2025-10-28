package cz.atomsoft.playground.kmm

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform