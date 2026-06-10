package ldv.shuuen.data.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import ldv.shuuen.domain.audio.music.NoteRange
import ldv.shuuen.domain.training.level.LevelConfig
import ldv.shuuen.domain.training.level.LevelSource

@Entity(tableName = "singles_levels")
data class SinglesLevelDbEntity(
  @PrimaryKey
  val id: String,
  val name: String,
  val config: LevelConfig.Singles,
  val source: LevelSource,
  val questionsNumber: Int?,
  val range: NoteRange
)