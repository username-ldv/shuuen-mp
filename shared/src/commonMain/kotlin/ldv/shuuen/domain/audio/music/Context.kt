package ldv.shuuen.domain.audio.music


data class DegreeContext(val nodes: List<DegreeContextNode>)

data class DegreeContextNode(
  val degrees: List<Degree>,
  val durationInQuestions: Int,
  val sustain: Sustain
)

sealed interface Sustain {
  data object Endless : Sustain

  data class Finite(val duration: Double) : Sustain
}
