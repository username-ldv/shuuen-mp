package ldv.shuuen.domain.audio.training.singles

import kotlinx.serialization.Serializable
import ldv.shuuen.domain.audio.music.DegreeContext
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.training.TrainingScale

@Serializable
data class SinglesLevel(
  val name: String,
  val traningScale: TrainingScale,
  val context: DegreeContext,
  val questionsNumber: Int?,
  val range: Pair<Note, Note>,
)