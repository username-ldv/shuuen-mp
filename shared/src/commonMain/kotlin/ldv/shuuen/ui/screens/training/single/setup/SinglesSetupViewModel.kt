package ldv.shuuen.ui.screens.training.single.setup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import ldv.shuuen.domain.audio.music.Degree
import ldv.shuuen.domain.audio.music.Note
import ldv.shuuen.domain.audio.music.Pitch
import ldv.shuuen.domain.audio.music.Scale
import ldv.shuuen.domain.audio.music.ScaleType
import ldv.shuuen.ui.common.music.BoxedListItemState

enum class TrainingScaleType {
  Relative, Absolute
}

sealed interface TrainingItemStates {
  val items: Map<*, BoxedListItemState>

  data class ByPitch(
    override val items: Map<Pitch, BoxedListItemState>
  ) : TrainingItemStates

  data class ByDegree(
    override val items: Map<Degree, BoxedListItemState>
  ) : TrainingItemStates
}

data class TrainingScale(val root: Pitch?, val itemStates: TrainingItemStates) {
  val type = if (root != null) TrainingScaleType.Absolute else TrainingScaleType.Relative

  companion object {
    fun fromScale(s: Scale): TrainingScale {
      val names = s.appropriatePitchNames()
      return TrainingScale(
        root = s.root,
        itemStates = TrainingItemStates.ByPitch(s.pitches.zip(names).associate { (pitch, name) ->
          pitch to BoxedListItemState(true, name)
        })
      )
    }

    fun degreesFromType(m: ScaleType, formula: List<Int>? = null): TrainingScale {
      val sampleScale = Scale.fromScaleType(Pitch.C, m, formula ?: listOf(0))
      return TrainingScale(
        root = null,
        itemStates = TrainingItemStates.ByDegree(
          sampleScale.degrees.associateWith { BoxedListItemState(true, it.label) })
      )
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
      traningScale = TrainingScale.degreesFromType(ScaleType.Major)
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