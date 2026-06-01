package ldv.shuuen

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform