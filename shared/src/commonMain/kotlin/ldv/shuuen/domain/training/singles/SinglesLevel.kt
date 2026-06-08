package ldv.shuuen.domain.training.singles

import kotlinx.serialization.Serializable
import ldv.shuuen.domain.audio.music.DegreeContext
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.training.TrainingScale
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class SinglesLevel(
  val id: String = Uuid.generateV7().toString(),
  val name: String,
  val traningScale: TrainingScale,
  val context: DegreeContext,
  val questionsNumber: Int?,
  val range: Pair<Note, Note>,
)