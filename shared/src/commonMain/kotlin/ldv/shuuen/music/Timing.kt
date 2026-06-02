package ldv.shuuen.music

object Timing {
  fun whole(tempo: Int): Double = quarter(tempo) * 4.0

  fun half(tempo: Int): Double = quarter(tempo) * 2.0

  fun quarter(tempo: Int): Double {
    require(tempo > 0) { "Tempo must be positive." }
    return 60_000.0 / tempo
  }

  fun eighth(tempo: Int): Double = quarter(tempo) / 2.0

  fun sixteenth(tempo: Int): Double = quarter(tempo) / 4.0

  fun thirtySecond(tempo: Int): Double = quarter(tempo) / 8.0

  fun sixtyFourth(tempo: Int): Double = quarter(tempo) / 16.0

  fun oneHundredTwentyEighth(tempo: Int): Double = quarter(tempo) / 32.0
}
