package ldv.shuuen.domain.audio.music

import io.github.aakira.napier.Napier

enum class ScaleType(val scaleName: String) {
  Major("Major"), NaturalMinor("Natural Minor"), Custom("Custom");

  override fun toString(): String {
    return this.scaleName
  }

  companion object {
    fun fromName(s: String): ScaleType? {
      return ScaleType.entries.firstOrNull { it.scaleName == s }
    }
  }
}

enum class ScaleAccidentalType {
  Sharps, Flats,
}

private val sharpMajorKeys = List(6) { index -> Pitch.C + 7 * (index + 1) }
private val flatMajorKeys = List(6) { index -> Pitch.C - 7 * (index + 1) }
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

  fun appropriatePitchNames(accidentalResolutionType: ScaleAccidentalType = ScaleAccidentalType.Sharps): List<String> {
    return pitches.map { pitch ->
      appropriatePitchName(
        root,
        pitch,
        type,
        accidentalResolutionType
      )
    }
  }

  companion object {
    fun major(root: Pitch): Scale = fromFormula(root, listOf(0, 2, 2, 1, 2, 2, 2), ScaleType.Major)

    fun naturalMinor(root: Pitch): Scale =
      fromFormula(root, listOf(0, 2, 1, 2, 2, 1, 2), ScaleType.NaturalMinor)

    fun custom(root: Pitch, formula: List<Int>): Scale =
      fromFormula(root, formula, ScaleType.Custom)

    fun appropriatePitchName(
      root: Pitch,
      pitch: Pitch,
      type: ScaleType,
      accidentalResolutionType: ScaleAccidentalType = ScaleAccidentalType.Sharps,
    ): String {
      val useSharps = when (type) {
        ScaleType.Custom -> accidentalResolutionType == ScaleAccidentalType.Sharps
        ScaleType.Major -> if (accidentalResolutionType == ScaleAccidentalType.Sharps) (root in sharpMajorKeys) else (root !in flatMajorKeys)
        ScaleType.NaturalMinor -> if (accidentalResolutionType == ScaleAccidentalType.Sharps) (root in sharpMinorKeys) else (root !in flatMinorKeys)
      }
      val appropriateName =  if (useSharps) pitch.toString() else pitch.toFlatString()
      if(pitch == Pitch.ASharp) Napier.v { "Appropriate pitch name root: $root, name: $appropriateName, type: $type, useSharps: $useSharps" }
      return appropriateName
    }

    private fun fromFormula(
      root: Pitch, formula: List<Int>, type: ScaleType = ScaleType.Custom
    ): Scale {
      require(formula.isNotEmpty()) { "Scale formula must not be empty." }
      val pitches = formula.runningReduce { acc, step -> acc + step }.map { root + it }
      return Scale(pitches = pitches, formula = formula, type = type)
    }
  }
}
