package ldv.shuuen.domain.audio.music

import kotlinx.serialization.Serializable
import kotlin.time.Duration


@Serializable
data class DegreeContext(
  val nodes: List<DegreeContextNode>, val name: String? = null, val setupMelody: List<Degree>?
)

@Serializable
data class DegreeContextNode(
  val degrees: List<DegreeWithOctave>, val durationInQuestions: Int?, val sustain: Sustain
)

@Serializable
sealed interface Sustain {
  @Serializable
  data object Endless : Sustain

  @Serializable
  data class Finite(val duration: Duration) : Sustain
}

val defaultContext = DegreeContext(
  listOf(DegreeContextNode(listOf(DegreeWithOctave(Degree.D1, 2)), null, Sustain.Endless)),
  setupMelody = listOf(
    Degree.D1,
    Degree.D3,
    Degree.D5,
    Degree.D1,
  )
)