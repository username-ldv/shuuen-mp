package ldv.shuuen.domain.audio.music

import kotlinx.serialization.Serializable


@Serializable
data class DegreeContext(val nodes: List<DegreeContextNode>, val name: String? = null)

@Serializable
data class DegreeContextNode(
  val degrees: List<Degree>,
  val durationInQuestions: Int?,
  val sustain: Sustain
)

@Serializable
sealed interface Sustain {
  @Serializable
  data object Endless : Sustain

  @Serializable
  data class Finite(val duration: Double) : Sustain
}
