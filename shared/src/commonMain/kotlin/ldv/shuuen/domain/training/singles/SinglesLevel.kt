package ldv.shuuen.domain.training.singles

import kotlinx.serialization.Serializable
import ldv.shuuen.domain.audio.music.DegreeContext
import ldv.shuuen.domain.audio.music.NoteRange
import ldv.shuuen.domain.training.level.LevelConfig
import ldv.shuuen.domain.training.level.LevelSource
import kotlin.uuid.ExperimentalUuidApi

@Serializable
@OptIn(ExperimentalUuidApi::class)
data class SinglesLevel(
  val id: String,
  val name: String,
  val levelConfig: LevelConfig.Singles,
  val context: DegreeContext?,
  val source: LevelSource,
  val questionsNumber: Int?,
  val range: NoteRange
)