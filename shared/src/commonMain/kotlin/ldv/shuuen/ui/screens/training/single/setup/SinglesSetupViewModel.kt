package ldv.shuuen.ui.screens.training.single.setup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.Scale
import ldv.shuuen.ui.common.music.PitchState

data class PitchOrRandom(val pitcha: Pitch?)

data class TrainingScale(val pitchStates: Map<Pitch, PitchState>) {
  companion object {
    fun fromScale(s: Scale): TrainingScale {
      val names = s.appropriatePitchNames()
      return TrainingScale(pitchStates = s.pitches.zip(names).map { (pitch, name) ->
        pitch to PitchState(true, name)
      }.toMap())
    }
  }
}

data class SaveableScreenState(
  val questionsNumber: Int?, val range: Pair<Note, Note>, val traningScale: TrainingScale
)

class SinglesSetupViewModel : ViewModel() {
  private val _saveableScreenState = MutableStateFlow(
    SaveableScreenState(
      questionsNumber = 20,
      range = Note(Pitch.C, 2) to Note(Pitch.C, 6),
      traningScale = TrainingScale.fromScale(Scale.major(Pitch.C))
    )
  )
  val screenState = _saveableScreenState.asStateFlow()

  fun changeQuestionsNumber(v: Int?) {
    _saveableScreenState.update { it.copy(questionsNumber = v) }
  }

  fun changeRangeStart(v: Note) {
    _saveableScreenState.update { it.copy(range = v to it.range.second) }
  }

  fun changeRangeEnd(v: Note) {
    _saveableScreenState.update { it.copy(range = it.range.first to v) }
  }

  fun changeScale(t: TrainingScale) {
    _saveableScreenState.update { it.copy(traningScale = t) }
  }
}