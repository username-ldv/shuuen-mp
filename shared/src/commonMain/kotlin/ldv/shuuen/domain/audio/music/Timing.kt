package ldv.shuuen.domain.audio.music

data class Timing(val tempo: Int) {
  fun whole(): Double = quarter() * 4.0

  fun half(): Double = quarter() * 2.0

  fun quarter(): Double {
    require(tempo > 0) { "Tempo must be positive." }
    return 60_000.0 / tempo
  }

  fun eighth(): Double = quarter() / 2.0

  fun sixteenth(): Double = quarter() / 4.0

  fun thirtySecond(): Double = quarter() / 8.0

  fun sixtyFourth(): Double = quarter() / 16.0

  fun oneHundredTwentyEighth(): Double = quarter() / 32.0
}

inline fun <T> withTiming(tempo: Int, block: Timing.() -> T): T = Timing(tempo).block()
