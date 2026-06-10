package ldv.shuuen.data.repository.local

import ldv.shuuen.domain.audio.music.DegreeContext
import ldv.shuuen.domain.audio.music.defaultContext
import ldv.shuuen.domain.repository.local.ContextLocalRepository

class ContextLocalRepositoryImpl : ContextLocalRepository {
  override suspend fun getDegreeContextById(id: String): DegreeContext? {
    return defaultContext
  }
}