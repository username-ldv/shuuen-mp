package ldv.shuuen.ui.screens.training.single.setup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch

data class ScreenState(val questionsNumber: Int? = null, val range: Pair<Note, Note>)

class SinglesSetupViewModel : ViewModel() {
  private val _screenState =
    MutableStateFlow(
      ScreenState(
        questionsNumber = 20,
        range = Note(Pitch.C, 2) to Note(Pitch.C, 6)
      )
    )
  val screenState = _screenState.asStateFlow()

  fun changeQuestionsNumber(v: Int?) {
    _screenState.update { it.copy(questionsNumber = v) }
  }

  fun changeRangeStart(v: Note) {
    _screenState.update { it.copy(range = v to it.range.second) }
  }
  fun changeRangeEnd(v: Note) {
    _screenState.update { it.copy(range = it.range.first to v) }
  }
}