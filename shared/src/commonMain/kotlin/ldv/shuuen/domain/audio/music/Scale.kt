package ldv.shuuen.domain.audio.music

enum class ScaleType(val scaleName: String) {
  Major("Major"),
  NaturalMinor("Natural Minor"),
  Custom("Custom"),
}

enum class ScaleAccidentalType {
  Sharps,
  Flats,
}

private val sharpMajorKeys = List(7) { index -> Pitch.C + 7 * (index + 1) }
private val flatMajorKeys = List(7) { index -> Pitch.C - 7 * (index + 1) }
private val sharpMinorKeys = sharpMajorKeys.map { it - 3 }
private val flatMinorKeys = flatMajorKeys.map { it - 3 }

data class Scale(
  val pitches: List<Pitch>,
  val formula: List<Int>,
  val type: ScaleType,
) {
  val root: Pitch = pitches.first()

  fun notes(startingOctave: Int): List<Note> {
    val rootNote = Note(root, startingOctave)
    return formula.runningReduce { acc, step -> acc + step }.map { rootNote + it }
  }

  fun noteNames(accidentalType: ScaleAccidentalType): List<String> {
    val useSharps = when (type) {
      ScaleType.Custom -> accidentalType == ScaleAccidentalType.Sharps
      ScaleType.Major -> accidentalType == ScaleAccidentalType.Sharps || root in sharpMajorKeys && root !in flatMajorKeys
      ScaleType.NaturalMinor -> accidentalType == ScaleAccidentalType.Sharps || root in sharpMinorKeys && root !in flatMinorKeys
    }

    return notes(4).map { note ->
      if (useSharps) note.pitch.toString() else note.pitch.toFlatString()
    }
  }

  companion object {
    fun major(root: Pitch): Scale =
      fromFormula(root, listOf(0, 2, 2, 1, 2, 2, 2, 1), ScaleType.Major)

    fun naturalMinor(root: Pitch): Scale =
      fromFormula(root, listOf(0, 2, 1, 2, 2, 1, 2, 2), ScaleType.NaturalMinor)

    fun custom(root: Pitch, formula: List<Int>): Scale =
      fromFormula(root, formula, ScaleType.Custom)

    private fun fromFormula(root: Pitch, formula: List<Int>, type: ScaleType): Scale {
      require(formula.isNotEmpty()) { "Scale formula must not be empty." }
      val pitches = formula.runningReduce { acc, step -> acc + step }.map { root + it }
      return Scale(pitches = pitches, formula = formula, type = type)
    }
  }
}
