package ldv.shuuen.data.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import ldv.shuuen.data.database.entity.ContextDbEntity

@Dao
interface ContextDao {
  @Query("select * from context where id = :id")
  suspend fun getById(id: String): ContextDbEntity?
}