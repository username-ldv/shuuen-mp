package ldv.shuuen.domain.training

import kotlinx.serialization.Serializable
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.Scale
import ldv.shuuen.domain.audio.music.ScaleType
import ldv.shuuen.ui.common.BoxedListItemState

enum class TrainingScaleType {
  Relative, Absolute
}

@Serializable
data class TrainingScale(val root: Pitch?, val itemStates: TrainingScaleItemStates) {
  val type = if (root != null) TrainingScaleType.Absolute else TrainingScaleType.Relative

  companion object {
    fun fromScale(s: Scale): TrainingScale {
      val names = s.appropriatePitchNames()
      return TrainingScale(
        root = s.root,
        itemStates = TrainingScaleItemStates.ByPitch(s.pitches.zip(names).associate { (pitch, name) ->
          pitch to BoxedListItemState(true, name)
        })
      )
    }

    fun degreesFromType(m: ScaleType, formula: List<Int>? = null): TrainingScale {
      val sampleScale = Scale.fromScaleType(Pitch.C, m, formula ?: listOf(0))
      return TrainingScale(
        root = null,
        itemStates = TrainingScaleItemStates.ByDegree(
          sampleScale.degrees.associateWith { BoxedListItemState(true, it.label) })
      )
    }
  }
}