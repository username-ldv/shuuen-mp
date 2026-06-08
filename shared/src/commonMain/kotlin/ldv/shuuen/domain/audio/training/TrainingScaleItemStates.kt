package ldv.shuuen.domain.audio.training

import kotlinx.serialization.Serializable
import ldv.shuuen.domain.audio.music.Degree
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.ui.common.BoxedListItemState

@Serializable
sealed interface TrainingScaleItemStates {
  val items: Map<*, BoxedListItemState>

  @Serializable
  data class ByPitch(
    override val items: Map<Pitch, BoxedListItemState>
  ) : TrainingScaleItemStates

  @Serializable
  data class ByDegree(
    override val items: Map<Degree, BoxedListItemState>
  ) : TrainingScaleItemStates
}