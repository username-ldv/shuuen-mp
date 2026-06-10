package ldv.shuuen.data.database.converters

import androidx.room3.TypeConverter
import ldv.shuuen.data.database.RoomJson
import ldv.shuuen.domain.training.context.ContextConfig

class ContextTypeConverter {
  @TypeConverter
  fun contextConfigToString(c: ContextConfig): String = RoomJson.encode(c)

  @TypeConverter
  fun stringtoContextConfig(s: String): ContextConfig = RoomJson.decode(s)
}