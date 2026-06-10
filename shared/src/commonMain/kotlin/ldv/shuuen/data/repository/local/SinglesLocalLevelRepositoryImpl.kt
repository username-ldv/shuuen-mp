package ldv.shuuen.data.repository.local

import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import ldv.shuuen.common.ResponseState
import ldv.shuuen.data.database.dao.SinglesLevelDao
import ldv.shuuen.data.database.entity.SinglesLevelDbEntity
import ldv.shuuen.domain.audio.music.toNoteRange
import ldv.shuuen.domain.repository.local.ContextLocalRepository
import ldv.shuuen.domain.repository.local.SinglesLocalLevelRepository
import ldv.shuuen.domain.training.TrainingScale
import ldv.shuuen.domain.training.TrainingScaleItemStates
import ldv.shuuen.domain.training.level.LevelConfig
import ldv.shuuen.domain.training.level.LevelSource
import ldv.shuuen.domain.training.level.ScaleConfig
import ldv.shuuen.domain.training.singles.SinglesLevel
import ldv.shuuen.ui.common.BoxedListItemState

class SinglesLocalLevelRepositoryImpl(
  private val singlesLevelDao: SinglesLevelDao,
  private val contextLocalRepository: ContextLocalRepository
) : SinglesLocalLevelRepository {
  override fun getLevels(): Flow<ResponseState<List<SinglesLevel>>> {
    return flow<ResponseState<List<SinglesLevel>>> {
      emit(ResponseState.Loading)
      Napier.v { "getting levels" }
      val entities = singlesLevelDao.getAll()
      Napier.v { "got entities, size: ${entities.size}" }
      emit(ResponseState.Success(entities.map { mapEntity(it) }))
    }.catch {
      emit(ResponseState.Error(it))
    }
  }

  override suspend fun getLevelById(id: String): Flow<ResponseState<SinglesLevel>> {
    return flow {
      emit(ResponseState.Loading)
      val response =
        singlesLevelDao.getById(id)?.let { mapEntity(it) } ?: error("level with id $id not found")
      emit(ResponseState.Success(response))
    }.catch {
      emit(ResponseState.Error(it))
    }
  }

  override suspend fun upsertLevel(level: SinglesLevel, source: LevelSource) {
    val firstScale =
      level.traningScales.firstOrNull() ?: error("No scales in the level ${level.id}")
    val levelConfig: LevelConfig.Singles = when (val itemStates = firstScale.itemStates) {
      is TrainingScaleItemStates.ByPitch -> {
        val pitchStates = itemStates.items.map {
          ScaleConfig.ScaleItemState.ScalePitchState(
            pitch = it.key, active = it.value.active
          )
        }
        val config = ScaleConfig.AbsoluteScaleConfig(
          root = firstScale.root!!, scaleType = firstScale.scaleType, pitchStates = pitchStates
        )
        LevelConfig.Singles.Absolute(scales = listOf(config), rotateEveryQuestions = null)
      }

      is TrainingScaleItemStates.ByDegree -> {
        val degreeStates = itemStates.items.map {
          ScaleConfig.ScaleItemState.ScaleDegreeState(
            degree = it.key, active = it.value.active
          )
        }
        val config = ScaleConfig.RelativeScaleConfig(
          scaleType = firstScale.scaleType, degreeStates = degreeStates
        )
        LevelConfig.Singles.Relative(config = config, rotateEveryQuestions = null)
      }
    }
    val entity = SinglesLevelDbEntity(
      id = level.id,
      name = level.name,
      config = levelConfig,
      source = source,
      questionsNumber = level.questionsNumber,
      range = level.range.toNoteRange()
    )
    singlesLevelDao.upsertLevel(entity)
  }

  private suspend fun mapEntity(entity: SinglesLevelDbEntity): SinglesLevel {
    val context = contextLocalRepository.getDegreeContextById("123") ?: error("shoudln't happen")
    val trainingScale: List<TrainingScale> = when (val c = entity.config) {
      is LevelConfig.Singles.Absolute -> {
        c.scales.map { scale ->
          val itemStates = TrainingScaleItemStates.ByPitch(items = scale.pitchStates.associate {
            it.pitch to BoxedListItemState(
              active = it.active, label = it.pitch.toString()
            )
          })
          TrainingScale(root = scale.root, itemStates = itemStates, scaleType = scale.scaleType)
        }
      }

      is LevelConfig.Singles.Relative -> {
        val itemStates = TrainingScaleItemStates.ByDegree(items = c.config.degreeStates.associate {
          it.degree to BoxedListItemState(
            active = it.active, label = it.degree.toString()
          )
        })
        listOf(TrainingScale(root = null, itemStates = itemStates, scaleType = c.config.scaleType))
      }
    }
    return SinglesLevel(
      id = entity.id,
      name = entity.name,
      traningScales = trainingScale,
      context = context,
      questionsNumber = entity.questionsNumber,
      range = entity.range.toPair()
    )
  }
}