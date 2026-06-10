package ldv.shuuen.ui.screens.training.single.setup

import androidx.lifecycle.ViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.ScaleType
import ldv.shuuen.domain.audio.music.defaultContext
import ldv.shuuen.domain.repository.local.SinglesLocalLevelRepository
import ldv.shuuen.domain.training.TrainingScale
import ldv.shuuen.domain.training.TrainingScaleItemStates
import ldv.shuuen.domain.training.level.LevelSource
import ldv.shuuen.domain.training.singles.SinglesLevel

class SinglesSetupScreenViewModel(
  val levelRepository: SinglesLocalLevelRepository
) : ViewModel() {
  private val _singlesLevelState = MutableStateFlow(
    SinglesLevel(
      name = "",
      traningScales = listOf(TrainingScale.degreesFromType(ScaleType.Major)),
      context = defaultContext.copy(),
      questionsNumber = 20,
      range = Note(Pitch.C, 2) to Note(Pitch.C, 6),
    )
  )
  val screenState = _singlesLevelState.asStateFlow()

  fun changeQuestionsNumber(v: Int?) {
    _singlesLevelState.update { it.copy(questionsNumber = v) }
  }

  fun changeRangeStart(v: Note) {
    _singlesLevelState.update { it.copy(range = v to it.range.second) }
  }

  fun changeRangeEnd(v: Note) {
    _singlesLevelState.update { it.copy(range = it.range.first to v) }
  }

  fun changeScale(t: TrainingScale) {
    _singlesLevelState.update { it.copy(traningScales = listOf(t)) }
  }

  suspend fun upsertLevel() {
    val level = screenState.value
    val firstScale = level.traningScales.first()
    // todo: what should be the default name?
    val levelName = when (val itemStates = firstScale.itemStates) {
      is TrainingScaleItemStates.ByPitch -> {
        itemStates.items.entries.first().value.label
      }

      is TrainingScaleItemStates.ByDegree -> "Random"

    }
    levelRepository.upsertLevel(level.copy(name = levelName), source = LevelSource.User)
    Napier.v { "Saved new level: $level" }
  }

  override fun onCleared() {
    Napier.v { "Setup screen viewmodel cleared?" }
  }
}