package ldv.shuuen.data.database.entity

import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Index
import androidx.room3.PrimaryKey
import ldv.shuuen.domain.audio.music.NoteRange
import ldv.shuuen.domain.training.level.LevelConfig
import ldv.shuuen.domain.training.level.LevelSource

@Entity(
  tableName = "levels_singles", foreignKeys = [ForeignKey(
    entity = ContextDbEntity::class,
    parentColumns = ["id"],
    childColumns = ["contextId"],
    onDelete = ForeignKey.SET_NULL
  )], indices = [Index("contextId")]
)
data class SinglesLevelDbEntity(
  @PrimaryKey val id: String,
  val name: String,
  val config: LevelConfig.Singles,
  val contextId: String?,
  val source: LevelSource,
  val questionsNumber: Int?,
  val range: NoteRange
)