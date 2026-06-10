package ldv.shuuen.data.database.entity

import androidx.room3.Entity
import androidx.room3.PrimaryKey
import ldv.shuuen.domain.training.context.ContextConfig
import ldv.shuuen.domain.training.context.ContextSource

@Entity(tableName = "context")
data class ContextDbEntity(
  @PrimaryKey
  val id: String,
  val name: String?,
  val source: ContextSource,
  val config: ContextConfig
)