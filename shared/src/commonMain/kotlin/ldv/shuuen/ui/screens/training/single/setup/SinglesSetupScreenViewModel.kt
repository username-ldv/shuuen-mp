package ldv.shuuen.ui.screens.training.single.setup

import androidx.lifecycle.ViewModel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ldv.shuuen.domain.audio.music.Degree
import ldv.shuuen.domain.audio.music.DegreeContext
import ldv.shuuen.domain.audio.music.DegreeContextNode
import ldv.shuuen.domain.audio.music.DegreeWithOctave
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.ScaleType
import ldv.shuuen.domain.audio.music.Sustain
import ldv.shuuen.domain.repository.SettingsRepository
import ldv.shuuen.domain.training.TrainingScale
import ldv.shuuen.domain.training.TrainingScaleItemStates
import ldv.shuuen.domain.training.singles.SinglesLevel

class SinglesSetupScreenViewModel(val settingsRepository: SettingsRepository) : ViewModel() {
  private val _singlesLevelState = MutableStateFlow(
    SinglesLevel(
      name = "",
      traningScale = TrainingScale.degreesFromType(ScaleType.Major),
      context = DegreeContext(
        listOf(DegreeContextNode(listOf(DegreeWithOctave(Degree.D1, 2)), null, Sustain.Endless)),
        setupMelody = listOf(
          Degree.D1,
          Degree.D3,
          Degree.D5,
          Degree.D1,
        )
      ),
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
    _singlesLevelState.update { it.copy(traningScale = t) }
  }

  suspend fun upsertLevel() {
    val level = screenState.value
    // todo: what should be the default name?
    val levelName = when (level.traningScale.itemStates) {
      is TrainingScaleItemStates.ByPitch -> {
        level.traningScale.itemStates.items.entries.first().value.label
      }

      is TrainingScaleItemStates.ByDegree -> "Random"

    }
    settingsRepository.upsertLevel(level.copy(name = levelName))
    Napier.v { "Saved new level: $level" }
  }

  override fun onCleared() {
    Napier.v { "Setup screen viewmodel cleared?" }
  }
}