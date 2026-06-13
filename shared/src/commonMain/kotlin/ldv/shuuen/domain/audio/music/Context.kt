package ldv.shuuen.domain.audio.music

import kotlinx.serialization.Serializable
import kotlin.time.Duration


@Serializable
data class DegreeContext(
  val id: String,
  val nodes: List<DegreeContextNode>,
  val name: String? = null,
)

@Serializable
data class DegreeContextNode(
  val degrees: List<DegreeWithOctave>,
  val durationInQuestions: Int?,
  val sustain: Sustain,
  val setupMelody: RelativeMelody?
)

@Serializable
sealed interface Sustain {
  @Serializable
  data object Endless : Sustain

  @Serializable
  data class Finite(val duration: Duration) : Sustain
}

val defaultContext = DegreeContext(
  id = "default", listOf(
    DegreeContextNode(
      listOf(DegreeWithOctave(Degree.D1, 2)), null, Sustain.Endless, setupMelody = RelativeMelody(
        firstDegree = DegreeWithOctave(Degree.D1, 2), extraDegrees = listOf(
          DirectedDegree(Degree.D3, DegreeDirection.Up),
          DirectedDegree(Degree.D5, DegreeDirection.Up),
          DirectedDegree(
            Degree.D1, DegreeDirection.Up
          )
        )
      )
    )
  )
)