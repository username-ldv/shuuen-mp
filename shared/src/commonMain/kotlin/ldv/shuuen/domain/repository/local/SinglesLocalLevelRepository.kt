package ldv.shuuen.domain.repository.local

import kotlinx.coroutines.flow.Flow
import ldv.shuuen.common.ResponseState
import ldv.shuuen.domain.training.level.LevelSource
import ldv.shuuen.domain.training.singles.SinglesLevel

interface SinglesLocalLevelRepository {

  fun getLevels(): Flow<ResponseState<List<SinglesLevel>>>

  fun getLevelById(id: String): Flow<ResponseState<SinglesLevel>>

  suspend fun upsertLevel(level: SinglesLevel, source: LevelSource)
}