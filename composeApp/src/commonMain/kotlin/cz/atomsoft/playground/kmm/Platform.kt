package cz.atomsoft.playground.kmm

// Deprecated skeleton interface kept only to avoid compile errors if referenced.
// No expect/actual pattern anymore.
interface Platform { val name: String }

@Deprecated("Skeleton placeholder")
fun getPlatform(): Platform = object : Platform { override val name: String = "deprecated" }