package ldv.shuuen.data.database.dao

import androidx.room3.Dao
import androidx.room3.Query
import androidx.room3.Upsert
import ldv.shuuen.data.database.entity.SinglesLevelDbEntity

@Dao
interface SinglesLevelDao {
  @Query("select * from singles_levels")
  suspend fun getAll(): List<SinglesLevelDbEntity>

  @Query("select * from singles_levels where id = :id")
  suspend fun getById(id: String): SinglesLevelDbEntity?

  @Upsert
  suspend fun upsertLevel(level: SinglesLevelDbEntity)
}